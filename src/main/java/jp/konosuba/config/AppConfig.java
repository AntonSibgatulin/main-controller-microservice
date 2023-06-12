package jp.konosuba.config;


import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
public class AppConfig {
    @PersistenceContext
    EntityManager entityManager;



}

 /*
public class AppConfig {
}


  */

