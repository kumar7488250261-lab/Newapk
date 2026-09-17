package com.example.ui.screens.equipment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.equipment.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EquipmentViewModel(
    private val repository: EquipmentRepository,
    private val inChargeAuthManager: InChargeAuthManager
) : ViewModel() {

    val activeIssuedCount = repository.activeIssuedCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeIssuedRecords = repository.activeIssuedRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPrRequests = repository.allPrRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _crewLookupResult = MutableStateFlow<CrewMember?>(null)
    val crewLookupResult: StateFlow<CrewMember?> = _crewLookupResult.asStateFlow()

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage: SharedFlow<String> = _uiMessage.asSharedFlow()

    val isAdminSessionActive: Boolean
        get() = inChargeAuthManager.isSessionValid

    val currentAdminUser: String
        get() = inChargeAuthManager.currentAdminUser

    fun verifyAdminPin(pin: String): Boolean {
        return inChargeAuthManager.verifyPin(pin)
    }

    fun endAdminSession() {
        inChargeAuthManager.endSession()
    }

    fun lookupCrew(crewId: String) {
        val trimmed = crewId.trim()
        if (trimmed.isEmpty()) {
            _crewLookupResult.value = null
            return
        }
        viewModelScope.launch {
            val found = repository.findCrewById(trimmed)
            _crewLookupResult.value = found
        }
    }

    fun clearCrewLookup() {
        _crewLookupResult.value = null
    }

    fun submitIssue(
        equipmentName: String,
        serialNo: String,
        crewId: String,
        crewName: String,
        designation: String,
        category: DesignationCategory,
        trainNo: String = "",
        remarks: String = ""
    ) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val currentTime = sdf.format(Date())

            val record = EquipmentRecord(
                equipmentName = equipmentName,
                equipmentSerialNo = serialNo,
                issuedToCrewId = crewId,
                issuedToCrewName = crewName,
                designation = designation,
                category = category,
                issueTime = currentTime,
                trainNo = trainNo,
                remarks = remarks,
                status = EquipmentStatus.ISSUED
            )

            repository.insertIssueRecord(record)
            _uiMessage.emit("उपकरण सफलतापूर्वक जारी किया गया: $equipmentName ($serialNo)")
        }
    }

    fun submitReturn(
        record: EquipmentRecord,
        remarks: String = ""
    ) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val currentTime = sdf.format(Date())

            val updatedRecord = record.copy(
                returnTime = currentTime,
                status = EquipmentStatus.RETURNED,
                remarks = if (remarks.isNotBlank()) "${record.remarks} | Return: $remarks" else record.remarks
            )

            repository.updateReturnRecord(updatedRecord)
            _uiMessage.emit("उपकरण सफलतापूर्वक वापस लिया गया: ${record.equipmentName}")
        }
    }

    fun submitPrRequest(
        crewId: String,
        crewName: String,
        designation: String,
        signOffDate: String,
        signOffTime: String
    ) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            val requestDate = sdf.format(Date())

            val request = PrRequest(
                crewId = crewId.trim().uppercase(),
                crewName = crewName.trim(),
                designation = designation.trim(),
                signOffDate = signOffDate,
                signOffTime = signOffTime,
                requestDate = requestDate,
                status = "Pending",
                remarks = ""
            )

            repository.insertPrRequest(request)
            _uiMessage.emit("PR अनुरोध सफलतापूर्वक दर्ज किया गया! (Crew ID: ${request.crewId})")
        }
    }

    fun reviewPrRequest(
        requestId: Long,
        status: String,
        remarks: String
    ) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            val reviewedAt = sdf.format(Date())
            val adminId = inChargeAuthManager.currentAdminUser

            repository.updatePrStatus(
                id = requestId,
                status = status,
                remarks = remarks,
                adminId = adminId,
                timestamp = reviewedAt
            )
            _uiMessage.emit("PR अनुरोध $status अपडेट किया गया (रिमार्क: $remarks)")
        }
    }

    fun deletePrRequest(requestId: Long) {
        viewModelScope.launch {
            repository.deletePrRequest(requestId)
            _uiMessage.emit("PR अनुरोध हटाया गया")
        }
    }

    fun syncWithGoogleSheets() {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                repository.syncWithGoogleSheets("")
                _uiMessage.emit("Google Sheets सिंक सफल!")
            } catch (e: Exception) {
                _uiMessage.emit("सिंक में त्रुटि: ${e.message}")
            } finally {
                _isSyncing.value = false
            }
        }
    }
}

class EquipmentViewModelFactory(
    private val repository: EquipmentRepository,
    private val inChargeAuthManager: InChargeAuthManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EquipmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EquipmentViewModel(repository, inChargeAuthManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
