package com.example.data.equipment

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class EquipmentRepository(
    private val database: AppDatabase,
    private val context: Context
) {
    private val equipmentDao = database.equipmentDao()
    private val prDao = database.prDao()
    private val longHourDao = database.longHourDao()
    private val storeDao = database.storeDao()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val apiService: GoogleSheetsApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://script.google.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GoogleSheetsApiService::class.java)
    }

    private var cachedCrewMembers: List<CrewMember> = emptyList()

    val allEquipmentRecords: Flow<List<EquipmentRecord>> = equipmentDao.getAllRecords()
    val activeIssuedRecords: Flow<List<EquipmentRecord>> = equipmentDao.getActiveIssuedRecords()
    val activeIssuedCount: Flow<Int> = equipmentDao.getActiveIssuedCount()

    // PR operations
    val allPrRequests: Flow<List<PrRequest>> = prDao.getAllPrRequests()
    val pendingPrRequests: Flow<List<PrRequest>> = prDao.getPendingPrRequests()

    // Long Hour Duty operations
    val allLongHourRecords: Flow<List<LongHourDutyRecord>> = longHourDao.getAllRecords()
    val activeLongHourRecords: Flow<List<LongHourDutyRecord>> = longHourDao.getActiveRecords()

    // Store Register operations
    val allStoreRecords: Flow<List<StoreIssueRecord>> = storeDao.getAllRecords()
    val activeStoreRecords: Flow<List<StoreIssueRecord>> = storeDao.getActiveIssuedRecords()
    val pendingApprovalRecords: Flow<List<StoreIssueRecord>> = storeDao.getPendingApprovalRecords()
    val pendingReturnRecords: Flow<List<StoreIssueRecord>> = storeDao.getPendingReturnApprovalRecords()
    val allShiftRecords: Flow<List<StoreShiftRecord>> = storeDao.getAllShiftRecords()

    suspend fun insertStoreRecord(record: StoreIssueRecord): Long = withContext(Dispatchers.IO) {
        storeDao.insertRecord(record)
    }

    suspend fun updateStoreRecord(record: StoreIssueRecord) = withContext(Dispatchers.IO) {
        storeDao.updateRecord(record)
    }

    suspend fun approveStoreIssue(id: Long, adminId: String, adminName: String, timestamp: String) =
        withContext(Dispatchers.IO) {
            storeDao.approveIssue(id, adminId, adminName, timestamp)
        }

    suspend fun submitStoreReturn(id: Long, choDate: String, choTime: String, returnNotes: String) =
        withContext(Dispatchers.IO) {
            storeDao.submitReturn(id, choDate, choTime, returnNotes)
        }

    suspend fun approveStoreReturn(id: Long, adminId: String, adminName: String, timestamp: String) =
        withContext(Dispatchers.IO) {
            storeDao.approveReturn(id, adminId, adminName, timestamp)
        }

    suspend fun deleteStoreRecord(id: Long) = withContext(Dispatchers.IO) {
        storeDao.deleteRecord(id)
    }

    suspend fun insertStoreShiftRecord(record: StoreShiftRecord): Long = withContext(Dispatchers.IO) {
        storeDao.insertShiftRecord(record)
    }

    suspend fun updateStoreShiftRecord(record: StoreShiftRecord) = withContext(Dispatchers.IO) {
        storeDao.updateShiftRecord(record)
    }

    suspend fun insertLongHourRecord(record: LongHourDutyRecord): Long = withContext(Dispatchers.IO) {
        longHourDao.insertRecord(record)
    }

    suspend fun updateLongHourRecord(record: LongHourDutyRecord) = withContext(Dispatchers.IO) {
        longHourDao.updateRecord(record)
    }

    suspend fun closeLongHourDuty(
        id: Long,
        reliefDate: String,
        reliefTime: String,
        reliefStationCode: String,
        closedBy: String,
        closedAt: String
    ) = withContext(Dispatchers.IO) {
        longHourDao.closeDuty(id, reliefDate, reliefTime, reliefStationCode, closedBy, closedAt)
    }

    suspend fun deleteLongHourRecord(id: Long) = withContext(Dispatchers.IO) {
        longHourDao.deleteRecord(id)
    }

    suspend fun insertPrRequest(request: PrRequest): Long = withContext(Dispatchers.IO) {
        prDao.insertPrRequest(request)
    }

    suspend fun updatePrStatus(id: Long, status: String, remarks: String, adminId: String, timestamp: String) =
        withContext(Dispatchers.IO) {
            prDao.updatePrStatus(id, status, remarks, adminId, timestamp)
        }

    suspend fun deletePrRequest(id: Long) = withContext(Dispatchers.IO) {
        prDao.deletePrRequest(id)
    }

    suspend fun loadCrewMaster(): List<CrewMember> = withContext(Dispatchers.IO) {
        if (cachedCrewMembers.isNotEmpty()) return@withContext cachedCrewMembers
        try {
            val json = context.assets.open("kharsia_crew_master.json").bufferedReader().use { it.readText() }
            val listType = Types.newParameterizedType(List::class.java, CrewMember::class.java)
            val adapter = moshi.adapter<List<CrewMember>>(listType)
            val list = adapter.fromJson(json) ?: emptyList()
            cachedCrewMembers = list
            list
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun findCrewById(crewId: String): CrewMember? = withContext(Dispatchers.IO) {
        val list = loadCrewMaster()
        list.find { it.crewId.equals(crewId.trim(), ignoreCase = true) }
    }

    suspend fun insertIssueRecord(record: EquipmentRecord): Long = withContext(Dispatchers.IO) {
        equipmentDao.insertRecord(record)
    }

    suspend fun updateReturnRecord(record: EquipmentRecord) = withContext(Dispatchers.IO) {
        equipmentDao.updateRecord(record)
    }

    suspend fun deleteRecord(recordId: Long) = withContext(Dispatchers.IO) {
        equipmentDao.deleteRecord(recordId)
    }

    // Google Sheets Auto-Sync for Long Hour Duty
    suspend fun syncLongHourToSheet(
        webhookUrl: String,
        record: LongHourDutyRecord,
        durationFormatted: String
    ): Boolean = withContext(Dispatchers.IO) {
        if (webhookUrl.isBlank()) return@withContext true
        try {
            val payload = LongHourSheetPayload(
                id = record.id,
                lpgId = record.lpgId,
                lpgName = record.lpgName,
                alpId = record.alpId,
                alpName = record.alpName,
                trainNo = record.trainNo,
                locoNo = record.locoNo,
                signOnDate = record.signOnDate,
                signOnTime = record.signOnTime,
                direction = record.direction,
                currentStationCode = record.currentStationCode,
                arrivalTimeCurrentStation = record.arrivalTimeCurrentStation,
                currentTrainPosition = record.currentTrainPosition,
                positionTiming = record.positionTiming,
                isClosed = record.isClosed,
                reliefDate = record.reliefDate,
                reliefTime = record.reliefTime,
                reliefStationCode = record.reliefStationCode,
                closedBy = record.closedBy,
                closedAt = record.closedAt,
                totalDutyHoursDuration = durationFormatted,
                timestamp = System.currentTimeMillis().toString()
            )
            val response = apiService.syncLongHourDuty(webhookUrl, payload)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun syncStoreRecordToSheets(webhookUrl: String, record: StoreIssueRecord): Boolean = withContext(Dispatchers.IO) {
        if (webhookUrl.isBlank()) return@withContext false
        try {
            val payload = StoreSheetPayload(
                action = "STORE_REGISTER_UPDATE",
                id = record.id,
                issueDate = record.issueDate,
                crewId = record.crewId,
                crewName = record.crewName,
                designation = record.designation,
                roleType = record.roleType,
                toTime = record.toTime,
                toBooked = record.toBooked,
                walkieTalkieBrand = record.walkieTalkieBrand,
                walkieTalkieNo = record.walkieTalkieNo,
                spareBatteryNo = record.spareBatteryNo,
                detonatorNo = record.detonatorNo,
                fsdBrand = record.fsdBrand,
                fsdNo = record.fsdNo,
                notes = record.notes,
                status = record.status,
                approvedByAdminId = record.approvedByAdminId,
                approvedByAdminName = record.approvedByAdminName,
                approvedAt = record.approvedAt,
                issueShift = record.issueShift,
                isReturned = record.isReturned,
                choDate = record.choDate,
                choTime = record.choTime,
                returnNotes = record.returnNotes,
                returnApprovedByAdminId = record.returnApprovedByAdminId,
                returnApprovedByAdminName = record.returnApprovedByAdminName,
                returnApprovedAt = record.returnApprovedAt,
                timestamp = System.currentTimeMillis().toString()
            )
            val response = apiService.syncStoreRecord(webhookUrl, payload)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun syncStoreShiftSummaryToSheets(webhookUrl: String, record: StoreShiftRecord): Boolean = withContext(Dispatchers.IO) {
        if (webhookUrl.isBlank()) return@withContext false
        try {
            val payload = StoreShiftSheetPayload(
                action = "STORE_SHIFT_SUMMARY",
                id = record.id,
                shiftDate = record.shiftDate,
                shiftSlot = record.shiftSlot,
                adminCrewId = record.adminCrewId,
                adminName = record.adminName,
                adminDesignation = record.adminDesignation,
                loginTime = record.loginTime,
                totalWalkieTalkieIssued = record.totalWalkieTalkieIssued,
                totalBatteryIssued = record.totalBatteryIssued,
                totalDetonatorIssued = record.totalDetonatorIssued,
                totalFsdIssued = record.totalFsdIssued,
                totalReturnsReceived = record.totalReturnsReceived,
                timestamp = System.currentTimeMillis().toString()
            )
            val response = apiService.syncStoreShiftSummary(webhookUrl, payload)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun syncWithGoogleSheets(webhookUrl: String): Boolean = withContext(Dispatchers.IO) {
        // General sheets sync fallback
        true
    }
}
