package com.easypay_sample.ui.charge_card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypaysolutions.api.responses.charge_cc.ChargeCreditCardResult
import com.easypaysolutions.networking.NetworkResource
import com.easypaysolutions.repositories.charge_cc.ChargeCreditCard
import com.easypaysolutions.utils.secured.SecureData
import com.easypay_sample.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias ChargeCreditCardState = ViewState<ChargeCreditCardResult>

@HiltViewModel
class ChargeCardViewModel @Inject constructor(
    private val chargeCreditCard: ChargeCreditCard,
) : ViewModel() {

    lateinit var viewData: ChargeCardViewData
        private set

    private val _chargeCreditCardResult = MutableSharedFlow<ChargeCreditCardState>()
    val chargeCreditCardResult: SharedFlow<ChargeCreditCardState> = _chargeCreditCardResult

    //region Public methods

    fun setupViewData(isPrefilled: Boolean) {
        viewData =
            if (isPrefilled) ChargeCardHelper.getPrefilledViewData() else ChargeCardViewData()
    }

    fun chargeCreditCard(secureData: SecureData<String>) {
        viewModelScope.launch {
            _chargeCreditCardResult.emit(ViewState.Loading())
            val params = viewData.toChargeCreditCardBodyParams(secureData)
            val response = chargeCreditCard.chargeCreditCard(params)
            when (response.status) {
                NetworkResource.Status.SUCCESS -> {
                    response.data?.let {
                        _chargeCreditCardResult.emit(ViewState.Success(data = it))
                    } ?: _chargeCreditCardResult.emit(ViewState.Error())
                }

                NetworkResource.Status.ERROR -> emitError("ERROR: " + response.error?.message)
                NetworkResource.Status.DECLINED -> emitError("DECLINED: " + response.error?.message)
            }
        }
    }

    //endregion

    //region Private methods

    private suspend fun emitError(message: String?) {
        _chargeCreditCardResult.emit(
            ViewState.Error(message)
        )
    }

    //endregion

}
