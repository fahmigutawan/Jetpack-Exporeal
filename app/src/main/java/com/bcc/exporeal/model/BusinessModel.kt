package com.bcc.exporeal.model

data class BusinessModel(
    val business_id:String? = null,
    val uid:String? = null,
    val business_name:String? = null,
    val business_owner:String? = null,
    val business_description:String? = null,
    val phone_num:String? = null,
    val email:String? = null,
    val address_detail:String? = null,
    val address_country:String? = null,
    val address_province:String? = null,
    val address_city:String? = null,
    val address_postcode:String? = null,
    val verification_status:Int? = null
)
