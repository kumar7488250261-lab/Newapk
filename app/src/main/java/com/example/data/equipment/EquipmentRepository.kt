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

    suspend fun syncWithGoogleSheets(webhookUrl: String): Boolean = withContext(Dispatchers.IO) {
        // Implementation for optional webhook sync
        true
    }
}
