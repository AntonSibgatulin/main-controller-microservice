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


class Contacts {
    @Id

    var id: Long? = null
    var name: String? = null
    var phone: String? = null
    var email: String? = null
    var relative: Boolean? = false
    var tg: Boolean? = false
    var vk: Boolean? = false
    var ws //whatsapp
            : Boolean? = false

    constructor()
    constructor(
        name: String?,
        phone: String?,
        email: String?,
        relative: Boolean?,
        tg: Boolean?,
        vk: Boolean?,
        ws: Boolean?
    ) {

        this.id = -1
        this.name = name
        this.phone = phone
        this.email = email
        this.relative = relative
        this.tg = tg
        this.vk = vk
        this.ws = ws
    }




    override fun toString(): String {
        return "Contacts(id=$id, name=$name, phone=$phone, email=$email, relative=$relative, tg=$tg, vk=$vk, ws=$ws)"
    }


}
