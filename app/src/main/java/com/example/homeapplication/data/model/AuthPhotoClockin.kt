package com.example.homeapplication.data.model

import com.google.gson.annotations.SerializedName

class AuthPhotoClockin(
        @SerializedName("meta")
        var meta: AuthPhotoClockinMeta,

        @SerializedName("result")
        val result: AuthPhotoClockinResult
)
