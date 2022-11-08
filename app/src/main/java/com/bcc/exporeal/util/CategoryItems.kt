package com.bcc.exporeal.util

import com.bcc.exporeal.R

enum class CategoryItems(
    val word:String,
    val category_id:String,
    val iconId:Int,
) {
    Semua(
        "Semua",
        "1",
        R.drawable.ic_category_semua
    ),
    Perikanan(
        "Perikanan",
        "2",
        R.drawable.ic_category_perikanan
    ),
    Perkebunan(
        "Perkebunan",
        "3",
        R.drawable.ic_category_perkebunan
    ),
    Furniture(
        "Furniture",
        "4",
        R.drawable.ic_category_furniture
    ),
    Tekstil(
        "Tekstil",
        "5",
        R.drawable.ic_category_tekstil
    ),
    Lainnya(
        "Lainnya",
        "6",
        R.drawable.ic_category_lainnya
    )
}