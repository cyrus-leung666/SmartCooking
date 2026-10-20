package com.example.smartcooking.data.local.db.seed

import com.example.smartcooking.data.local.db.entity.CookingMethodEntity
import com.example.smartcooking.data.local.db.entity.IngredientEntity
import com.example.smartcooking.data.local.db.entity.RecipeEntity
import com.example.smartcooking.data.local.db.entity.RecipeIngredientEntity
import org.json.JSONArray

object SeedData {

    const val CREATED_AT: Long = 1_760_000_000_000L

    val ingredients = listOf(
        IngredientEntity("ing_chicken_breast", "鸡胸肉(去皮)", 165.0, 31.0, 0.0, 3.6, "肉类"),
        IngredientEntity("ing_egg", "鸡蛋", 144.0, 13.3, 2.8, 8.8, "蛋奶类"),
        IngredientEntity("ing_rice", "大米(生)", 346.0, 7.4, 77.9, 0.8, "主食"),
        IngredientEntity("ing_broccoli", "西兰花", 34.0, 4.1, 4.3, 0.6, "蔬菜"),
        IngredientEntity("ing_potato", "土豆", 77.0, 2.0, 17.2, 0.2, "蔬菜"),
        IngredientEntity("ing_beef", "牛里脊", 125.0, 20.2, 2.0, 4.0, "肉类"),
        IngredientEntity("ing_salmon", "三文鱼", 208.0, 20.4, 0.0, 13.4, "水产"),
        IngredientEntity("ing_tofu", "北豆腐", 84.0, 8.1, 3.4, 4.2, "豆制品"),
        IngredientEntity("ing_tomato", "番茄", 15.0, 0.9, 3.3, 0.2, "蔬菜"),
        IngredientEntity("ing_cucumber", "黄瓜", 16.0, 0.8, 2.9, 0.2, "蔬菜"),
        IngredientEntity("ing_oil", "食用油", 900.0, 0.0, 0.0, 100.0, "调味品"),
        IngredientEntity("ing_noodles", "挂面(生)", 353.0, 11.4, 74.4, 1.4, "主食")
    )

    val cookingMethods = listOf(
        CookingMethodEntity("mth_boil", "水煮", 0.05),
        CookingMethodEntity("mth_steam", "清蒸", 0.05),
        CookingMethodEntity("mth_cold", "凉拌", 0.10),
        CookingMethodEntity("mth_pan_fry", "煎", 0.70),
        CookingMethodEntity("mth_stir_fry", "炒", 0.70),
        CookingMethodEntity("mth_stew", "炖", 0.45),
        CookingMethodEntity("mth_roast", "烤", 0.35),
        CookingMethodEntity("mth_deep_fry", "炸", 1.35)
    )

    val recipes = listOf(
        RecipeEntity(
            recipeId = "rec_scrambled_egg_tomato",
            title = "番茄炒蛋",
            description = "家常快手菜,酸甜开胃,蛋白质丰富,15 分钟出锅。",
            coverImageUrl = "",
            cookingTimeMinutes = 15,
            difficulty = "简单",
            instructions = toInstructionsJson(
                "番茄洗净去蒂,切成滚刀块;鸡蛋打入碗中,加 1g 盐,用筷子充分搅打至蛋液均匀。",
                "热锅后倒入 10g 食用油,开中火烧至六成热(约 180°C,油面微微冒烟),转中小火。",
                "倒入蛋液,待底部凝固后用锅铲轻轻推动,翻炒至蛋液全部凝固成嫩滑蛋块,立即盛出备用(全程约 1 分钟)。",
                "锅中留底油,倒入番茄块,中火翻炒 2 分钟至番茄出汁、变软。",
                "加入 1g 糖中和酸味(热量可忽略),再倒入炒好的蛋块,快速翻炒 30 秒使两者融合。",
                "加少许盐调味,翻炒均匀后关火装盘。"
            ),
            createdAt = CREATED_AT
        ),
        RecipeEntity(
            recipeId = "rec_steamed_chicken_broccoli",
            title = "清蒸鸡胸肉配西兰花",
            description = "减脂期首选:高蛋白、低脂肪,清蒸与水煮几乎不吸油。",
            coverImageUrl = "",
            cookingTimeMinutes = 20,
            difficulty = "简单",
            instructions = toInstructionsJson(
                "鸡胸肉洗净,用厨房纸吸干表面水分,两面均匀抹少许盐和黑胡椒(热量可忽略),腌制 5 分钟。",
                "蒸锅加水烧开,鸡胸肉放入蒸盘,盖上锅盖,大火清蒸 8~10 分钟(视厚度,中心完全变白即熟)。",
                "蒸鸡胸肉的同时处理西兰花:西兰花掰成小朵,放入淡盐水中浸泡 5 分钟后冲洗干净。",
                "另起锅烧开水,放入西兰花焯水 2 分钟,捞出沥干(清蒸/水煮吸油系数约 0.05,几乎不吸油)。",
                "鸡胸肉蒸熟后取出,静置 2 分钟锁住肉汁,切成 1cm 厚片装盘。",
                "西兰花摆在鸡胸肉旁,可淋少许生抽(约 5g,热量可忽略)调味。"
            ),
            createdAt = CREATED_AT
        ),
        RecipeEntity(
            recipeId = "rec_pan_fried_salmon",
            title = "香煎三文鱼配土豆",
            description = "富含 Omega-3 脂肪酸,外酥里嫩,搭配水煮土豆饱腹感强。",
            coverImageUrl = "",
            cookingTimeMinutes = 25,
            difficulty = "中等",
            instructions = toInstructionsJson(
                "三文鱼解冻后用厨房纸吸干水分,两面撒少许盐和黑胡椒,腌制 10 分钟。",
                "土豆去皮切 2cm 小块,放入锅中加冷水没过,大火煮开后转中火煮 10 分钟至筷子能轻松插入。",
                "捞出土豆沥干,趁热加少许盐拌匀备用。",
                "平底锅倒入 8g 食用油,中火烧至五成热(约 160°C,油面泛波纹)。",
                "三文鱼皮朝下入锅,中火煎 3 分钟至鱼皮金黄酥脆(煎制吸油系数约 0.70)。",
                "翻面再煎 2~3 分钟,煎至鱼肉变色、中心仍带微粉即可(全熟会发柴)。",
                "土豆块放入锅中利用余油煎 1 分钟,与三文鱼一同装盘。"
            ),
            createdAt = CREATED_AT
        ),
        RecipeEntity(
            recipeId = "rec_braised_beef_potato",
            title = "红烧牛肉炖土豆",
            description = "慢火炖煮 90 分钟,牛肉软烂入味,土豆吸满汤汁,适合周末大餐。",
            coverImageUrl = "",
            cookingTimeMinutes = 90,
            difficulty = "困难",
            instructions = toInstructionsJson(
                "牛肉切 3cm 见方的大块,冷水下锅,加入 2 片姜,大火煮沸后撇去浮沫,捞出用温水洗净沥干。",
                "土豆去皮切滚刀块,放入清水中浸泡防止氧化变黑。",
                "锅中倒入 15g 食用油,中火烧至六成热(约 180°C),放入牛肉块翻炒 3 分钟至表面微黄。",
                "加入 1 勺料酒、2 勺生抽、半勺老抽、5g 冰糖(调味料热量不计入主食材计算),翻炒均匀上色。",
                "倒入热水没过牛肉(炖煮吸油系数约 0.45),大火烧开后转小火,加盖慢炖 40 分钟。",
                "开盖检查牛肉软硬,筷子能戳透即达到七成烂;加入土豆块,继续小火炖 20 分钟。",
                "转大火收汁 3~5 分钟,期间翻动防止糊底,待汤汁浓稠裹住牛肉和土豆即可关火。",
                "装盘后撒少许葱花点缀。"
            ),
            createdAt = CREATED_AT
        ),
        RecipeEntity(
            recipeId = "rec_cold_cucumber",
            title = "凉拌黄瓜",
            description = "10 分钟搞定,清爽解腻,夏日开胃小菜,几乎零负担。",
            coverImageUrl = "",
            cookingTimeMinutes = 10,
            difficulty = "简单",
            instructions = toInstructionsJson(
                "黄瓜洗净,用刀背轻轻拍裂,便于入味。",
                "拍裂后的黄瓜切成 3cm 长的段,放入碗中。",
                "加 2g 盐抓匀,腌制 5 分钟,倒掉腌出的水分(保持爽脆口感)。",
                "蒜瓣 2 个拍碎切末,与 5g 香油、5g 醋、少许糖混合成调味汁(凉拌吸油系数约 0.10)。",
                "调味汁倒入黄瓜中,充分拌匀。",
                "装盘,撒少许白芝麻点缀,即拌即食。"
            ),
            createdAt = CREATED_AT
        ),
        RecipeEntity(
            recipeId = "rec_tomato_egg_noodles",
            title = "西红柿鸡蛋面",
            description = "一碗暖胃的国民面条,番茄汤底酸甜浓郁,15 分钟上桌。",
            coverImageUrl = "",
            cookingTimeMinutes = 15,
            difficulty = "简单",
            instructions = toInstructionsJson(
                "番茄洗净切小块;鸡蛋 2 个(约 100g)打入碗中搅打均匀。",
                "锅中倒入 8g 食用油,中火烧至六成热(约 180°C),倒入蛋液炒至凝固成块,盛出备用。",
                "利用锅中余油,倒入番茄块中火翻炒 2 分钟至出汁。",
                "加入 300ml 热水,大火烧开,制成番茄汤底。",
                "另起一锅烧开水,放入挂面,煮 3~4 分钟至面条无白芯,捞出沥干。",
                "面条装入碗中,浇上番茄蛋汤,加少许盐调味。"
            ),
            createdAt = CREATED_AT
        )
    )

    val recipeIngredients = listOf(
        RecipeIngredientEntity("ri_01", "rec_scrambled_egg_tomato", "ing_egg", "mth_stir_fry", 150.0, 10.0),
        RecipeIngredientEntity("ri_02", "rec_scrambled_egg_tomato", "ing_tomato", "mth_stir_fry", 200.0, 0.0),
        RecipeIngredientEntity("ri_03", "rec_steamed_chicken_broccoli", "ing_chicken_breast", "mth_steam", 200.0, 0.0),
        RecipeIngredientEntity("ri_04", "rec_steamed_chicken_broccoli", "ing_broccoli", "mth_boil", 150.0, 0.0),
        RecipeIngredientEntity("ri_05", "rec_pan_fried_salmon", "ing_salmon", "mth_pan_fry", 150.0, 8.0),
        RecipeIngredientEntity("ri_06", "rec_pan_fried_salmon", "ing_potato", "mth_boil", 100.0, 0.0),
        RecipeIngredientEntity("ri_07", "rec_braised_beef_potato", "ing_beef", "mth_stew", 300.0, 15.0),
        RecipeIngredientEntity("ri_08", "rec_braised_beef_potato", "ing_potato", "mth_stew", 200.0, 0.0),
        RecipeIngredientEntity("ri_09", "rec_cold_cucumber", "ing_cucumber", "mth_cold", 250.0, 5.0),
        RecipeIngredientEntity("ri_10", "rec_tomato_egg_noodles", "ing_noodles", "mth_boil", 100.0, 0.0),
        RecipeIngredientEntity("ri_11", "rec_tomato_egg_noodles", "ing_egg", "mth_stir_fry", 100.0, 8.0),
        RecipeIngredientEntity("ri_12", "rec_tomato_egg_noodles", "ing_tomato", "mth_stir_fry", 150.0, 0.0)
    )

    private fun toInstructionsJson(vararg steps: String): String = JSONArray(steps).toString()
}
