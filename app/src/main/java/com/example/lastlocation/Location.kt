package com.example.lastlocation

data class Location(
    var latitude: Double? = 0.0,
    var longitude: Double? = 0.0,
    var country: String? = null,
    var province: String? = null,
    var city: String? = null,
    var district: String? = null,
    var address: String? = null
)


