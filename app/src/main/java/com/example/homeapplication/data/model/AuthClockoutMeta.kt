package com.example.homeapplication.data.model

import com.google.gson.annotations.SerializedName

class AuthClockoutMeta(

        @SerializedName("status")
        val status: String? = "",

        @SerializedName("message")
        val message: String? = ""
)
