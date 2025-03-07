package com.easypaysolutions.utils.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.easypaysolutions.utils.waitUntil
import com.easypaysolutions.utils.waitUntilGone
import com.easypaysolutions.widgets.R
import org.hamcrest.Matchers.allOf

internal class AddNewCardPage {

    fun fillCardData(cardNumber: String = "4242 4242 4242 4242") {
        onView(withId(R.id.section_card_information))
            .inRoot(isDialog())
            .perform(waitUntil(isDisplayed()))

        fillField(R.id.et_card_holder_name, "Android Test ${System.currentTimeMillis()}")
        fillField(R.id.et_card_number, cardNumber)
        fillField(R.id.et_expiry_date, getRandomExpiryDate())
        fillField(R.id.et_cvc, (100..999).random().toString())
        fillField(R.id.et_street_address, "Test Address")
        fillField(R.id.et_zip, (10000..99999).random().toString())

        clickOnView(R.id.et_card_holder_name)
    }

    fun clickOnPrimaryActionButton() {
        onView(withId(R.id.btn_complete))
            .inRoot(isDialog())
            .perform(click())
    }

    fun waitForErrorText() {
        onView(withId(R.id.tv_main_error))
            .inRoot(isDialog())
            .perform(waitUntil(isDisplayed()))
    }

    fun waitUntilIsGone() {
        onView(withId(R.id.progress_view))
            .inRoot(isDialog())
            .perform(waitUntilGone(3000))
    }

    //region Helpers

    private fun fillField(fieldId: Int, text: String) {
        onView(allOf(isDescendantOfA(withId(fieldId)), withId(R.id.edit_text)))
            .inRoot(isDialog())
            .perform(typeText(text))
    }

    private fun clickOnView(fieldId: Int) {
        onView(allOf(isDescendantOfA(withId(fieldId)), withId(R.id.edit_text)))
            .inRoot(isDialog())
            .perform(click())
    }

    private fun getRandomExpiryDate(): String {
        return "${(10..12).random()}/${(30..40).random()}"
    }

    //endregion

}