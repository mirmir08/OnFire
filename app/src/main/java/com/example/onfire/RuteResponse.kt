package com.example.onfire

import com.google.gson.annotations.SerializedName

data class RuteResponse(@SerializedName("features") val features: List<Feature>)

data class Feature(@SerializedName("geometry") val geometry: Geometry)

data class Geometry(@SerializedName("coordinates") val coordinates: List<List<Double>>)
