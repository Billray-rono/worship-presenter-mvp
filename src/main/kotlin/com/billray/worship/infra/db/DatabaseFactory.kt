package com.billray.worship.infra.db

import org.jetbrains.exposed.sql.Database
import java.nio.file.Files
import java.nio.file.Path

object DatabaseFactory {
    fun init() {
        val dbDir = Path.of("data")
        Files.createDirectories(dbDir)
        val dbPath = dbDir.resolve("worship.db")
        Database.connect("jdbc:sqlite:${dbPath.toAbsolutePath()}", driver = "org.sqlite.JDBC")

        Schema.create()
    }
}
