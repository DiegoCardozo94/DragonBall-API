package com.example.dragonballapi

import retrofit2.Call
import retrofit2.http.GET

interface DragonBallApiService {
    @GET("characters")
    fun getCharacters(): Call<CharacterResponse>
}
