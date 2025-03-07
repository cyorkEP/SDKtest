package com.easypay_sample.ui.consent_annual.add

import com.easypay_sample.utils.DateUtils
import java.util.Calendar
import java.util.GregorianCalendar

object AddAnnualConsentHelper {
    fun getPrefilledViewData(): AddAnnualConsentViewData = AddAnnualConsentViewData(
        merchantId = "1",
        expMonth = "10",
        expYear = "26",
        cvv = "999",
        holderFirstName = "Android",
        holderLastName = "Test",
        holderCompany = "Another company",
        holderPhone = "8775558472",
        holderEmail = "h4_sam.usevich@gmail.com",
        holderAddress1 = "123 Fake St.",
        holderAddress2 = "",
        holderCity = "Portland",
        holderState = "ME",
        holderZip = "04005",
        holderCountry = "USA",
        customerFirstName = "Anna",
        customerLastName = "Some surname",
        customerCompany = "",
        customerPhone = "8775558472",
        customerEmail = "h4_sam.usevich@gmail.com",
        customerAddress1 = "123 Fake St.",
        customerAddress2 = "",
        customerCity = "Portland",
        customerState = "ME",
        customerZip = "04005",
        customerCountry = "USA",
        serviceDescription = "FROM API TESTER",
        customerReferenceId = "12456",
        rpguid = "3d3424a6-c5f3-4c28",
        limitPerCharge = "1000.0",
        limitLifeTime = "100000.0",
        startDate = getTomorrowDate()
    )

    private fun getTomorrowDate(): String {
        val gc = GregorianCalendar()
        gc.add(Calendar.DATE, 1)
        return DateUtils.parseDate(gc.time) ?: ""
    }
}