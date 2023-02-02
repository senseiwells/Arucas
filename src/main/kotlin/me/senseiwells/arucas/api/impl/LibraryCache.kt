package me.senseiwells.arucas.api.impl

import com.google.gson.JsonObject

data class LibraryCache(
    val lastUpdateSeconds: Long,
    val lastSha: String,
    val downloadUrl: String
) {
    companion object {
        fun fromJson(json: JsonObject?): LibraryCache {
            return LibraryCache(
                json?.get("last")?.asLong ?: 0,
                json?.get("sha")?.asString ?: "",
                json?.get("url")?.asString ?: ""
            )
        }
    }

    fun toJson(): JsonObject {
        return JsonObject().also {
            it.addProperty("last", this.lastUpdateSeconds)
            it.addProperty("sha",  this.lastSha)
            it.addProperty("url", this.downloadUrl)
        }
    }
}