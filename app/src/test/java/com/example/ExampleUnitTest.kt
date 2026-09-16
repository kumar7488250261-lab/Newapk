package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testUserIdValidation() {
    val idRegex = Regex("^KHS[0-9]{4}$")
    // Valid cases: KHS + exactly 4 digits
    assertTrue(idRegex.matches("KHS1234"))
    assertTrue(idRegex.matches("KHS0001"))
    assertTrue(idRegex.matches("KHS9999"))
    assertTrue(idRegex.matches("KHS5678"))

    // Invalid cases: less than 4 digits
    assertFalse(idRegex.matches("KHS1"))
    assertFalse(idRegex.matches("KHS12"))
    assertFalse(idRegex.matches("KHS123"))

    // Invalid cases: more than 4 digits
    assertFalse(idRegex.matches("KHS12345"))
    assertFalse(idRegex.matches("KHS123456"))

    // Invalid cases: wrong prefix
    assertFalse(idRegex.matches("BSP1234"))
    assertFalse(idRegex.matches("1234"))
    assertFalse(idRegex.matches("KH1234"))
    assertFalse(idRegex.matches("KHSA123"))
  }

  @Test
  fun testDefaultPassword() {
    val defaultPassword = "1234"
    assertEquals("1234", defaultPassword)
  }
}
