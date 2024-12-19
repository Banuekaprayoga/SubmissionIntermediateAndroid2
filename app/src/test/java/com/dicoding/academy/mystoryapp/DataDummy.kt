package com.dicoding.academy.mystoryapp

import com.dicoding.academy.mystoryapp.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "photoUri $i",
                "TimeZone $1",
                "User $i",
                "Description $i",
                1.1,
                i.toString(),
                1.1
            )
            items.add(story)
        }
        return items
    }
}