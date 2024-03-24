package model

data class Whisky(
    val id: String,
    val koName: String,
    val enName: String,
    val country: String,
    val category: String,
    val abv: String,
    val originImageUrl: String,
) {
    override fun toString(): String =
        listOf(koName, enName, country, category, abv, id).joinToString(separator = " | ")

}