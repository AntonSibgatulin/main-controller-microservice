package jp.konosuba.include.contacts

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
class Contacts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var name: String? = null
    var phone: String? = null
    var email: String? = null
    var relative: Boolean? = null
    var tg: Boolean? = null
    var vk: Boolean? = null
    var ws //whatsapp
            : Boolean? = null
}
