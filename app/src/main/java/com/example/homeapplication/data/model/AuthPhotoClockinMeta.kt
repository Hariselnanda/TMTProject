package com.example.homeapplication.data.model

import com.google.gson.annotations.SerializedName

class AuthPhotoClockinMeta(

        @SerializedName("status")
        val status: String? = "",

        @SerializedName("message")
        val message: String? = ""
)
