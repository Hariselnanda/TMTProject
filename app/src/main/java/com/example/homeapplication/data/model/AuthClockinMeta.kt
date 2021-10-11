package com.example.homeapplication.data.model

import com.google.gson.annotations.SerializedName

class AuthClockinMeta(

        @SerializedName("status")
        val status: String? = "",

        @SerializedName("message")
        val message: String? = ""
)
