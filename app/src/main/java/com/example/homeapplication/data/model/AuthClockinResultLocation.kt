package com.example.homeapplication.data.model

import com.google.gson.annotations.SerializedName

class AuthClockinResultLocation(

    @SerializedName("desa")
    val desa: String? = "",

    @SerializedName("kecamatan")
    val kecamatan: String? = "",

    @SerializedName("kabupaten")
    val kabupaten: String? = "",

    @SerializedName("provinsi")
    val provinsi: String? = ""


)
