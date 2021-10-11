package com.example.homeapplication.data.model

import com.google.gson.annotations.SerializedName

class AuthClockinResult(

    @SerializedName("location")
    val location: AuthClockinResultLocation,

    @SerializedName("attedance")
    val attedance: AuthClockinResultAttendance
)
