package com.billray.worship.infra.db

import com.billray.worship.domain.Song
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class SongRepository {
    fun findAll(): List<Song> {
        return transaction {
            SongsTable
                .selectAll()
                .orderBy(SongsTable.title to SortOrder.ASC)
                .map(::toSong)
        }
    }

    fun save(song: Song): Long {
        return transaction {
            if (song.id == null) {
                SongsTable.insert {
                    it[title] = song.title
                    it[lyrics] = song.lyrics
                    it[author] = song.author
                    it[tags] = serializeTags(song.tags)
                }[SongsTable.id]
            } else {
                SongsTable.update({ SongsTable.id eq song.id }) {
                    it[title] = song.title
                    it[lyrics] = song.lyrics
                    it[author] = song.author
                    it[tags] = serializeTags(song.tags)
                }
                song.id
            }
        }
    }

    fun delete(songId: Long) {
        transaction {
            SongsTable.deleteWhere { id eq songId }
        }
    }

    private fun toSong(row: ResultRow): Song {
        return Song(
            id = row[SongsTable.id],
            title = row[SongsTable.title],
            lyrics = row[SongsTable.lyrics],
            author = row[SongsTable.author],
            tags = deserializeTags(row[SongsTable.tags])
        )
    }

    private fun serializeTags(tags: List<String>): String {
        return tags
            .map(String::trim)
            .filter(String::isNotBlank)
            .joinToString(",")
    }

    private fun deserializeTags(raw: String): List<String> {
        return raw
            .split(",")
            .map(String::trim)
            .filter(String::isNotBlank)
    }
}
