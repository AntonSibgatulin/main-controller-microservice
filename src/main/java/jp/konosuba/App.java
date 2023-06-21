package jp.konosuba;


import com.sun.tools.javac.Main;
import jp.konosuba.config.Config;
import jp.konosuba.controller.MainController;
import jp.konosuba.include.contacts.Contacts;
import jp.konosuba.include.contacts.ContactsRepository;
import jp.konosuba.include.contacts.entity.ContactsReposytoryImpl;
import jp.konosuba.include.contacts.service.ContactsService;
import jp.konosuba.include.cron.Cron;
import jp.konosuba.include.cron.CronRepository;
import jp.konosuba.include.cron.entity.CronRepositoryImpl;
import jp.konosuba.include.cron.services.CronService;
import jp.konosuba.include.messages.MessageAction;
import jp.konosuba.include.messages.MessageActionRepository;
import jp.konosuba.include.messages.MessageType;
import jp.konosuba.include.messages.entity.MessageActionRepositoryImpl;
import jp.konosuba.include.messages.services.MessageActionService;
import jp.konosuba.utils.ClassUtils;
import org.json.JSONObject;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import redis.clients.jedis.Jedis;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.Transactional;
import java.io.*;

/**
 * Hello world!
 */
@EnableJpaRepositories(basePackages = "jp.konosuba")
@EnableTransactionManagement
public class App {
    public static Config config =new Config();;

    public static void main(String[] args) {
        loadConfig("configure/config.json");
        var jedis = new Jedis(config.redis_host,Integer.valueOf(config.redis_port));
        //var context = new AnnotationConfigApplicationContext(AppConfig.class);
        //var cronService = context.getBean(CronService.class);
       // var messageActionService = context.getBean(MessageActionService.class);
       // var mainController = new MainController(config,jedis,messageActionService,cronService);

        //AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        //var cronService = context.getBean(CronService.class);
        // EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("postgresPU");
       // EntityManager entityManager = entityManagerFactory.createEntityManager();
        var entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit");
        var entityManager = entityManagerFactory.createEntityManager();
        var cronRepository = new CronRepositoryImpl(entityManagerFactory);
        var cronService = new CronService(cronRepository);

        var contactsRepository = new ContactsReposytoryImpl(entityManagerFactory);
        var contactsService = new ContactsService(contactsRepository);

        var messageActionRepository = new MessageActionRepositoryImpl(entityManagerFactory);
        var messageActionService = new MessageActionService(messageActionRepository);

        var messageAction = new MessageAction();

        var contacts = new Contacts();
        //contacts.setId(1L);
       /* contacts.setEmail("test@test");
        contacts.setName("Name");
        contacts.setPhone("12344364563");

        contactsRepository.save(contacts);

        */

        messageAction.setMessageType(MessageType.OK);
        //messageAction.setId(11L);
        messageAction.setMessageId("hashId#1");
        messageAction.setContacts(null);
        messageAction.setTime(System.currentTimeMillis());
        messageAction.setUserId(2L);
        /*for(int i =0;i<100;i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    messageActionRepository.save(messageAction);
                }
            }).start();

        }

         */
        //messageActionRepository.save(messageAction);

        System.out.println(ClassUtils.fromObjectToJson(messageAction));
        //System.out.println(ClassUtils.fromObjectToJson(contactsRepository.getById(1L)));



        var mainController = new MainController(config,jedis,messageActionService,cronService,contactsRepository);



       // System.out.println("User ID: " + cron.toString());

       // entityManager.close();
       // entityManagerFactory.close();

    }

    private static JSONObject loadFile(String path) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path)));
            String all = "";
            String pie = null;
            while ((pie = bufferedReader.readLine()) != null) {
                all += pie;
            }
            System.out.println(all);
            return new JSONObject(all);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void loadConfig(String path) {
        JSONObject jsonObject = loadFile(path);
        config.setTopicMainControllerReader(jsonObject.getString("topicMainControllerReader"));
        config.setPrivate_topic(jsonObject.getString("private_topic"));
        config.setPublic_topic(jsonObject.getString("public_topic"));
        config.setKafka_host(jsonObject.getString("kafka_host"));
        config.setKafka_port(jsonObject.getString("kafka_port"));
        config.setGroupId(jsonObject.getString("groupId"));
        config.setDuration_of_poll(jsonObject.getLong("duration_of_poll"));
        config.setRedis_host(jsonObject.getString("redis_host"));
        config.setRedis_port(jsonObject.getString("redis_port"));
        config.setCountThreadWriteInKafka(jsonObject.getInt("countThreadWriteInKafka"));
    }
}
