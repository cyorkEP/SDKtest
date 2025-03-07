package com.easypay_sample.ui.charge_card

object ChargeCardHelper {
    fun getPrefilledViewData(): ChargeCardViewData = ChargeCardViewData(
        merchantId = "1",
        expMonth = "10",
        expYear = "26",
        cvv = "999",
        holderFirstName = "Name",
        holderLastName = "Surname",
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
        totalAmount = "10",
        salesAmount = "0",
        surcharge = "0",
        serviceDescription = "FROM API TESTER",
        clientRefId = "12456",
        rpguid = "3d3424a6-c5f3-4c28"
    )
}
