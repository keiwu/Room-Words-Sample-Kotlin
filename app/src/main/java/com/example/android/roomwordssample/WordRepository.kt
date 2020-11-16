/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.roomwordssample

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.android.roomwordssample.db.WordDao
import com.example.android.roomwordssample.service.NetworkReturnedWord
import com.example.android.roomwordssample.service.NetworkWordContainer
import com.example.android.roomwordssample.service.asDatabaseModel

/**
 * Abstracted Repository as promoted by the Architecture Guide.
 * https://developer.android.com/topic/libraries/architecture/guide.html
 */
class WordRepository(private val wordDao: WordDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allWords: LiveData<List<Word>> = wordDao.getAlphabetizedWords()

    // You must call this on a non-UI thread or your app will crash. So we're making this a
    // suspend function so the caller methods know this.
    // Like this, Room ensures that you're not doing any long running operations on the main
    // thread, blocking the UI.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(word: Word) {
        wordDao.insert(word)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertWords(wordList: List<String>) {
        var wordsContainer =
                updateNetworkReturnedWordsContainer(wordList)
        wordDao.insertWords(*wordsContainer!!.asDatabaseModel())
    }

    private fun updateNetworkReturnedWordsContainer(returnedWords: List<String>): NetworkWordContainer? {
        var networkReturnedWordList = arrayListOf<NetworkReturnedWord>()
        for (word in returnedWords) {
            networkReturnedWordList.add(NetworkReturnedWord(word))
        }
        return NetworkWordContainer(networkReturnedWordList)
    }
}
