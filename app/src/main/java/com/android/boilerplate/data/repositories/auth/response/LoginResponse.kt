package com.android.boilerplate.data.repositories.auth.response

data class LoginResponse(
    val data: UserData? = null,
    val msg: String? = null,
    val status: Boolean? = false,
    val status_code: String? = null,
    val token: String? = null,
    val token_type: String? = null
)

data class UserData(
    var avatar: AvatarData? = null,
    var date_created: DateCreatedData? = null,
    var email: String? = null,
    var firstname: String? = null,
    var lastname: String? = null,
    var middlename: String? = null,
    var name: String? = null,
    var user_id: Int? = 0,
    var username: String? = null
)

data class AvatarData(
    var directory: String? = null,
    var filename: String? = null,
    var full_path: String? = null,
    var path: String? = null,
    var thumb_path: String? = null
)

data class DateCreatedData(
    var date_db: String? = null,
    var month_year: String? = null,
    var time_passed: String? = null,
    var timestamp: String? = null
)