package jp.konosuba.utils


import com.fasterxml.jackson.databind.ObjectMapper
import jp.konosuba.include.cron.Cron
import jp.konosuba.include.messages.MessageAction

class ClassUtils {
    companion object {
        fun <T : Any> fromObjectToJson(objects: T): String {
            var objectMapper: ObjectMapper = ObjectMapper()
            return objectMapper.writeValueAsString(objects)
        }
        fun fromJsonToMessageAction(data:String):MessageAction{
            val objectMapper = ObjectMapper()
            return objectMapper.readValue(data,MessageAction::class.java)
        }
        fun fromJsonToCron(data:String):Cron{
            val objectMapper = ObjectMapper()
            return objectMapper.readValue(data,Cron::class.java)
        }
    }


}