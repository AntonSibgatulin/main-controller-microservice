package jp.konosuba.include.messages

import jp.konosuba.include.contacts.Contacts
import lombok.Data
import lombok.NoArgsConstructor
import javax.persistence.*
import kotlin.jvm.Transient


@NoArgsConstructor
@Data
@Entity
class MessageAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var userId: Long? = null
    var messageId: String? = null

    @ManyToOne(cascade = arrayOf(CascadeType.MERGE))
    var contacts: Contacts? = null

    @Enumerated(EnumType.STRING)
    var messageType: MessageType? = null

    var time: Long? = null
    @Transient var lastOne:Boolean?=null
    override fun toString(): String {
        return "MessageAction(id=$id, userId=$userId, messageId=$messageId, contacts=$contacts, messageType=$messageType, time=$time)"
    }


}
