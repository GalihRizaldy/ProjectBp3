package com.android.prakiraancuaca.network

import com.android.prakiraancuaca.model.BmkgResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BmkgApi {

    @GET("publik/prakiraan-cuaca")
    fun getCuaca(
        @Query("adm4") adm4: String
    ): Call<BmkgResponse>
}
