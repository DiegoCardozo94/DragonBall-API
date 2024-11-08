package com.example.dragonballapi

import com.example.dragonballapi.Planet

data class CharacterResponse(
    val items: List<Character>,
    val meta: Meta
)

data class PlanetResponse(
    val items: List<Planet>,
    val meta: Meta
)

data class Meta(
    val totalItems: Int,
    val itemCount: Int,
    val itemsPerPage: Int,
    val totalPages: Int,
    val currentPage: Int
)

data class Character(
    val data: List<Character>,
    val id: Int,
    val name: String,
    val ki: String,
    val maxKi: String,
    val race: String,
    val gender: String,
    val description: String,
    val image: String,
    val affiliation: String,
    val originPlanet: Planet?,
    val transformations: List<Transformation> = emptyList()
)

data class Transformation(
    val id: Int,
    val name: String,
    val ki: String,
    val image: String
)
