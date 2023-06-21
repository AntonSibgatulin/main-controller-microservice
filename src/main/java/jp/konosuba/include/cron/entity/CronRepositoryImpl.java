package jp.konosuba.include.cron.entity;

import jp.konosuba.include.contacts.Contacts;
import jp.konosuba.include.cron.Cron;
import jp.konosuba.include.cron.CronRepository;
import jp.konosuba.include.cron.CronType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public class CronRepositoryImpl extends SimpleJpaRepository<Cron,Long> implements CronRepository {

    private final EntityManagerFactory entityManagerFactory;

    public CronRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        super(Cron.class,entityManagerFactory.createEntityManager());
        this.entityManagerFactory = entityManagerFactory;
    }




    @Override
    public Cron getCronByIdAndUserId(long id, long userId) {
        var entityManager = entityManagerFactory.createEntityManager();
        var builder = entityManager.getCriteriaBuilder();
        var query = builder.createQuery(Cron.class);

        var root = query.from(Cron.class);
        query.select(root).where(builder.and(builder.equal(root.get("id"), id), builder.equal(root.get("userId"), userId)));

        var typedQuery = entityManager.createQuery(query);
        return (typedQuery.getSingleResult());
    }


    @Override
    public Cron getCronById(long id) {
        var entityManager = entityManagerFactory.createEntityManager();
        var builder = entityManager.getCriteriaBuilder();
        var query = builder.createQuery(Cron.class);

        var root = query.from(Cron.class);
        query.select(root).where(builder.and(builder.equal(root.get("id"), id)));

        var typedQuery = entityManager.createQuery(query);
        var listResult = typedQuery.getResultList();
        if(listResult.size()==0){
            return null;
        }
        return Optional.ofNullable(typedQuery.getSingleResult()).get();


    }


    @Override
    public List<Cron> getCronByCronType(@NotNull CronType type) {
        var entityManager = entityManagerFactory.createEntityManager();
        var builder = entityManager.getCriteriaBuilder();
        var query = builder.createQuery(Cron.class);

        var root = query.from(Cron.class);
        query.select(root).where(builder.and(builder.equal(root.get("cronType"), type)));

        var typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();

    }

    @Transactional
    @Override
    public <S extends Cron> S save(S entity) {
        var entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
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


    @Override
    public void deleteById(Long aLong) {

        var entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        var cron = entityManager.find(Cron.class,aLong);
        if(cron==null)return;
        cron.getContacts().clear();
        try {
            transaction.begin();
            /*if(!entityManager.contains(cron)){
                entityManager.merge(cron);
            }

             */
            entityManager.remove(cron);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            //transaction.rollback();
            throw e;
        }
    }
}
