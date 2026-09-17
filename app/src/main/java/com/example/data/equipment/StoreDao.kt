package com.example.data.equipment

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {

    @Query("SELECT * FROM store_issue_records ORDER BY id DESC")
    fun getAllRecords(): Flow<List<StoreIssueRecord>>

    @Query("SELECT * FROM store_issue_records WHERE isReturned = 0 ORDER BY id DESC")
    fun getActiveIssuedRecords(): Flow<List<StoreIssueRecord>>

    @Query("SELECT * FROM store_issue_records WHERE status = 'ISSUED_PENDING_APPROVAL' ORDER BY id DESC")
    fun getPendingApprovalRecords(): Flow<List<StoreIssueRecord>>

    @Query("SELECT * FROM store_issue_records WHERE status = 'RETURNED_PENDING_APPROVAL' ORDER BY id DESC")
    fun getPendingReturnApprovalRecords(): Flow<List<StoreIssueRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: StoreIssueRecord): Long

    @Update
    suspend fun updateRecord(record: StoreIssueRecord)

    @Query("""
        UPDATE store_issue_records 
        SET status = 'APPROVED', 
            approvedByAdminId = :adminId, 
            approvedByAdminName = :adminName, 
            approvedAt = :timestamp 
        WHERE id = :id
    """)
    suspend fun approveIssue(id: Long, adminId: String, adminName: String, timestamp: String)

    @Query("""
        UPDATE store_issue_records 
        SET isReturned = 1, 
            status = 'RETURNED_PENDING_APPROVAL', 
            choDate = :choDate, 
            choTime = :choTime, 
            returnNotes = :returnNotes 
        WHERE id = :id
    """)
    suspend fun submitReturn(id: Long, choDate: String, choTime: String, returnNotes: String)

    @Query("""
        UPDATE store_issue_records 
        SET status = 'RETURN_APPROVED', 
            returnApprovedByAdminId = :adminId, 
            returnApprovedByAdminName = :adminName, 
            returnApprovedAt = :timestamp 
        WHERE id = :id
    """)
    suspend fun approveReturn(id: Long, adminId: String, adminName: String, timestamp: String)

    @Query("DELETE FROM store_issue_records WHERE id = :id")
    suspend fun deleteRecord(id: Long)

    // Shift Records
    @Query("SELECT * FROM store_shift_records ORDER BY id DESC")
    fun getAllShiftRecords(): Flow<List<StoreShiftRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShiftRecord(record: StoreShiftRecord): Long

    @Update
    suspend fun updateShiftRecord(record: StoreShiftRecord)
}
