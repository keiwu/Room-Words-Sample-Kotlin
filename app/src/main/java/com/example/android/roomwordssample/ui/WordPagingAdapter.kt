package com.example.android.roomwordssample.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.roomwordssample.R
import com.example.android.roomwordssample.Word
import com.example.android.roomwordssample.WordListAdapter


class WordPagingAdapter(context: Context) :
        PagedListAdapter<Word, WordPagingAdapter.WordViewHolder>(DIFF_CALLBACK) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    companion object {
        private val DIFF_CALLBACK = object :
                DiffUtil.ItemCallback<Word>() {
            // Concert details may have changed if reloaded from the database,
            // but ID is fixed.
            override fun areItemsTheSame(oldConcert: Word,
                                         newConcert: Word) = oldConcert.id == newConcert.id

            override fun areContentsTheSame(oldConcert: Word,
                                            newConcert: Word) = oldConcert == newConcert
        }
    }

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val current: Word? = getItem(position)

        // Note that "concert" is a placeholder if it's null.
        holder.wordItemView.text = current?.author + " " +  current?.title
    }
}
