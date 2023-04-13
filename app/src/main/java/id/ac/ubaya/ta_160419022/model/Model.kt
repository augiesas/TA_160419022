package id.ac.ubaya.ta_160419022.model

data class FruitNutritionFacts(
    val nama: String?,
    val air: String?,
    val energi: String?,
    val protein: String?,
    val lemak: String?,
    val karbohidrat: String?,
    val gula_total: String?,
    val serat: String?,
    val kalsium: String?,
    val fosfor: String?,
    val besi: String?,
    val natrium: String?,
    val kalium: String?,
    val tembaga: String?,
    val seng: String?,
    val magnesium: String?,
    val beta_karoten: String?,
    val karoten_total: String?,
    val vitamin_a: String?,
    val vitaminB1: String?,
    val vitaminB2: String?,
    val niacin: String?,
    val vitamin_b6: String?,
    val vitaminC: String?,
    val vitamin_e: String?,
    val vitamin_d: String?,
    val vitamin_k: String?,
    val sumber: String?,
)

data class Fruit(
    val kategori: Int?,
    val value: String?
)

data class ApiResponse(
    val data: List<FruitNutritionFacts>
)

data class NutritionList(
    val kategori: String?,
    val value: String?
)

data class ApiResponseNutrition(
    val data: List<NutritionList>
)
