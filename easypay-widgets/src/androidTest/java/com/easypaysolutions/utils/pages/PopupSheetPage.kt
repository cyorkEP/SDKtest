package com.easypaysolutions.utils.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.easypaysolutions.widgets.R

internal class PopupSheetPage {

    fun clickOnPrimaryActionButton() {
        onView(withId(R.id.btn_primary))
            .perform(click())
    }
}