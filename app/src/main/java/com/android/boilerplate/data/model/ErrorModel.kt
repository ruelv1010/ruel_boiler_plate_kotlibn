package com.android.boilerplate.data.model

data class ErrorModel(
    val msg: String? = null,
    val status: Boolean? = false,
    val status_code: String? = null,
    val has_requirements: Boolean? = false,
    var errors: ErrorsData? = null
)

data class ErrorsData(
    var email: List<String>? = null,
    var password: List<String>? = null,
    var password_confirmation: List<String>? = null,
    var firstname: List<String>? = null,
    var lastname: List<String>? = null,
    var middlename: List<String>? = null,
    var phone_number: List<String>? = null,
    var name: List<String>? = null,
    var image: List<String>? = null,
    var desc: List<String>? = null
)
