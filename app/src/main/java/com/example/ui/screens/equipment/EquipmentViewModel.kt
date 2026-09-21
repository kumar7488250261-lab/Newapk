package com.example.ui.screens.equipment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.equipment.*
import kotlinx.coroutines.delay
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

    // Long Hour records
    val allLongHourRecords = repository.allLongHourRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeLongHourRecords = repository.activeLongHourRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Store Register records
    val allStoreRecords = repository.allStoreRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeStoreRecords = repository.activeStoreRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingApprovalRecords = repository.pendingApprovalRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingReturnRecords = repository.pendingReturnRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allShiftRecords = repository.allShiftRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Roster & TLC records
    val allRosterTlcRecords = repository.allRosterTlcRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Store Admin Shift state
    private val _storeAdminMember = MutableStateFlow<CrewMember?>(null)
    val storeAdminMember: StateFlow<CrewMember?> = _storeAdminMember.asStateFlow()

    private val _storeShiftSlot = MutableStateFlow("06:00 - 14:00")
    val storeShiftSlot: StateFlow<String> = _storeShiftSlot.asStateFlow()

    private val _storeShiftDate = MutableStateFlow(SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()))
    val storeShiftDate: StateFlow<String> = _storeShiftDate.asStateFlow()

    // Google Sheets Webhook URL configured by user/admin (default empty or app script endpoint)
    private val _sheetsWebhookUrl = MutableStateFlow("")
    val sheetsWebhookUrl: StateFlow<String> = _sheetsWebhookUrl.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    // Single crew lookup for PR screen
    private val _crewLookupResult = MutableStateFlow<CrewMember?>(null)
    val crewLookupResult: StateFlow<CrewMember?> = _crewLookupResult.asStateFlow()

    // Dual crew lookup for Long Hour: LPG and ALP
    private val _lpgLookupResult = MutableStateFlow<CrewMember?>(null)
    val lpgLookupResult: StateFlow<CrewMember?> = _lpgLookupResult.asStateFlow()

    private val _alpLookupResult = MutableStateFlow<CrewMember?>(null)
    val alpLookupResult: StateFlow<CrewMember?> = _alpLookupResult.asStateFlow()

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

    fun setSheetsWebhookUrl(url: String) {
        _sheetsWebhookUrl.value = url.trim()
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

    fun lookupLpg(crewId: String) {
        val trimmed = crewId.trim()
        if (trimmed.isEmpty()) {
            _lpgLookupResult.value = null
            return
        }
        viewModelScope.launch {
            val found = repository.findCrewById(trimmed)
            _lpgLookupResult.value = found
        }
    }

    fun lookupAlp(crewId: String) {
        val trimmed = crewId.trim()
        if (trimmed.isEmpty()) {
            _alpLookupResult.value = null
            return
        }
        viewModelScope.launch {
            val found = repository.findCrewById(trimmed)
            _alpLookupResult.value = found
        }
    }

    fun clearCrewLookup() {
        _crewLookupResult.value = null
    }

    // Long Hour Duty Operations
    fun submitLongHourDuty(
        lpgId: String,
        lpgName: String,
        alpId: String,
        alpName: String,
        trainNo: String,
        locoNo: String,
        signOnDate: String,
        signOnTime: String,
        direction: String,
        currentStationCode: String,
        arrivalTimeCurrentStation: String,
        currentTrainPosition: String,
        positionTiming: String
    ) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            val createdAt = sdf.format(Date())

            val record = LongHourDutyRecord(
                lpgId = lpgId.trim().uppercase(),
                lpgName = lpgName.trim(),
                alpId = alpId.trim().uppercase(),
                alpName = alpName.trim(),
                trainNo = trainNo.trim(),
                locoNo = locoNo.trim(),
                signOnDate = signOnDate.trim(),
                signOnTime = signOnTime.trim(),
                direction = direction.trim(),
                currentStationCode = currentStationCode.trim().uppercase(),
                arrivalTimeCurrentStation = arrivalTimeCurrentStation.trim(),
                currentTrainPosition = currentTrainPosition.trim(),
                positionTiming = positionTiming.trim(),
                isClosed = false,
                createdAt = createdAt
            )

            val newId = repository.insertLongHourRecord(record)
            val insertedRecord = record.copy(id = newId)

            // Auto-save to Google Sheet
            val durationStr = calculateDutyDuration(signOnDate, signOnTime)
            if (_sheetsWebhookUrl.value.isNotBlank()) {
                repository.syncLongHourToSheet(_sheetsWebhookUrl.value, insertedRecord, durationStr)
            }

            _uiMessage.emit("लॉन्ग आवर ड्यूटी डाटा सफलतापूर्वक दर्ज हुआ! (Train No: $trainNo)")
        }
    }

    fun closeLongHourDutyByAdmin(
        id: Long,
        reliefDate: String,
        reliefTime: String,
        reliefStationCode: String,
        record: LongHourDutyRecord
    ) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            val closedAt = sdf.format(Date())
            val adminId = inChargeAuthManager.currentAdminUser

            repository.closeLongHourDuty(
                id = id,
                reliefDate = reliefDate.trim(),
                reliefTime = reliefTime.trim(),
                reliefStationCode = reliefStationCode.trim().uppercase(),
                closedBy = adminId,
                closedAt = closedAt
            )

            // Calculate final total duty hours duration
            val durationStr = calculateDutyDurationBetween(
                record.signOnDate,
                record.signOnTime,
                reliefDate.trim(),
                reliefTime.trim()
            )

            val updatedRecord = record.copy(
                isClosed = true,
                reliefDate = reliefDate.trim(),
                reliefTime = reliefTime.trim(),
                reliefStationCode = reliefStationCode.trim().uppercase(),
                closedBy = adminId,
                closedAt = closedAt
            )

            // Auto-save update to Google Sheet
            if (_sheetsWebhookUrl.value.isNotBlank()) {
                repository.syncLongHourToSheet(_sheetsWebhookUrl.value, updatedRecord, durationStr)
            }

            _uiMessage.emit("ड्यूटी सफलतापूर्वक क्लोज की गई (रिलीव स्टेशन: ${reliefStationCode.uppercase()})")
        }
    }

    fun deleteLongHourRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteLongHourRecord(id)
            _uiMessage.emit("लॉन्ग आवर रिकॉर्ड हटाया गया")
        }
    }

    // Helper functions for duty hours calculation
    fun calculateDutyDuration(signOnDate: String, signOnTime: String): String {
        return try {
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            val signOn = sdf.parse("$signOnDate $signOnTime") ?: return "00h 00m"
            val now = Date()
            val diffMs = now.time - signOn.time
            if (diffMs < 0) return "00h 00m"
            val totalMinutes = diffMs / (60 * 1000)
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            String.format(Locale.getDefault(), "%02dh %02dm", hours, minutes)
        } catch (e: Exception) {
            "00h 00m"
        }
    }

    fun calculateDutyDurationBetween(
        signOnDate: String,
        signOnTime: String,
        reliefDate: String,
        reliefTime: String
    ): String {
        return try {
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            val start = sdf.parse("$signOnDate $signOnTime") ?: return "00h 00m"
            val end = sdf.parse("$reliefDate $reliefTime") ?: return "00h 00m"
            val diffMs = end.time - start.time
            if (diffMs < 0) return "00h 00m"
            val totalMinutes = diffMs / (60 * 1000)
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            String.format(Locale.getDefault(), "%02dh %02dm", hours, minutes)
        } catch (e: Exception) {
            "00h 00m"
        }
    }

    // Store Fast Issue/Return
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

    // PR operations
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

            val newId = repository.insertPrRequest(request)
            val savedRequest = request.copy(id = newId)

            val webhook = _sheetsWebhookUrl.value
            if (webhook.isNotBlank()) {
                repository.syncPrRemarkToSheets(webhook, savedRequest)
            }

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

            val webhook = _sheetsWebhookUrl.value
            if (webhook.isNotBlank()) {
                val current = allPrRequests.value.find { it.id == requestId }
                if (current != null) {
                    val updated = current.copy(
                        status = status,
                        remarks = remarks,
                        reviewedBy = adminId,
                        reviewedAt = reviewedAt
                    )
                    repository.syncPrRemarkToSheets(webhook, updated)
                }
            }

            _uiMessage.emit("PR अनुरोध $status अपडेट किया गया (रिमार्क: $remarks)")
        }
    }

    fun deletePrRequest(requestId: Long) {
        viewModelScope.launch {
            repository.deletePrRequest(requestId)
            _uiMessage.emit("PR अनुरोध हटाया गया")
        }
    }

    fun autoDetectShiftSlot(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour in 6..13 -> "06:00 - 14:00"
            hour in 14..21 -> "14:00 - 22:00"
            else -> "22:00 - 06:00"
        }
    }

    fun lookupStoreAdmin(crewId: String) {
        val trimmed = crewId.trim()
        if (trimmed.isEmpty()) {
            _storeAdminMember.value = null
            return
        }
        viewModelScope.launch {
            val found = repository.findCrewById(trimmed)
            _storeAdminMember.value = found
        }
    }

    fun setStoreShiftSlot(slot: String) {
        _storeShiftSlot.value = slot
    }

    fun setStoreShiftDate(date: String) {
        _storeShiftDate.value = date
    }

    fun submitStoreIssue(
        issueDate: String,
        crewId: String,
        crewName: String,
        designation: String,
        roleType: String,
        toTime: String,
        toBooked: String,
        walkieTalkieBrand: String?,
        walkieTalkieNo: String?,
        spareBatteryNo: String?,
        detonatorNo: String?,
        fsdBrand: String?,
        fsdNo: String?,
        notes: String
    ) {
        viewModelScope.launch {
            val admin = _storeAdminMember.value
            val currentShift = _storeShiftSlot.value
            val nowTime = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())

            val autoApprove = admin != null
            val status = if (autoApprove) "APPROVED" else "ISSUED_PENDING_APPROVAL"

            val record = StoreIssueRecord(
                issueDate = issueDate,
                crewId = crewId,
                crewName = crewName,
                designation = designation,
                roleType = roleType,
                toTime = toTime,
                toBooked = toBooked,
                walkieTalkieBrand = walkieTalkieBrand,
                walkieTalkieNo = walkieTalkieNo,
                spareBatteryNo = spareBatteryNo,
                detonatorNo = detonatorNo,
                fsdBrand = fsdBrand,
                fsdNo = fsdNo,
                notes = notes,
                status = status,
                approvedByAdminId = if (autoApprove) admin?.crewId else null,
                approvedByAdminName = if (autoApprove) admin?.name else null,
                approvedAt = if (autoApprove) nowTime else null,
                issueShift = currentShift
            )

            val newId = repository.insertStoreRecord(record)
            val savedRecord = record.copy(id = newId)

            val webhook = _sheetsWebhookUrl.value
            if (webhook.isNotBlank()) {
                repository.syncStoreRecordToSheets(webhook, savedRecord)
            }

            _uiMessage.emit("सामान सफलतापूर्वक जारी किया गया! (Crew: $crewId)")
        }
    }

    fun approveStoreIssue(record: StoreIssueRecord) {
        viewModelScope.launch {
            val admin = _storeAdminMember.value
            val adminId = admin?.crewId ?: currentAdminUser
            val adminName = admin?.name ?: "Store In-Charge"
            val nowTime = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())

            repository.approveStoreIssue(record.id, adminId, adminName, nowTime)

            val webhook = _sheetsWebhookUrl.value
            if (webhook.isNotBlank()) {
                repository.syncStoreRecordToSheets(
                    webhook,
                    record.copy(
                        status = "APPROVED",
                        approvedByAdminId = adminId,
                        approvedByAdminName = adminName,
                        approvedAt = nowTime
                    )
                )
            }

            _uiMessage.emit("सामान स्वीकृति सफल! (ID: ${record.crewId})")
        }
    }

    fun submitStoreReturn(
        recordId: Long,
        choDate: String,
        choTime: String,
        returnNotes: String
    ) {
        viewModelScope.launch {
            val admin = _storeAdminMember.value
            val nowTime = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())

            repository.submitStoreReturn(recordId, choDate, choTime, returnNotes)

            // If admin is active on duty, auto approve return
            if (admin != null) {
                repository.approveStoreReturn(recordId, admin.crewId, admin.name, nowTime)
            }

            val webhook = _sheetsWebhookUrl.value
            if (webhook.isNotBlank()) {
                val record = allStoreRecords.value.find { it.id == recordId }
                if (record != null) {
                    val updated = record.copy(
                        isReturned = true,
                        choDate = choDate,
                        choTime = choTime,
                        returnNotes = returnNotes,
                        status = if (admin != null) "RETURN_APPROVED" else "RETURNED_PENDING_APPROVAL",
                        returnApprovedByAdminId = admin?.crewId,
                        returnApprovedByAdminName = admin?.name,
                        returnApprovedAt = if (admin != null) nowTime else null
                    )
                    repository.syncStoreRecordToSheets(webhook, updated)
                }
            }

            _uiMessage.emit("सामान वापसी (CHO) दर्ज कर दी गई!")
        }
    }

    fun approveStoreReturn(record: StoreIssueRecord) {
        viewModelScope.launch {
            val admin = _storeAdminMember.value
            val adminId = admin?.crewId ?: currentAdminUser
            val adminName = admin?.name ?: "Store In-Charge"
            val nowTime = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())

            repository.approveStoreReturn(record.id, adminId, adminName, nowTime)

            val webhook = _sheetsWebhookUrl.value
            if (webhook.isNotBlank()) {
                repository.syncStoreRecordToSheets(
                    webhook,
                    record.copy(
                        status = "RETURN_APPROVED",
                        returnApprovedByAdminId = adminId,
                        returnApprovedByAdminName = adminName,
                        returnApprovedAt = nowTime
                    )
                )
            }

            _uiMessage.emit("वापसी स्वीकृति सफल! (Crew: ${record.crewId})")
        }
    }

    fun deleteStoreRecord(recordId: Long) {
        viewModelScope.launch {
            repository.deleteStoreRecord(recordId)
            _uiMessage.emit("स्टोर रिकॉर्ड हटाया गया")
        }
    }

    fun saveAndSyncShiftSummary() {
        viewModelScope.launch {
            val admin = _storeAdminMember.value
            if (admin == null) {
                _uiMessage.emit("कृपया पहले स्टोर एडमिन की Crew ID दर्ज करें")
                return@launch
            }

            val date = _storeShiftDate.value
            val slot = _storeShiftSlot.value
            val shiftRecords = allStoreRecords.value.filter {
                it.issueDate == date && (it.issueShift == slot || it.issueShift.isEmpty())
            }

            val totalWt = shiftRecords.count { !it.walkieTalkieNo.isNullOrBlank() }
            val totalBat = shiftRecords.count { !it.spareBatteryNo.isNullOrBlank() }
            val totalDet = shiftRecords.count { !it.detonatorNo.isNullOrBlank() }
            val totalFsd = shiftRecords.count { !it.fsdNo.isNullOrBlank() }
            val totalRet = shiftRecords.count { it.isReturned }

            val shiftRecord = StoreShiftRecord(
                shiftDate = date,
                shiftSlot = slot,
                adminCrewId = admin.crewId,
                adminName = admin.name,
                adminDesignation = admin.designation,
                loginTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                totalWalkieTalkieIssued = totalWt,
                totalBatteryIssued = totalBat,
                totalDetonatorIssued = totalDet,
                totalFsdIssued = totalFsd,
                totalReturnsReceived = totalRet
            )

            val id = repository.insertStoreShiftRecord(shiftRecord)
            val webhook = _sheetsWebhookUrl.value
            if (webhook.isNotBlank()) {
                repository.syncStoreShiftSummaryToSheets(webhook, shiftRecord.copy(id = id))
            }

            _uiMessage.emit("शिफ्ट सारांश Google Sheets व डेटाबेस में सुरक्षित किया गया!")
        }
    }

    fun syncWithGoogleSheets() {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                // If sheets webhook is configured, sync
                val webhook = _sheetsWebhookUrl.value
                val success = if (webhook.isNotBlank()) {
                    repository.syncWithGoogleSheets(webhook)
                } else {
                    true
                }
                if (success) {
                    _uiMessage.emit("Google Sheets सिंक सफल!")
                } else {
                    _uiMessage.emit("Google Sheets सिंक में समस्या आई")
                }
            } catch (e: Exception) {
                _uiMessage.emit("सिंक में त्रुटि: ${e.message}")
            } finally {
                _isSyncing.value = false
            }
        }
    }

    // ==================== JEEP MOVEMENT & AVAILABILITY ====================
    val allCrewMembers: StateFlow<List<CrewMember>> = flow {
        emit(repository.loadCrewMaster())
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allDirectoryContacts: StateFlow<List<com.example.data.StaffContact>> = flow {
        emit(repository.loadDirectoryContacts())
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allJeepMovements: StateFlow<List<JeepMovementRecord>> = repository.allJeepMovements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val jeepAvailabilityList: StateFlow<List<JeepAvailabilityItem>> = allJeepMovements
        .map { movements -> calculateJeepAvailability(movements) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            calculateJeepAvailability(emptyList())
        )

    private fun calculateJeepAvailability(movements: List<JeepMovementRecord>): List<JeepAvailabilityItem> {
        val coreJeeps = listOf("89", "89(ll)", "91", "22", "31", "79", "Breakdown")
        val availableList = mutableListOf<JeepAvailabilityItem>()
        val onMovementList = mutableListOf<JeepAvailabilityItem>()

        val sdfDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val todayStr = sdfDate.format(Date())

        for (jeepNo in coreJeeps) {
            val latest = movements.filter { it.jeepNo == jeepNo }.maxByOrNull { it.timestamp }

            // Determine if jeep is currently on movement or available
            val isOnMovement = latest != null && !latest.isCompleted && latest.returningArrivalTime.isBlank()

            if (isOnMovement && latest != null) {
                onMovementList.add(
                    JeepAvailabilityItem(
                        jeepNo = jeepNo,
                        isAvailable = false,
                        turnNumber = 0,
                        lastArrivalDate = latest.arrivalDate.ifBlank { todayStr },
                        lastArrivalTime = latest.arrivalTime.ifBlank { latest.departureTime },
                        arrivalTimestamp = Long.MAX_VALUE,
                        driverName = latest.driverName,
                        currentLocation = latest.toStation.ifBlank { "On Road" },
                        movementDestination = latest.toStation,
                        departureTime = latest.departureTime,
                        departureDate = latest.departureDate,
                        crewSummary = latest.outwardCrews,
                        statusDescription = "On Duty to ${latest.toStation} (${latest.departureTime})"
                    )
                )
            } else {
                val arrDate = latest?.returningArrivalDate?.ifBlank { latest.arrivalDate.ifBlank { todayStr } } ?: todayStr
                val arrTime = latest?.returningArrivalTime?.ifBlank { latest.arrivalTime.ifBlank { getDefaultJeepArrivalTime(jeepNo) } } ?: getDefaultJeepArrivalTime(jeepNo)
                val timestamp = parseJeepTimestamp(arrDate, arrTime, latest?.timestamp ?: getDefaultJeepTimestamp(jeepNo))

                availableList.add(
                    JeepAvailabilityItem(
                        jeepNo = jeepNo,
                        isAvailable = true,
                        turnNumber = 0, // Assigned after sorting
                        lastArrivalDate = arrDate,
                        lastArrivalTime = arrTime,
                        arrivalTimestamp = timestamp,
                        driverName = latest?.driverName ?: "Lobby Driver",
                        currentLocation = "KHS Lobby",
                        movementDestination = "",
                        departureTime = "",
                        departureDate = "",
                        crewSummary = latest?.returningCrews ?: "",
                        statusDescription = if (latest != null) "Available at KHS Lobby (Returned from ${latest.returningFromStation.ifBlank { latest.toStation }})" else "Available at KHS Lobby"
                    )
                )
            }
        }

        // Sort available jeeps by last arrival time ascending (Earliest arrival = Turn #1)
        val sortedAvailable = availableList.sortedBy { it.arrivalTimestamp }.mapIndexed { index, item ->
            item.copy(turnNumber = index + 1)
        }

        return sortedAvailable + onMovementList
    }

    private fun getDefaultJeepArrivalTime(jeepNo: String): String {
        return when (jeepNo) {
            "89" -> "05:30"
            "89(ll)" -> "06:15"
            "91" -> "07:00"
            "22" -> "07:45"
            "31" -> "08:30"
            "79" -> "09:15"
            else -> "10:00"
        }
    }

    private fun getDefaultJeepTimestamp(jeepNo: String): Long {
        val calendar = Calendar.getInstance()
        val minutes = when (jeepNo) {
            "89" -> 5 * 60 + 30
            "89(ll)" -> 6 * 60 + 15
            "91" -> 7 * 60
            "22" -> 7 * 60 + 45
            "31" -> 8 * 60 + 30
            "79" -> 9 * 60 + 15
            else -> 10 * 60
        }
        calendar.set(Calendar.HOUR_OF_DAY, minutes / 60)
        calendar.set(Calendar.MINUTE, minutes % 60)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun parseJeepTimestamp(dateStr: String, timeStr: String, fallback: Long): Long {
        return try {
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            sdf.parse("$dateStr $timeStr")?.time ?: fallback
        } catch (e: Exception) {
            fallback
        }
    }

    fun submitJeepMovement(record: JeepMovementRecord, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val newId = repository.insertJeepMovement(record)
                val savedRecord = record.copy(id = newId)

                val webhook = _sheetsWebhookUrl.value
                if (webhook.isNotBlank()) {
                    repository.syncJeepMovementToSheets(webhook, savedRecord)
                }

                _uiMessage.emit("जीप ${record.jeepNo} का मूवमेंट सफलतापूर्वक दर्ज हुआ!")
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiMessage.emit("मूवमेंट दर्ज करने में त्रुटि: ${e.message}")
                onResult(false)
            }
        }
    }

    fun deleteJeepMovement(id: Long) {
        viewModelScope.launch {
            repository.deleteJeepMovement(id)
            _uiMessage.emit("मूवमेंट रिकॉर्ड हटा दिया गया")
        }
    }

    // Roster & TLC Operations
    fun submitRosterTlc(record: RosterTlcRecord, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val newId = repository.insertRosterTlcRecord(record)
                val savedRecord = record.copy(id = newId)

                val webhook = _sheetsWebhookUrl.value
                if (webhook.isNotBlank()) {
                    repository.syncRosterTlcToSheets(webhook, savedRecord)
                }

                _uiMessage.emit("रोस्टर एवं TLC अपडेट (${record.shiftTiming}) सफलतापूर्वक दर्ज हुआ!")
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiMessage.emit("रोस्टर दर्ज करने में त्रुटि: ${e.message}")
                onResult(false)
            }
        }
    }

    fun deleteRosterTlc(id: Long) {
        viewModelScope.launch {
            repository.deleteRosterTlcRecord(id)
            _uiMessage.emit("रोस्टर रिकॉर्ड हटा दिया गया")
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
