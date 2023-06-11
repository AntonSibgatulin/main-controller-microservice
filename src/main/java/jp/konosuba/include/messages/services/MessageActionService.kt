package jp.konosuba.include.messages.services

import jp.konosuba.include.messages.MessageAction
import jp.konosuba.include.messages.MessageActionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
open class MessageActionService(private val messageActionRepository: MessageActionRepository) {


    @Transactional
    open fun saveAll(arr:ArrayList<MessageAction>){
        messageActionRepository.saveAll(arr);
    }

}