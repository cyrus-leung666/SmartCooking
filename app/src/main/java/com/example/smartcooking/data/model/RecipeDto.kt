package com.example.smartcooking.data.model

import com.google.gson.annotations.SerializedName

data class RecipeDto(
    @SerializedName("recipe_id") val recipeId: String,
    val title: String,
    val description: String,
    @SerializedName("cover_image_url") val coverImageUrl: String,
    @SerializedName("cooking_time_minutes") val cookingTimeMinutes: Int,
    val difficulty: String,
    val instructions: List<String>
)
