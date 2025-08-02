package com.example.appstock.data.converters

import androidx.room.TypeConverter

class StringListConverter {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        return value?.split("||")?.filter { it.isNotBlank() } ?: emptyList()
    }

    @TypeConverter
    fun listToString(list: List<String>?): String {
        return list?.joinToString("||") ?: ""
    }
}
