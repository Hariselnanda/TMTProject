package com.example.homeapplication.utils

import android.app.Application
import okhttp3.*
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


class ApiClientSimple : Application() {
    var interceptor: Interceptor = Interceptor { chain ->
        val oldRequest: Request = chain.request()
        val newRequest: Request.Builder = chain.request().newBuilder()
        if ("POST" == oldRequest.method() && (oldRequest.body() == null || oldRequest.body()!!
                .contentLength() <= 0)
        ) {
            newRequest.post(RequestBody.create(MediaType.parse("application/json"), "{}"))
        }
        chain.proceed(newRequest.build())
    }
    val nullOnEmptyConverterFactory = object : Converter.Factory() {
        fun converterFactory() = this
        override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) = object : Converter<ResponseBody, Any?> {
            val nextResponseBodyConverter = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
            override fun convert(value: ResponseBody) = if (value.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
        }
    }
    private val client = OkHttpClient().newBuilder()

        .addInterceptor(interceptor)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(nullOnEmptyConverterFactory)
//        .baseUrl("http://147.139.181.131:1323/")
        .baseUrl("https://tmt.juvanita.com/")
        .client(client)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val services: ApiService = retrofit.create(ApiService::class.java)


}