package jp.konosuba.include.messages

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageActionRepository : JpaRepository<MessageAction,Long> {
}