package com.example.data.equipment

import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

@JsonClass(generateAdapter = true)
data class GoogleSheetPayload(
    val action: String,
    val recordId: Long,
    val equipmentName: String,
    val serialNo: String,
    val crewId: String,
    val crewName: String,
    val designation: String,
    val category: String,
    val issueTime: String,
    val returnTime: String? = null,
    val status: String,
    val trainNo: String = "",
    val remarks: String = ""
)

@JsonClass(generateAdapter = true)
data class GoogleSheetResponse(
    val status: String,
    val message: String? = null
)

interface GoogleSheetsApiService {
    @POST
    suspend fun syncRecord(
        @Url webhookUrl: String,
        @Body payload: GoogleSheetPayload
    ): Response<GoogleSheetResponse>
}
