package id.ac.ubaya.ta_160419022.model

data class Fruit(
    val id_buah:Int?,
    val nama:String?,
    val gambar:String?,
    val air:String?,
    val energi:String?,
    val protein:String?,
    val lemak:String?,
    val karbohidrat:String?,
    val serat:String?,
    val kalsium:String?,
    val fosfor:String?,
    val besi:String?,
    val natrium:String?,
    val kalium:String?,
    val tembaga:String?,
    val seng:String?,
    val betaKaroten:String?,
    val karotenTotal:String?,
    val vitaminB1:String?,
    val vitaminB2:String?,
    val vitaminC:String?,
)

data class ApiResponse(
    val data: List<Fruit>
)
