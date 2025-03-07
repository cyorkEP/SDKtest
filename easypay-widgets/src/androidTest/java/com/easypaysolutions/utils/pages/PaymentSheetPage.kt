package com.easypaysolutions.utils.pages

import android.view.View
import android.widget.Button
import androidx.annotation.StringRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.easypaysolutions.common.presentation.manage_cards.CardsAdapter
import com.easypaysolutions.utils.hasItemCount
import com.easypaysolutions.utils.waitUntil
import com.easypaysolutions.utils.waitUntilGone
import com.easypaysolutions.widgets.R
import org.hamcrest.Matchers.greaterThan

internal class PaymentSheetPage {

    fun waitForCards() {
        onView(withId(R.id.main_progress_view))
            .perform(waitUntilGone(3000))
        onView(withId(R.id.rv_cards))
            .perform(waitUntil(hasItemCount(greaterThan(1))))
    }

    fun selectCard(position: Int = 1) {
        onView(withId(R.id.rv_cards))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<CardsAdapter.SavedCardViewHolder>(
                    position,
                    object : ViewAction {
                        override fun getConstraints() =
                            ViewMatchers.isAssignableFrom(View::class.java)

                        override fun getDescription() = "Click on the card"
                        override fun perform(
                            uiController: UiController,
                            view: View,
                        ) {
                            val btnAction: Button? = view.findViewById(R.id.btn_action)
                            btnAction?.performClick()
                        }
                    }
                )
            )
    }

    fun clickOnCompleteButton() {
        onView(withId(R.id.btn_complete))
            .perform(ViewActions.click())
    }

    fun clickOnDeleteButton() {
        onView(withId(R.id.btn_delete_card))
            .perform(ViewActions.click())
    }

    fun waitForGeneralErrorText() {
        onView(withId(R.id.tv_error))
            .perform(waitUntil(isDisplayed()))
    }

    fun waitForSnackbarSuccess(@StringRes messageId: Int) {
        onView(withId(R.id.progress_view))
            .perform(waitUntilGone(2000))
        onView(withText(messageId))
            .check(matches(isDisplayed()))
    }

    fun navigateToAddNewCard() {
        selectCard(0)
    }

    fun clickOnCloseButton() {
        onView(withId(R.id.btn_close))
            .perform(ViewActions.click())
    }

}