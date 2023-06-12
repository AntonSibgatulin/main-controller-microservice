package jp.konosuba.include.contacts

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional


@Repository
interface ContactsRepository : JpaRepository<Contacts,Long> {

}