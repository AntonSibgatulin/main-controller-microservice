package jp.konosuba.include.cron.entity;

import jp.konosuba.include.cron.Cron;
import jp.konosuba.include.cron.CronRepository;
import jp.konosuba.include.cron.CronType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public class CronRepositoryImpl extends SimpleJpaRepository<Cron,Long> implements CronRepository {

    private final EntityManager entityManager;

    public CronRepositoryImpl(EntityManager entityManager) {
        super(Cron.class,entityManager);
        this.entityManager = entityManager;
    }




    @Override
    public Cron getCronByIdAndUserId(long id, long userId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Cron> query = builder.createQuery(Cron.class);

        Root<Cron> root = query.from(Cron.class);
        query.select(root).where(builder.and(builder.equal(root.get("id"), id), builder.equal(root.get("userId"), userId)));

        TypedQuery<Cron> typedQuery = entityManager.createQuery(query);
        return (typedQuery.getSingleResult());
    }


    @Override
    public Cron getCronById(long id) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Cron> query = builder.createQuery(Cron.class);

        Root<Cron> root = query.from(Cron.class);
        query.select(root).where(builder.and(builder.equal(root.get("id"), id)));

        TypedQuery<Cron> typedQuery = entityManager.createQuery(query);
        List<Cron> listResult = typedQuery.getResultList();
        if(listResult.size()==0){
            return null;
        }
        return Optional.ofNullable(typedQuery.getSingleResult()).get();


    }


    @Override
    public List<Cron> getCronByCronType(@NotNull CronType type) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Cron> query = builder.createQuery(Cron.class);

        Root<Cron> root = query.from(Cron.class);
        query.select(root).where(builder.and(builder.equal(root.get("cronType"), type)));

        TypedQuery<Cron> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();

    }
}
