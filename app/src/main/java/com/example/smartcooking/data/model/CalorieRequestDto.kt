package com.example.smartcooking.data.model

import com.google.gson.annotations.SerializedName

data class CalorieRequestItemDto(
    @SerializedName("ingredient_id") val ingredientId: String,
    @SerializedName("weight_g") val weightG: Double,
    @SerializedName("method_id") val methodId: String,
    @SerializedName("added_oil_g") val addedOilG: Double
)

data class CalorieRequestDto(
    val ingredients: List<CalorieRequestItemDto>
)
