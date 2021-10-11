package com.example.homeapplication.data.model

import com.google.gson.annotations.SerializedName

class AuthLogin(

    @SerializedName("status")
    val status: String? = "",

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("empid")
    val empid: String? = null,

    @SerializedName("token")
    val token: String? = null,

    @SerializedName("full_name")
    val full_name: String? = null,

    @SerializedName("e_ktp_number")
    val e_ktp_number: String? = null,

    @SerializedName("photo")
    val photo: String? = null
)
