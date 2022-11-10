package com.bcc.exporeal.model

data class ProductModel(
    val product_id:String? = null,
    val product_count:Int? = null,
    val product_name:String? = null,
    val product_description:String? = null,
    val product_price:String? = null,
    val product_thumbnail:String? = null,
    val product_quantity:Int? = null,
    val product_unit:String? = null,
    val category_id:String? = null,
    val seller_id:String? = null
)
