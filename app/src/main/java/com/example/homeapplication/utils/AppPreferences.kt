package com.example.homeapplication.utils

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val NAME = "TMT"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    // list of app specific preferences
    private val IS_FIRST_RUN_PREF = Pair("is_first_run", true)
    private val BELUM_LOGIN = Pair("belum_login", true)
    private val NOTIFICATION = Pair("notification", false)
    private val TOKEN = Pair("token", "")
    private val USERNAME = Pair("username", "")
    private val NAMA_LENGKAP = Pair("nama_lengkap", "")
    private val EMPID = Pair("empid", "")
    private val EKTPNUMBER = Pair("ektp_number", "")
    private val PHOTO = Pair("photo", "")
    private val ABSEN_MASUK = Pair("absen_masuk", "Belum Absen Masuk")
    private val ABSEN_KELUAR = Pair("absen_keluar", "Belum Absen Keluar")
    private val ABSEN_LOCATION = Pair("absen_location", "")
    private val ABSEN = Pair("absen", false)
    private val ABSEN_STEP_TWO = Pair("absen_step_two", false)
    private val ABSEN_PHOTO = Pair("absen_photo", "")

    fun init(context: Context) {
        preferences = context.getSharedPreferences(
            NAME,
            MODE
        )
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var firstRun: Boolean
        get() = preferences.getBoolean(
            IS_FIRST_RUN_PREF.first, IS_FIRST_RUN_PREF.second
        )
        set(value) = preferences.edit {
            it.putBoolean(IS_FIRST_RUN_PREF.first, value)
        }

    var belumLogin: Boolean
        get() = preferences.getBoolean(
            BELUM_LOGIN.first, BELUM_LOGIN.second
        )
        set(value) = preferences.edit {
            it.putBoolean(BELUM_LOGIN.first, value)
        }


    var username: String?
        get() = preferences.getString(
            USERNAME.first, USERNAME.second
        )
        set(value) = preferences.edit {
            it.putString(USERNAME.first, value)
        }
    var token: String?
        get() = preferences.getString(
            TOKEN.first, TOKEN.second
        )
        set(value) = preferences.edit {
            it.putString(TOKEN.first, value)
        }

    var namaLengkap: String?
        get() = preferences.getString(
            NAMA_LENGKAP.first, NAMA_LENGKAP.second
        )
        set(value) = preferences.edit {
            it.putString(NAMA_LENGKAP.first, value)
        }

    var empId: String?
        get() = preferences.getString(
            EMPID.first, EMPID.second
        )
        set(value) = preferences.edit {
            it.putString(EMPID.first, value)
        }

    var ektpNumber: String?
        get() = preferences.getString(
            EKTPNUMBER.first, EKTPNUMBER.second
        )
        set(value) = preferences.edit {
            it.putString(EKTPNUMBER.first, value)
        }

    var photo: String?
        get() = preferences.getString(
            PHOTO.first, PHOTO.second
        )
        set(value) = preferences.edit {
            it.putString(PHOTO.first, value)
        }

    var absenMasuk: String?
        get() = preferences.getString(
            ABSEN_MASUK.first, ABSEN_MASUK.second
        )
        set(value) = preferences.edit {
            it.putString(ABSEN_MASUK.first, value)
        }

    var absenKeluar: String?
        get() = preferences.getString(
            ABSEN_KELUAR.first, ABSEN_KELUAR.second
        )
        set(value) = preferences.edit {
            it.putString(ABSEN_KELUAR.first, value)
        }

    var absenLocation: String?
        get() = preferences.getString(
            ABSEN_LOCATION.first, ABSEN_LOCATION.second
        )
        set(value) = preferences.edit {
            it.putString(ABSEN_LOCATION.first, value)
        }

    var absen: Boolean
        get() = preferences.getBoolean(
            ABSEN.first, ABSEN.second
        )
        set(value) = preferences.edit {
            it.putBoolean(ABSEN.first, value)
        }

    var absenStepTwo: Boolean
        get() = preferences.getBoolean(
            ABSEN_STEP_TWO.first, ABSEN_STEP_TWO.second
        )
        set(value) = preferences.edit {
            it.putBoolean(ABSEN_STEP_TWO.first, value)
        }

    var absenPhoto: String?
        get() = preferences.getString(
            ABSEN_PHOTO.first, ABSEN_PHOTO.second
        )
        set(value) = preferences.edit {
            it.putString(ABSEN_PHOTO.first, value)
        }

}