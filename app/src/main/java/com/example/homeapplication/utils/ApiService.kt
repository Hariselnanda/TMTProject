package com.example.homeapplication.utils

import com.example.homeapplication.data.model.AuthClockin
import com.example.homeapplication.data.model.AuthClockout
import com.example.homeapplication.data.model.AuthLogin
import com.example.homeapplication.data.model.AuthPhotoClockin
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface ApiService {

    @FormUrlEncoded
    @POST("/auth/loginapk")
    fun Login(
        @Field("identity") identity: String,
        @Field("password") password : String
    ): Observable<AuthLogin>

    @FormUrlEncoded
    @POST("/mobile/attedance/clockin")
    fun Clockin(
            @Field("empid") empid: String,
            @Field("latitude") latitude : String,
            @Field("longitude") longitude : String
    ): Observable<AuthClockin>

    @FormUrlEncoded
    @POST("/mobile/attedance/clockout")
    fun ClockOut(
        @Field("empid") empid: String,
    ): Observable<AuthClockout>

    @FormUrlEncoded
    @POST("/mobile/attedance/clockin_photo")
    fun PhotoClockin(
        @Field("empid") empid: String,
        @Field("img_base") img_base : String
    ): Observable<AuthPhotoClockin>
}