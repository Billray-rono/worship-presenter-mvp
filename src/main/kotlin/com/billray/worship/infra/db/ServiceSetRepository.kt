package com.billray.worship.infra.db

import com.billray.worship.domain.ServiceSet
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class ServiceSetRepository {
    fun save(serviceSet: ServiceSet): Long {
        return transaction {
            if (serviceSet.id == null) {
                ServiceSetsTable.insert {
                    it[name] = serviceSet.name
                }[ServiceSetsTable.id]
            } else {
                ServiceSetsTable.update({ ServiceSetsTable.id eq serviceSet.id }) {
                    it[name] = serviceSet.name
                }
                serviceSet.id
            }
        }
    }

    fun findById(id: Long): ServiceSet? {
        return transaction {
            ServiceSetsTable
                .selectAll()
                .where { ServiceSetsTable.id eq id }
                .limit(1)
                .map { row ->
                    ServiceSet(
                        id = row[ServiceSetsTable.id],
                        name = row[ServiceSetsTable.name]
                    )
                }
                .firstOrNull()
        }
    }
}
