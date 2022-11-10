package com.bcc.exporeal.model

data class PermintaanModel(
    val permintaan_id:String? = null,
    val permintaan_count:Int? = null,
    val category_id:String? = null,
    val peminta_uid:String? = null,
    val name:String? = null,
    val description:String? = null,
    val thumbnail:String? = null,
    val quantity:String? = null,
    val quantity_unit:String? = null,
    val bottom_price:String? = null,
    val top_price:String? = null,
    val flag_id:String? = null,
    val tanggal_permintaan:String? = null
)
