package jp.konosuba.include.contacts

import org.springframework.data.jpa.repository.JpaRepository

interface ContactsRepository : JpaRepository<Contacts,Long> {
}