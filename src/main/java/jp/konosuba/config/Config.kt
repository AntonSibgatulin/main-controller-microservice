package jp.konosuba.config

import lombok.Data
import lombok.Getter
import lombok.Setter

@Getter
@Setter


@Data
class Config {
    var countThreadWriteInKafka: Int = 10
    lateinit var topicMainControllerReader: String

    lateinit var private_topic: String
    lateinit var public_topic:String

    lateinit var kafka_host: String
    lateinit var kafka_port: String
    lateinit var groupId: String
    var duration_of_poll:Long = 100
    lateinit var redis_host:String
    lateinit var redis_port:String

}
