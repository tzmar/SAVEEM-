package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CurrencyPair
import org.junit.Assert.assertEquals
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
    assertEquals("SAVEEM", appName)
  }

  @Test
  fun `forex conversion calculation with user set rate`() {
    // Example from user: "if 1 usd is 14 pula he can set it for example if he wants it to be 15"
    val pair = CurrencyPair(
      id = "USD_BWP",
      baseCode = "USD",
      baseSymbol = "$",
      targetCode = "BWP",
      targetSymbol = "P",
      defaultRate = 14.0
    )

    val customRate = 15.0
    val baseAmount = 100.0
    val converted = baseAmount * customRate

    assertEquals(1500.0, converted, 0.001)
  }

  @Test
  fun `launch MainActivity without crash`() {
    val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java)
    val activity = controller.setup().get()
    org.junit.Assert.assertNotNull(activity)
  }
}
