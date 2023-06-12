package jp.konosuba.include.messages.entity;

import jp.konosuba.include.contacts.Contacts;
import jp.konosuba.include.messages.MessageAction;
import jp.konosuba.include.messages.MessageActionRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public class MessageActionRepositoryImpl extends SimpleJpaRepository<MessageAction,Long> implements MessageActionRepository {


    private final EntityManagerFactory entityManagerFactory;

    public MessageActionRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        super(MessageAction.class,entityManagerFactory.createEntityManager());
        this.entityManagerFactory = entityManagerFactory;
    }

    @Transactional
    @Override
    public <S extends MessageAction> S save(S entity) {
       var entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(entity.getContacts());
            entityManager.persist(entity);
            transaction.commit();
            entityManager.close();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
        return (entity);


    }

}
