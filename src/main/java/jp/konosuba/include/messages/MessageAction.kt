package jp.konosuba.include.messages

import jp.konosuba.include.contacts.Contacts
import lombok.Data
import lombok.NoArgsConstructor
import javax.persistence.*


@NoArgsConstructor
@Data
@Entity
class MessageAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var userId: Long? = null
    var messageId: String? = null

    @ManyToOne
    var contacts: Contacts? = null

    @Enumerated
    var messageType: MessageType? = null

    var time: Long? = null

}
