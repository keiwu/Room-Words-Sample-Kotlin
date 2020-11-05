package com.example.android.roomwordssample.service
import com.example.android.roomwordssample.AWord
import com.example.android.roomwordssample.Word
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class NetworkWordContainer(val networkReturnedWord: List<NetworkReturnedWord>) {
}

@JsonClass(generateAdapter = true)
data class NetworkReturnedWord(
        val word: String

)


/**
 * Convert Network results to database objects
 * user is the user table
 */

fun NetworkWordContainer.asDomainModel(): List<AWord> {
    return networkReturnedWord.map {
        AWord(
                word = it.word
        )
    }
}

fun NetworkWordContainer.asDatabaseModel(): Array<Word> {
    return networkReturnedWord.map {
        Word(
                word = it.word
        )
    }.toTypedArray()
}
