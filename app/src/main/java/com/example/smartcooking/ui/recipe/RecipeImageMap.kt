package com.example.smartcooking.ui.recipe

import androidx.annotation.DrawableRes
import com.example.smartcooking.R

/** 菜谱详情页的本地封面图，按 recipeId 一一对应。 */
@DrawableRes
fun recipeCoverImageRes(recipeId: String): Int? = when (recipeId) {
    "rec_scrambled_egg_tomato" -> R.drawable.recipe_tomato_scrambled_egg
    "rec_steamed_chicken_broccoli" -> R.drawable.recipe_steamed_chicken_broccoli
    "rec_pan_fried_salmon" -> R.drawable.recipe_pan_fried_salmon
    "rec_braised_beef_potato" -> R.drawable.recipe_braised_beef_potato
    "rec_cold_cucumber" -> R.drawable.recipe_cold_cucumber
    "rec_tomato_egg_noodles" -> R.drawable.recipe_tomato_egg_noodles
    "rec_milk_oatmeal" -> R.drawable.recipe_milk_oatmeal
    "rec_boiled_egg" -> R.drawable.recipe_boiled_egg
    "rec_whole_wheat_bread" -> R.drawable.recipe_whole_wheat_bread
    "rec_milk" -> R.drawable.recipe_milk
    "rec_banana" -> R.drawable.recipe_banana
    "rec_tomato_egg_sandwich" -> R.drawable.recipe_tomato_egg_sandwich
    "rec_yogurt_fruit_cup" -> R.drawable.recipe_yogurt_fruit_cup
    "rec_steamed_rice" -> R.drawable.recipe_steamed_rice
    else -> null
}
