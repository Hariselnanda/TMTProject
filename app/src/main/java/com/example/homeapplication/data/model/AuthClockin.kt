package com.example.homeapplication.data.model

import com.google.gson.annotations.SerializedName

class AuthClockin(
        @SerializedName("meta")
        var meta: AuthClockinMeta,

        @SerializedName("result")
        val result: AuthClockinResult
)
