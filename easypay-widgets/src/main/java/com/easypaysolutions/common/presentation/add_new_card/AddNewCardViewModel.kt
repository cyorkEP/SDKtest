package com.easypaysolutions.common.presentation.add_new_card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypaysolutions.utils.validation.ValidationState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal class AddNewCardViewModel(
    private val workContext: CoroutineContext,
) : ViewModel() {

    private val _validationState = MutableSharedFlow<ValidationState>()
    val validationState: SharedFlow<ValidationState> = _validationState

    //region Validation

    fun updateValidationState(state: ValidationState) {
        viewModelScope.launch(workContext) {
            _validationState.emit(state)
        }
    }

    //endregion

}