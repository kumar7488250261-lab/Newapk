package com.example

import com.example.data.equipment.*
import org.junit.Assert.*
import org.junit.Test

class EquipmentRegisterUnitTest {

    @Test
    fun testDesignationCategoryMapping() {
        // LPG cases
        val lpg1 = CrewMember(1, "KHS1001", "R. K. Sharma", "LPG")
        assertEquals(DesignationCategory.LPG, lpg1.category)

        val lpg2 = CrewMember(2, "KHS1002", "Amit Kumar", "LP GOODS")
        assertEquals(DesignationCategory.LPG, lpg2.category)

        // Train Manager / Guard cases
        val tm1 = CrewMember(3, "KHS2001", "S. P. Singh", "GD")
        assertEquals(DesignationCategory.TRAIN_MANAGER, tm1.category)

        val tm2 = CrewMember(4, "KHS2002", "Vikas Patel", "SGD")
        assertEquals(DesignationCategory.TRAIN_MANAGER, tm2.category)

        val tm3 = CrewMember(5, "KHS2003", "Anil Yadav", "Train Manager")
        assertEquals(DesignationCategory.TRAIN_MANAGER, tm3.category)

        // ALP cases
        val alp1 = CrewMember(6, "KHS3001", "Manoj Verma", "ALP")
        assertEquals(DesignationCategory.ALP, alp1.category)

        val alp2 = CrewMember(7, "KHS3002", "Pankaj Das", "SALP")
        assertEquals(DesignationCategory.ALP, alp2.category)
    }

    @Test
    fun testCrewIdNumericMatchingLogic() {
        val testCrewList = listOf(
            CrewMember(1, "KHS1001", "Sample Driver", "LPG"),
            CrewMember(2, "KHS1398", "Sample Guard", "GD"),
            CrewMember(3, "KHS7010", "Sample ALP", "ALP")
        )

        // Find by full ID
        val fullMatch = testCrewList.find { it.crewId.equals("KHS1001", ignoreCase = true) }
        assertNotNull(fullMatch)
        assertEquals("Sample Driver", fullMatch?.name)

        // Find by numeric suffix
        val numericQuery = "1398"
        val numMatch = testCrewList.find { it.crewId.removePrefix("KHS").equals(numericQuery, ignoreCase = true) }
        assertNotNull(numMatch)
        assertEquals("Sample Guard", numMatch?.name)
        assertEquals(DesignationCategory.TRAIN_MANAGER, numMatch?.category)
    }

    @Test
    fun testApprovalWorkflowStatusTransitions() {
        // Initial staff submission has status PENDING_ISSUE_APPROVAL
        val initialRecord = EquipmentRecord(
            id = 101L,
            crewId = "KHS1001",
            crewName = "R. K. Sharma",
            designation = "LPG",
            designationCategory = DesignationCategory.LPG.name,
            vhfRadioNo = "MOT-8890",
            vhfSpareBatteryNo = "BAT-441",
            detonatorBoxNo = "DT-12",
            trainOrToBooked = "BOXN/RIG",
            issueDateTime = "16-09-2026 14:00",
            status = EquipmentStatus.PENDING_ISSUE_APPROVAL.name
        )
        assertEquals(EquipmentStatus.PENDING_ISSUE_APPROVAL.name, initialRecord.status)

        // In-Charge approves issue
        val approvedIssueRecord = initialRecord.copy(
            status = EquipmentStatus.ISSUED.name,
            issueApprovedBy = "KHS_SUPERVISOR_1",
            issueApprovedAt = "16-09-2026 14:05"
        )
        assertEquals(EquipmentStatus.ISSUED.name, approvedIssueRecord.status)
        assertEquals("KHS_SUPERVISOR_1", approvedIssueRecord.issueApprovedBy)

        // Staff submits return
        val pendingReturnRecord = approvedIssueRecord.copy(
            status = EquipmentStatus.PENDING_RETURN_APPROVAL.name,
            returnDateTime = "16-09-2026 22:30",
            batteryStatus = "Not Used",
            vhfCondition = "OK"
        )
        assertEquals(EquipmentStatus.PENDING_RETURN_APPROVAL.name, pendingReturnRecord.status)

        // In-Charge verifies and accepts return
        val closedReturnRecord = pendingReturnRecord.copy(
            status = EquipmentStatus.RETURNED.name,
            returnApprovedBy = "KHS_SUPERVISOR_1",
            choVerifiedAt = "16-09-2026 22:35"
        )
        assertEquals(EquipmentStatus.RETURNED.name, closedReturnRecord.status)
        assertEquals("KHS_SUPERVISOR_1", closedReturnRecord.returnApprovedBy)
    }
}
