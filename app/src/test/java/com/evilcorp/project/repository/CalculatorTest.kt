package com.evilcorp.project.repository

import com.evilcorp.project.domain.Calculator
import com.evilcorp.project.domain.model.RateModel
import com.evilcorp.project.presentation.model.CurrencyRateModel
import org.junit.Assert
import org.junit.Test

class CalculatorTest {
    private val euroRates = listOf(
        RateModel("EUR", 1.0f),
        RateModel("AUD", 1.5944f),
        RateModel("BGN", 1.9864f),
        RateModel("BRL", 4.1824f),
        RateModel("CAD", 1.5184f),
        RateModel("CHF", 1.1414f)
    )

    @Test
    fun `Get CAD from 1 BGN`() {
        val calculator = Calculator(
            euroRates,
            CurrencyRateModel("BGN", "", 0, 1.0f)
        )

        val result = calculator.calculateRate("CAD")

        Assert.assertEquals(0.7644f, result)
    }

    @Test
    fun `Get CAD from 0_34 BGN`() {
        val calculator = Calculator(
            euroRates,
            CurrencyRateModel("BGN", "", 0, 0.34f)
        )

        val result = calculator.calculateRate("CAD")

        Assert.assertEquals(0.2599f, result)
    }

    @Test
    fun `Get CAD from 0_348756 BGN`() {
        val calculator = Calculator(
            euroRates,
            CurrencyRateModel("BGN", "", 0, 0.34f)
        )

        val result = calculator.calculateRate("CAD")

        Assert.assertEquals(0.2599f, result)
    }

    @Test
    fun `Get CAD from 0 BGN`() {
        val calculator = Calculator(
            euroRates,
            CurrencyRateModel("BGN", "", 0, 0.0f)
        )

        val result = calculator.calculateRate("CAD")

        Assert.assertEquals(0.0f, result)
    }

    @Test
    fun `Get CAD from -2 BGN`() {
        val calculator = Calculator(
            euroRates,
            CurrencyRateModel("BGN", "", 0, -2.0f)
        )

        val result = calculator.calculateRate("CAD")

        Assert.assertEquals(-1.5288f, result)
    }

    @Test
    fun `Calculated value rounded TO 4 decimals`() {
        val calculator = Calculator(
            euroRates,
            CurrencyRateModel("BGN", "", 0, 1.0f)
        )

        val result = calculator.calculateRate("CAD")

        Assert.assertTrue(result.toString().substringAfter('.').length == 4)
    }
}