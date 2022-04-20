/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.amphibians.network

import com.squareup.moshi.Json

/**
 * This data class defines an Amphibian which includes the amphibian's name, the type of
 * amphibian, and a brief description of the amphibian.
 * The property names of this data class are used by Moshi to match the names of values in JSON.
 */
data class Amphibian(
    @Json(name = "province") val province: String,
    @Json(name = "county") val county: String,
    @Json(name = "stats") val stats: Stats,
    @Json(name = "updatedAt") val updatedAt: String,
    @Json(name = "coordinates") val coordinates: Coordinates

)

data class Stats(
    @Json(name = "confirmed") val confirmed: String,
    @Json(name = "deaths") val deaths: String,
    @Json(name = "recovered") val recovered: String? = null
)

data class Coordinates(
    @Json(name = "latitude") val latitude: String,
    @Json(name = "longitude") val longitude: String

)

