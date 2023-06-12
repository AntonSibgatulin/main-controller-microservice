package jp.konosuba.include.contacts.entity;

import jp.konosuba.include.contacts.Contacts;
import jp.konosuba.include.contacts.ContactsRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.transaction.Transactional;


@Transactional
@Repository
public class ContactsReposytoryImpl extends SimpleJpaRepository<Contacts, Long> implements ContactsRepository {

    private final EntityManagerFactory entityManagerFactory;

    public ContactsReposytoryImpl(EntityManagerFactory entityManagerFactory) {
        super(Contacts.class, entityManagerFactory.createEntityManager());

        this.entityManagerFactory = entityManagerFactory;
    }

    @Transactional
    @Override
    public <S extends Contacts> S save(S entity) {
        var entityManager = entityManagerFactory.createEntityManager();
        var transaction = entityManager.getTransaction();
        try {
            transaction.begin();
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
