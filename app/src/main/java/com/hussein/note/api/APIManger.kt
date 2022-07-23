package com.hussein.note.logIn.api

import com.hussein.note.base.MyApplication
import com.hussein.note.base.TinyDB
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object NoteAPIManger {
    private var retrofitInstance: Retrofit? = null
    private lateinit var tinyDB: TinyDB

    private fun getInstance(): Retrofit? {
        if (retrofitInstance == null) {
            tinyDB = TinyDB(MyApplication.appContext)
            val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .readTimeout(90,TimeUnit.SECONDS)
                .writeTimeout(90,TimeUnit.SECONDS)
                .callTimeout(90,TimeUnit.SECONDS)
                .addInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val request = chain.request().newBuilder()
//                            .addHeader("Authorization", "Bearer " + tinyDB.getString("token"))
                            .build()
                        return chain.proceed(request)
                    }
                })
                .addInterceptor(interceptor)
                .build()


            retrofitInstance = Retrofit.Builder()
                .baseUrl("https://ktor-note-ktor.herokuapp.com/v1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }
        return retrofitInstance
    }

     fun getApis(): NoteApiCalls? {
        val apiCalls = getInstance()?.create(NoteApiCalls::class.java)
        return apiCalls
    }
//    val apis: ApiCalls
//        get() = getInstance(token = null)!!.create(ApiCalls::class.java)
}