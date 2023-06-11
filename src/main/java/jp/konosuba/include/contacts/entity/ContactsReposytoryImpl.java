package jp.konosuba.include.contacts.entity;

import jp.konosuba.include.contacts.Contacts;
import jp.konosuba.include.contacts.ContactsRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class ContactsReposytoryImpl extends SimpleJpaRepository<Contacts,Long> implements ContactsRepository {

    private final EntityManager entityManager;

    public ContactsReposytoryImpl(EntityManager entityManager) {
        super(Contacts.class,entityManager);
        this.entityManager = entityManager;
    }



}
