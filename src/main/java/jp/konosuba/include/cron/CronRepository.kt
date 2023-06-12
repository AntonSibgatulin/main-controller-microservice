package jp.konosuba.include.cron

import org.jetbrains.annotations.Nullable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.validation.constraints.Null

@Repository
interface CronRepository:JpaRepository<Cron,Long> {
    fun getCronByIdAndUserId(id:Long,userId:Long):Cron
    fun getCronById(id: Long): Cron?
    fun getCronByCronType(type: CronType): MutableList<Cron>


}