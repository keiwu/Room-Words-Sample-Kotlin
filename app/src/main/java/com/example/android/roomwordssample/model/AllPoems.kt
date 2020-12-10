package com.example.android.roomwordssample.model

import java.util.*

class AllPoems {
    var poems: ArrayList<Poem>? = null

    inner class Poem {
        var id: Long = 0
        var author: String? = null
        var paragraph: String? = null
        var strain: String? = null
        var tag: String? = null
        var title: String? = null
    }
}