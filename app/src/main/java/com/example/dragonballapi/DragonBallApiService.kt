package com.example.dragonballapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface DragonBallApiService {
    @GET("characters")
    fun getCharacters(): Call<CharacterResponse>
    @GET("planets")
    fun getPlanets(): Call<PlanetResponse>
    @GET("characters/{id}")
    fun getCharacterDetails(@Path("id") characterId: Int): Call<Character>
}
