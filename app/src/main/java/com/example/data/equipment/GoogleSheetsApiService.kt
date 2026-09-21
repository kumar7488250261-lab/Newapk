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
data class LongHourSheetPayload(
    val action: String = "LONG_HOUR_UPDATE",
    val id: Long,
    val lpgId: String,
    val lpgName: String,
    val alpId: String,
    val alpName: String,
    val trainNo: String,
    val locoNo: String,
    val signOnDate: String,
    val signOnTime: String,
    val direction: String,
    val currentStationCode: String,
    val arrivalTimeCurrentStation: String,
    val currentTrainPosition: String,
    val positionTiming: String,
    val isClosed: Boolean,
    val reliefDate: String? = null,
    val reliefTime: String? = null,
    val reliefStationCode: String? = null,
    val closedBy: String? = null,
    val closedAt: String? = null,
    val totalDutyHoursDuration: String = "",
    val timestamp: String = ""
)

@JsonClass(generateAdapter = true)
data class GoogleSheetResponse(
    val status: String,
    val message: String? = null
)

@JsonClass(generateAdapter = true)
data class StoreSheetPayload(
    val action: String = "STORE_REGISTER_UPDATE",
    val id: Long,
    val issueDate: String,
    val crewId: String,
    val crewName: String,
    val designation: String,
    val roleType: String,
    val toTime: String,
    val toBooked: String,
    val walkieTalkieBrand: String? = null,
    val walkieTalkieNo: String? = null,
    val spareBatteryNo: String? = null,
    val detonatorNo: String? = null,
    val fsdBrand: String? = null,
    val fsdNo: String? = null,
    val notes: String = "",
    val status: String,
    val approvedByAdminId: String? = null,
    val approvedByAdminName: String? = null,
    val approvedAt: String? = null,
    val issueShift: String = "",
    val isReturned: Boolean,
    val choDate: String? = null,
    val choTime: String? = null,
    val returnNotes: String? = null,
    val returnApprovedByAdminId: String? = null,
    val returnApprovedByAdminName: String? = null,
    val returnApprovedAt: String? = null,
    val timestamp: String = ""
)

@JsonClass(generateAdapter = true)
data class StoreShiftSheetPayload(
    val action: String = "STORE_SHIFT_SUMMARY",
    val id: Long,
    val shiftDate: String,
    val shiftSlot: String,
    val adminCrewId: String,
    val adminName: String,
    val adminDesignation: String,
    val loginTime: String,
    val totalWalkieTalkieIssued: Int,
    val totalBatteryIssued: Int,
    val totalDetonatorIssued: Int,
    val totalFsdIssued: Int,
    val totalReturnsReceived: Int,
    val timestamp: String = ""
)

interface GoogleSheetsApiService {
    @POST
    suspend fun syncRecord(
        @Url webhookUrl: String,
        @Body payload: GoogleSheetPayload
    ): Response<GoogleSheetResponse>

    @POST
    suspend fun syncLongHourDuty(
        @Url webhookUrl: String,
        @Body payload: LongHourSheetPayload
    ): Response<GoogleSheetResponse>

    @POST
    suspend fun syncStoreRecord(
        @Url webhookUrl: String,
        @Body payload: StoreSheetPayload
    ): Response<GoogleSheetResponse>

    @POST
    suspend fun syncStoreShiftSummary(
        @Url webhookUrl: String,
        @Body payload: StoreShiftSheetPayload
    ): Response<GoogleSheetResponse>

    @POST
    suspend fun syncJeepMovement(
        @Url webhookUrl: String,
        @Body payload: JeepMovementSheetPayload
    ): Response<GoogleSheetResponse>

    @POST
    suspend fun syncPrRemark(
        @Url webhookUrl: String,
        @Body payload: PrRemarkSheetPayload
    ): Response<GoogleSheetResponse>

    @POST
    suspend fun syncRosterTlc(
        @Url webhookUrl: String,
        @Body payload: RosterTlcSheetPayload
    ): Response<GoogleSheetResponse>
}
