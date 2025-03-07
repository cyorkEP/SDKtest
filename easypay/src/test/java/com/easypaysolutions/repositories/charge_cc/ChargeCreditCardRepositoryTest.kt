package com.easypaysolutions.repositories.charge_cc

import com.easypaysolutions.networking.NetworkResource
import com.easypaysolutions.utils.ChargeCreditCardHelper.prepareParams
import com.easypaysolutions.utils.TestApiHelper
import com.easypaysolutions.utils.secured.SecureData
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class ChargeCreditCardRepositoryTest {

    private val apiHelper = TestApiHelper()
    private val repository: ChargeCreditCardRepository = ChargeCreditCardRepositoryImpl(apiHelper)
    private val testSecureData = SecureData("encryptedData")

    //region Tests

    @Test
    fun `chargeCreditCard() with valid params returns Success`() = runBlocking {
        val params = prepareParams(testSecureData)
        val result = repository.chargeCreditCard(params)
        assertEquals(result.status, NetworkResource.Status.SUCCESS)
    }

    @Test
    fun `chargeCreditCard() with too long ZIP parameter returns Error for MaxLength`() =
        runBlocking {
            val tooLongZip = "99999999999999999999999"    //should be limited to 20 digits
            val params = prepareParams(testSecureData, zip = tooLongZip)
            val result = repository.chargeCreditCard(params)
            assertEquals(result.status, NetworkResource.Status.ERROR)
            assertEquals(result.error?.message, "zip exceeds maximum length of 20 characters")
        }

    @Test
    fun `chargeCreditCard() with invalid characters in ZIP returns Error for InvalidCharacters`() =
        runBlocking {
            val invalidZip = "999?"    //cannot contain question mark
            val params = prepareParams(testSecureData, zip = invalidZip)
            val result = repository.chargeCreditCard(params)
            assertEquals(result.status, NetworkResource.Status.ERROR)
            assertEquals(result.error?.message, "zip contains invalid characters")
        }

    @Test
    fun `chargeCreditCard() with negative TotalAmount returns Error for DoubleNotGreaterThanZero`() =
        runBlocking {
            val invalidTotalAmount: Double = -1.0    //double cannot be negative
            val params = prepareParams(testSecureData, totalAmount = invalidTotalAmount)
            val result = repository.chargeCreditCard(params)
            assertEquals(result.status, NetworkResource.Status.ERROR)
            assertEquals(result.error?.message, "totalAmount must be greater than 0.0")
        }

    @Test
    fun `chargeCreditCard() with blank CSV returns Error for NotBlank`() = runBlocking {
        val invalidCsv = ""     //CSV cannot be blank
        val params = prepareParams(testSecureData, csv = invalidCsv)
        val result = repository.chargeCreditCard(params)
        assertEquals(result.status, NetworkResource.Status.ERROR)
        assertEquals(result.error?.message, "csv cannot be blank")
    }

    //endregion

}