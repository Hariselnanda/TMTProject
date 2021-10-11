package com.example.homeapplication.data.model

import com.google.gson.annotations.SerializedName

class AuthClockinResultAttendance(


    @SerializedName("empid")
    val empid: String? = "",

    @SerializedName("tanggal")
    val tanggal: String? = "",

    @SerializedName("clockin")
    val clockin: String? = "",

    @SerializedName("clockout")
    val clockout: String? = "",

    @SerializedName("coordinate")
    val coordinate: String? = "",

    @SerializedName("activity")
    val activity: String? = ""
)
