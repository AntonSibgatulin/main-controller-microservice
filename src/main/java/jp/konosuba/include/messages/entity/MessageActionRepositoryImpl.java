package jp.konosuba.include.messages.entity;

import jp.konosuba.include.messages.MessageAction;
import jp.konosuba.include.messages.MessageActionRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class MessageActionRepositoryImpl extends SimpleJpaRepository<MessageAction,Long> implements MessageActionRepository {

    private final EntityManager entityManager;

    public MessageActionRepositoryImpl(EntityManager entityManager) {
        super(MessageAction.class,entityManager);
        this.entityManager = entityManager;
    }

}
