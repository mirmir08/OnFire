package com.example.onfire

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


//Para el uso de la api de openruteservice
interface ApiService {
    @GET("directions/driving-car")
    suspend fun getRoute(
        @Query("api_key") key:String,
        @Query("start", encoded = true) start:String,
        @Query("end", encoded = true) end:String
    ): Response<RuteResponse>
}
