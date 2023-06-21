package jp.konosuba.include.cron.services

import jp.konosuba.include.cron.Cron
import jp.konosuba.include.cron.CronRepository
import jp.konosuba.include.cron.CronType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
open class CronService(private val cronRepository: CronRepository) {


    @Transactional
    open fun save(cron: Cron): Cron {
        return cronRepository.save(cron)
    }

    @Transactional
    open fun getCronByIdAndUserId(id: Long, userId: Long): Cron {
        return cronRepository.getCronByIdAndUserId(id, userId)
    }

    @Transactional
    open fun getCron(id: Long): Cron? {
        return cronRepository.getCronById(id)
    }

    @Transactional
    open fun delete(id: Long) {
        cronRepository.deleteById(id)
    }

    @Transactional
    open fun getCronsByTypeCron(cronType: CronType): MutableList<Cron> {
        return cronRepository.getCronByCronType(cronType)
    }

    fun deleteById(id: Long) {
        cronRepository.deleteById(id);

    }


}