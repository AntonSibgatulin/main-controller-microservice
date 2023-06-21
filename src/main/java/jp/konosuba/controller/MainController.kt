package jp.konosuba.controller

import jp.konosuba.App
import jp.konosuba.config.Config
import jp.konosuba.include.contacts.ContactsRepository
import jp.konosuba.include.cron.Cron
import jp.konosuba.include.cron.CronType
import jp.konosuba.include.cron.services.CronService
import jp.konosuba.include.messages.MessageAction
import jp.konosuba.include.messages.MessageType
import jp.konosuba.include.messages.services.MessageActionService
import jp.konosuba.utils.ClassUtils
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.common.serialization.StringDeserializer
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis
import java.time.Duration
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue


class MainController(
    config: Config,
    jedis: Jedis,
    messageActionService: MessageActionService,
    cronService: CronService,
    contactsRepository: ContactsRepository
) : Thread() {
    private val log = LoggerFactory.getLogger(App::class.java)

    private val maxLengthOfArray = 1

    private lateinit var consumer: KafkaConsumer<String, String>
    private lateinit var producer: KafkaProducer<String?, String>

    private val config: Config
    private val jedis: Jedis
    private val messageActionService: MessageActionService
    private val cronService: CronService
    private val contactsRepository:ContactsRepository

    private val okList: ArrayList<MessageAction> = ArrayList()
    private val errorList: ArrayList<MessageAction> = ArrayList()


    private val listPrintInKafka: LinkedBlockingQueue<Cron> = LinkedBlockingQueue()
    private var executeService: ExecutorService? = null


    private var thread_5_min = Thread(Runnable {
        while (true) {
            Thread.sleep(1000 * 30 /* * 60 * 5*/)
            var crons = cronService.getCronsByTypeCron(CronType.MINUTES_5)
             for (cron in crons) {
                pushTaskInRedis(ClassUtils.fromObjectToJson(cron))
            }
        }
    })
    private var thread_15_min = Thread(Runnable {
        while (true) {
            Thread.sleep(1000 * 60 * 15)
            var crons = cronService.getCronsByTypeCron(CronType.MINUTES_15)
            for (cron in crons) {
                pushTaskInRedis(ClassUtils.fromObjectToJson(cron))
            }
        }
    })
    private var thread_30_min = Thread(Runnable {
        while (true) {
            Thread.sleep(1000 * 60 * 30)
            var crons = cronService.getCronsByTypeCron(CronType.MINUTES_30)
            for (cron in crons) {
                pushTaskInRedis(ClassUtils.fromObjectToJson(cron))
            }
        }
    })
    private var thread_60_min = Thread(Runnable {
        while (true) {
            Thread.sleep(1000 * 60 * 60)
            var crons = cronService.getCronsByTypeCron(CronType.HOUR)
            for (cron in crons) {
                pushTaskInRedis(ClassUtils.fromObjectToJson(cron))
            }
        }
    })
    private var thread_3_hours = Thread(Runnable {
        while (true) {
            Thread.sleep(1000 * 60 * 60 * 3)
            var crons = cronService.getCronsByTypeCron(CronType.HOURS_3)
            for (cron in crons) {
                pushTaskInRedis(ClassUtils.fromObjectToJson(cron))
            }
        }
    })
    private var thread_6_hours = Thread(Runnable {
        while (true) {
            Thread.sleep(1000 * 60 * 60 * 6)
            var crons = cronService.getCronsByTypeCron(CronType.HOURS_6)
            for (cron in crons) {
                pushTaskInRedis(ClassUtils.fromObjectToJson(cron))
            }
        }
    })
    private var thread_12_hours = Thread(Runnable {
        while (true) {
            Thread.sleep(1000 * 60 * 60 * 12)
            var crons = cronService.getCronsByTypeCron(CronType.HOURS_12)
            for (cron in crons) {
                pushTaskInRedis(ClassUtils.fromObjectToJson(cron))
            }
        }
    })
    private var thread_24_hours = Thread(Runnable {
        while (true) {
            Thread.sleep(1000 * 60 * 60 * 24)
            var crons = cronService.getCronsByTypeCron(CronType.HOURS_24)
            for (cron in crons) {
                pushTaskInRedis(ClassUtils.fromObjectToJson(cron))
            }
        }
    })


    private var thread_to_doing_queue = Thread(Runnable {
        while (true) {
            if (!listPrintInKafka.isEmpty()) {
                var cron = listPrintInKafka.poll()
                if (cron != null) {
                    var runnable = Runnable {
                        var messageId = "hash" + System.currentTimeMillis();
                        if(cron.message == null)
                            return@Runnable
                        jedis.set(messageId, cron.message)
                        var json = JSONObject()
                        json.put("typeOperation", "notification-api")
                        json.put("type", "cronError")
                        json.put("userId", cron.userId)
                        json.put("cronId", cron.id)
                        sendMessageInKafkaPublic(json.toString())


                        for (x in 0..(cron.contacts.size)-1) {
                            val jsonObject = JSONObject()
                            jsonObject.put("typeOperation", "send")
                            jsonObject.put(
                                "contact",
                                JSONObject(ClassUtils.fromObjectToJson(cron.contacts[x]))
                            )
                            jsonObject.put("messageId", messageId)
                            jsonObject.put("type", "email")
                            jsonObject.put("userId", cron.userId)
                            sendMessageInKafkaPublic(jsonObject.toString())
                        }
                        val jsonObject = JSONObject()
                        jsonObject.put("typeOperation", "end_send")
                        jsonObject.put("messageId", messageId)
                        jsonObject.put("userId", cron.userId)
                        sendMessageInKafkaPublic(jsonObject.toString())
                    }
                    executeService?.execute(runnable)
                }
            }
        }
    })


    init {
        this.config = config
        this.jedis = jedis
        this.messageActionService = messageActionService
        this.cronService = cronService
        this.contactsRepository = contactsRepository
        executeService = Executors.newFixedThreadPool(config.countThreadWriteInKafka)

        val props = Properties()
        props["bootstrap.servers"] = App.config.kafka_host + ":" + App.config.kafka_port
        props["acks"] = "all"
        props["retries"] = 0
        props["batch.size"] = 16384
        props["linger.ms"] = 1
        props["buffer.memory"] = 33554432
        props["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        props["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"

        producer = KafkaProducer(props)


        val bootstrapServers = config.kafka_host + ":" + config.kafka_port
        val groupId = config.groupId
        val topic: String = config.topicMainControllerReader


        val properties = Properties()
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId)
        //properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        //properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "5")
        properties.setProperty(
            ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
            "org.apache.kafka.clients.consumer.RoundRobinAssignor"
        )



        consumer = KafkaConsumer(properties)
        consumer.subscribe(listOf(topic))

        thread_to_doing_queue.start()
        start()


        thread_5_min.start()
        thread_15_min.start()
        thread_30_min.start()
        thread_60_min.start()
        thread_3_hours.start()
        thread_6_hours.start()
        thread_12_hours.start()
        thread_24_hours.start()

    }


    private fun execute(message: String) {
        val jsonObjects = JSONObject(message)
        var type = jsonObjects.getString("typeOperation")
        if (type.equals("email_ok")) {

            var messageAction: MessageAction = ClassUtils.fromJsonToMessageAction(jsonObjects.getJSONObject("messageAction").toString())
            messageAction.messageType = MessageType.OK
            messageAction.time = System.currentTimeMillis()
            okList.add(messageAction);

            //check size list,if size of list more then const then list saved and cleared
            if (okList.size >= maxLengthOfArray) {
                var cloneList = okList.clone()
                Thread(Runnable {
                    this.messageActionService.saveAll(cloneList as ArrayList<MessageAction>)
                }).start()

                okList.clear()
            }

        } else if (type.equals("email_error")) {

            var messageAction: MessageAction = ClassUtils.fromJsonToMessageAction(jsonObjects.getJSONObject("messageAction").toString())
            messageAction.messageType = MessageType.ERROR
            messageAction.time = System.currentTimeMillis()
            errorList.add(messageAction);
            if (errorList.size >= maxLengthOfArray) {
                Thread(Runnable {
                    this.messageActionService.saveAll(errorList.clone() as ArrayList<MessageAction>)
                }).start()

                errorList.clear()
            }
            var json = JSONObject(ClassUtils.fromObjectToJson(messageAction))
            json.put("typeOperation","send")


            sendMessageInKafkaPublic(json.toString());

        } else if (type.equals("end_send_confirm")) {
            sendMessageInKafkaPrivate(message)
        } else if (type.equals("new_cron") || type.equals("edit_cron")) {
            var cron: Cron = ClassUtils.fromJsonToCron(jsonObjects.getJSONObject("cron").toString())

            if(type.equals("edit_cron")){
                //cronService.update(cron)
                return;
            }
            try {
                contactsRepository.saveAll(cron.contacts);
            }catch (e:Exception){
                //ignore
            }

            cronService.save(cron)

        } else if (type.equals("delete_cron")) {
            var id = jsonObjects.getLong("cronId")
            cronService.deleteById(id)
        } else if (type.equals("error_cron")) {
            var id = jsonObjects.getLong("cronId")
            var cron = cronService.getCron(id)
            if (cron != null) {
                listPrintInKafka.offer(cron)
            }
        }

    }

    private fun sendMessageInKafkaPublic(message: String) {
        val producerRecord = ProducerRecord<String?, String>(config.public_topic, null, message)
        producer!!.send(producerRecord)
    }

    private fun sendMessageInKafkaPrivate(message: String) {
        val producerRecord = ProducerRecord<String?, String>(config.private_topic, null, message)
        producer!!.send(producerRecord)
    }


    override fun run() {

        try {
            while (true) {
                val records: ConsumerRecords<String?, String?> =
                    consumer.poll(Duration.ofMillis(App.config.duration_of_poll))
                for (record in records) {
                    val message = record.value()
                    println(message)
                    try {
                        if (message != null) {
                            execute(message)
                        };
                    }catch (e:Exception){
                        println(e)
                    }

                }
            }
        } catch (e: WakeupException) {
            println("Wake up exception!")
            // we ignore this as this is an expected exception when closing a consumer
        } catch (e: Exception) {
            e.printStackTrace()
            println("Unexpected exception $e")
        } finally {
            consumer.close() // this will also commit the offsets if need be.
            println("The consumer is now gracefully closed.")
        }

    }

    private fun pushTaskInRedis(message: String) {
        jedis.lpush("task#main", message)
    }

    private fun clearTaskInRedis() {
        jedis.del("task#main")
    }
}