package com.billray.worship.infra.db

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

object ServiceSetsTable : Table("service_sets") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 255)

    override val primaryKey = PrimaryKey(id)
}

object Schema {
    fun create() {
        transaction {
            SchemaUtils.create(ServiceSetsTable)
        }
    }
}
