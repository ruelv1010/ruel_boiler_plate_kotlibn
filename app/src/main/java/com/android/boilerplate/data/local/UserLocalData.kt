package com.android.boilerplate.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserLocalData(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    var avatar : String? = null,
    val email: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val middlename: String? = null,
    val name: String? = null,
    val user_id: Int? = null,
    val username: String? = null,
    val access_token: String? = null
){
    fun getFullName() = "$firstname $middlename $lastname"
}
