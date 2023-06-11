package jp.konosuba.include.cron

import jp.konosuba.include.contacts.Contacts
import lombok.Data
import lombok.NoArgsConstructor
import javax.persistence.*

@Entity
@Data
@NoArgsConstructor
class Cron {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var userId: Long? = null
    var codeFine: Int? = null

    @Column(columnDefinition = "VARCHAR(4096)")
    var message: String? = null

    var http: String? = null

    @ManyToMany
    @JoinTable
    var contacts: List<Contacts> = mutableListOf()

    //@Enumerated
    var cronType: CronType? = null
    override fun toString(): String {
        return "Cron(id=$id, userId=$userId, codeFine=$codeFine, message=$message, http=$http, contacts=$contacts, cronType=$cronType)"
    }


}