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

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.paging.flatMap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.roomwordssample.model.AllPoems
import com.example.android.roomwordssample.service.WordsApi
import com.example.android.roomwordssample.ui.SearchPoemsViewModel
import com.example.android.roomwordssample.ui.WordViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

const val BASE_URL = "http://littlewebsolution.altervista.org/poetry/v1/"


class MainActivity : AppCompatActivity() {

    private val newWordActivityRequestCode = 1
    //private lateinit var wordViewModel: WordViewModel
    private lateinit var viewModel: SearchPoemsViewModel
    private var searchJob: Job? = null



    val wordViewModel: WordViewModel by lazy {
        val activity = requireNotNull(this) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(this, WordViewModel.Factory(this.application))
                .get(WordViewModel::class.java)
    }

    lateinit var progressBar: ProgressBar

    private fun search(query: String, adapter: WordListAdapter?) {
        // Make sure we cancel the previous job before creating a new one
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchPoems(query).collectLatest {
                //adapter.submitData(it)
                Log.d("it", it.toString())
            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get the view model
        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(this))
                .get(SearchPoemsViewModel::class.java)


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = WordListAdapter(this)
        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(this)

        search("android", null)

        // Get a new or existing ViewModel from the ViewModelProvider.
        //wordViewModel = ViewModelProvider(this).get(WordViewModel::class.java)


        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        wordViewModel.allWords.observe(
                this,
                Observer { words ->
                    // Update the cached copy of the words in the adapter.
                    words?.let { adapter.setWords(it) }
                }
        )

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewWordActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }

        progressBar = findViewById(R.id.progressBar)

        getAllPoems()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
//        super.onActivityResult(requestCode, resultCode, intentData)
//
//        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
//            intentData?.getStringExtra(NewWordActivity.EXTRA_REPLY)?.let { reply ->
//                val word = Word(reply)
//                wordViewModel.insert(word)
//            }
//        } else {
//            Toast.makeText(
//                    applicationContext,
//                    R.string.empty_not_saved,
//                    Toast.LENGTH_LONG
//            ).show()
//        }
//    }

    fun getAllPoems() {
        val gson: Gson = GsonBuilder()
                .setLenient()
                .create()
        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        val wordsAPI: WordsApi = retrofit.create(WordsApi::class.java)
        val call: Call<AllPoems> = wordsAPI.allPoems

        call.enqueue(object : Callback<AllPoems> {
                override fun onResponse(call: Call<AllPoems>, response: Response<AllPoems>) {
                    val allPoemsAl: ArrayList<AllPoems.Poem>? = response.body()?.poems
                    Log.d("NotifyDataChanged", "poems count" + (allPoemsAl?.size ?: 0))
                    if (allPoemsAl != null) {
                        val wordList = ArrayList<AWord>()
                        for (poem in allPoemsAl){
                            val id = poem.id
                            val author = poem.author
                            val paragraph = poem.paragraph
                            val strain = poem.strain
                            val tag = poem.tag
                            val title = poem.title

                            wordList.add(AWord(id, author, paragraph, strain, tag, title!!))

                        }

                        //insert words all at once
                        wordViewModel.insertWords(wordList)

                    }
                    progressBar.visibility = View.GONE

                }

                override fun onFailure(call: Call<AllPoems>, t: Throwable) {
                    println("res")
                    progressBar.visibility = View.GONE

                }
            }
        )

    }

}
