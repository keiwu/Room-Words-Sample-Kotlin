package com.example.android.roomwordssample.service
import com.example.android.roomwordssample.AWord
import com.example.android.roomwordssample.Word
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class NetworkWordContainer(val networkReturnedWord: List<NetworkReturnedWord>) {
}

@JsonClass(generateAdapter = true)
data class NetworkReturnedWord(
        var id: Long,
        var author: String?,
        var paragraph: String?,
        var strain: String?,
        var tag: String?,
        var title: String
)


/**
 * Convert Network results to database objects
 * user is the user table
 */

fun NetworkWordContainer.asDomainModel(): List<AWord> {
    return networkReturnedWord.map {
        AWord(
                id = it.id,
                author = it.author,
                paragraph = it.paragraph,
                strain = it.strain,
                tag = it.tag,
                title = it.title
        )
    }
}

fun NetworkWordContainer.asDatabaseModel(): Array<Word> {
    return networkReturnedWord.map {
        Word(
                id = it.id,
                author = it.author,
                paragraph = it.paragraph,
                strain = it.strain,
                tag = it.tag,
                title = it.title
        )
    }.toTypedArray()
}
