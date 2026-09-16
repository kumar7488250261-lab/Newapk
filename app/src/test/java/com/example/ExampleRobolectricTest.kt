package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.StaffRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Kharsia Lobby", appName)
  }

  @Test
  fun `verify staff repository loads lobbies and kharsia staff`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = StaffRepository(context)
    val lobbies = repository.getLobbies()

    assertTrue("Should have 14 lobbies", lobbies.size >= 14)

    val kharsia = repository.getLobbyById("kharsia")
    assertNotNull("Kharsia lobby should exist", kharsia)
    assertTrue("Kharsia should have contacts", (kharsia?.totalContacts ?: 0) > 0)

    val searchResults = repository.searchAllStaff("Kharsia")
    assertTrue("Search should return staff contacts", searchResults.isNotEmpty())
  }
}

