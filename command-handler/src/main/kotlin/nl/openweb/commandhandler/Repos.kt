package nl.openweb.commandhandler

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BalanceRepository : CrudRepository<Balance, Int> {
    fun findByIban(iban: String): List<Balance>
}

@Repository
interface CacRepository : CrudRepository<Cac, UUID>

@Repository
interface CmtRepository : CrudRepository<Cmt, UUID>