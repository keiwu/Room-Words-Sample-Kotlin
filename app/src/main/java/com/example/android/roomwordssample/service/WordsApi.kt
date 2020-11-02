package com.example.android.roomwordssample.service

import com.example.android.roomwordssample.model.AllPoems
import retrofit2.Call
import retrofit2.http.GET

interface WordsApi {
    @get:GET("Api.php?apicall=getwords")
    val words: Call<Any?>?

    @get:GET("Api.php?apicall=getauthors")
    val authorPoemCount: Call<Any?>?

    @get:GET("Api.php?apicall=getpoems")
    val allPoems: Call<AllPoems>
}