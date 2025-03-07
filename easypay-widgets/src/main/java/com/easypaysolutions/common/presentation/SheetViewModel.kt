package com.easypaysolutions.common.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easypaysolutions.api.responses.annual_consent.AnnualConsent
import com.easypaysolutions.common.presentation.add_new_card.AddNewCardHelper
import com.easypaysolutions.common.presentation.add_new_card.AddNewCardViewData
import com.easypaysolutions.common.exceptions.EasyPayWidgetException
import com.easypaysolutions.common.utils.ConsentExcerpt
import com.easypaysolutions.customer_sheet.CustomerSheet
import com.easypaysolutions.customer_sheet.utils.CustomerSheetResult
import com.easypaysolutions.networking.NetworkResource
import com.easypaysolutions.payment_sheet.PaymentSheet
import com.easypaysolutions.payment_sheet.utils.PaymentSheetResult
import com.easypaysolutions.payment_sheet.utils.mapToPaymentSheetCompletedData
import com.easypaysolutions.repositories.annual_consent.cancel.CancelAnnualConsent
import com.easypaysolutions.repositories.annual_consent.cancel.CancelAnnualConsentBodyParams
import com.easypaysolutions.repositories.annual_consent.create.CreateAnnualConsent
import com.easypaysolutions.repositories.annual_consent.create.CreateAnnualConsentBodyParams
import com.easypaysolutions.repositories.annual_consent.list.ListAnnualConsents
import com.easypaysolutions.repositories.annual_consent.list.ListAnnualConsentsBodyParams
import com.easypaysolutions.repositories.annual_consent.process_payment.ProcessPaymentAnnual
import com.easypaysolutions.repositories.annual_consent.process_payment.ProcessPaymentAnnualParams
import com.easypaysolutions.repositories.charge_cc.AccountHolderDataParam
import com.easypaysolutions.repositories.charge_cc.ChargeCreditCard
import com.easypaysolutions.repositories.charge_cc.ChargeCreditCardBodyParams
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal class SheetViewModel private constructor(
    private var selectedCardId: Int? = null,
    private val accountHolderDataParam: AccountHolderDataParam?,
    private val listAnnualConsentsParams: ListAnnualConsentsBodyParams,
    private val listAnnualConsents: ListAnnualConsents,
    private val createAnnualConsent: CreateAnnualConsent,
    private val cancelAnnualConsent: CancelAnnualConsent,
    private val chargeCreditCard: ChargeCreditCard,
    private val processPaymentAnnual: ProcessPaymentAnnual,
    private val workContext: CoroutineContext,
) : ViewModel() {

    constructor(
        config: PaymentSheet.Configuration,
        listAnnualConsents: ListAnnualConsents,
        createAnnualConsent: CreateAnnualConsent,
        cancelAnnualConsent: CancelAnnualConsent,
        chargeCreditCard: ChargeCreditCard,
        processPaymentAnnual: ProcessPaymentAnnual,
        workContext: CoroutineContext,
    ) : this(
        selectedCardId = config.preselectedCardId,
        accountHolderDataParam = config.accountHolder,
        listAnnualConsentsParams = ListAnnualConsentsBodyParams(
            merchantId = config.consentCreator.merchantId,
            customerReferenceId = config.consentCreator.customerReferenceId,
            rpguid = config.consentCreator.rpguid
        ),
        listAnnualConsents = listAnnualConsents,
        createAnnualConsent = createAnnualConsent,
        cancelAnnualConsent = cancelAnnualConsent,
        chargeCreditCard = chargeCreditCard,
        processPaymentAnnual = processPaymentAnnual,
        workContext =  workContext,
    ) {
        paymentSheetConfig = config
    }

    constructor(
        config: CustomerSheet.Configuration,
        listAnnualConsents: ListAnnualConsents,
        createAnnualConsent: CreateAnnualConsent,
        cancelAnnualConsent: CancelAnnualConsent,
        chargeCreditCard: ChargeCreditCard,
        processPaymentAnnual: ProcessPaymentAnnual,
        workContext: CoroutineContext,
    ) : this(
        selectedCardId = config.preselectedCardId,
        accountHolderDataParam = config.accountHolder,
        listAnnualConsentsParams = ListAnnualConsentsBodyParams(
            merchantId = config.consentCreator.merchantId,
            customerReferenceId = config.consentCreator.customerReferenceId,
            rpguid = config.consentCreator.rpguid
        ),
        listAnnualConsents = listAnnualConsents,
        createAnnualConsent = createAnnualConsent,
        cancelAnnualConsent = cancelAnnualConsent,
        chargeCreditCard = chargeCreditCard,
        processPaymentAnnual = processPaymentAnnual,
        workContext = workContext,
    ) {
        customerSheetConfig = config
    }

    private var shouldRefreshAfterClose = false
    private var paymentSheetConfig: PaymentSheet.Configuration? = null
    private var customerSheetConfig: CustomerSheet.Configuration? = null

    val selectedCard: AnnualConsent?
        get() = selectedCardId?.let { selectedCardId ->
            (paymentMethods.value as? PaymentMethodsUiState.Success)?.methods?.firstOrNull { it.id == selectedCardId }
        }

    private val _addNewCardResult = MutableSharedFlow<AddNewCardUiState>()
    val addNewCardResult: SharedFlow<AddNewCardUiState> = _addNewCardResult

    private val _deleteCardResult = MutableSharedFlow<DeleteCardUiState>()
    val deleteCardResult: SharedFlow<DeleteCardUiState> = _deleteCardResult

    private val _payWithNewCardResult = MutableSharedFlow<PayWithNewCardUiState>()
    val payWithNewCardResult: SharedFlow<PayWithNewCardUiState> = _payWithNewCardResult

    private val _payWithSavedCardResult = MutableSharedFlow<PayWithSavedCardUiState>()
    val payWithSavedCardResult: SharedFlow<PayWithSavedCardUiState> = _payWithSavedCardResult

    private val _paymentMethods =
        MutableStateFlow<PaymentMethodsUiState>(PaymentMethodsUiState.Loading)
    val paymentMethods: StateFlow<PaymentMethodsUiState> = _paymentMethods

    private val _customerSheetResult = MutableSharedFlow<CustomerSheetResult>()
    val customerSheetResult: SharedFlow<CustomerSheetResult> = _customerSheetResult

    private val _paymentSheetResult = MutableSharedFlow<PaymentSheetResult>()
    val paymentSheetResult: SharedFlow<PaymentSheetResult> = _paymentSheetResult

    private val _openNewCardSheet =
        MutableStateFlow<OpenNewCardSheetUiState>(OpenNewCardSheetUiState.Idle)
    val openNewCardSheet: StateFlow<OpenNewCardSheetUiState> = _openNewCardSheet

    private val _errorState = MutableSharedFlow<Throwable>()
    val errorState: SharedFlow<Throwable> = _errorState

    var deletedConsentIDs = mutableListOf<Int>()
    var addedConsents = mutableListOf<ConsentExcerpt>()

    init {
        fetchAnnualConsents()
    }

    fun accFullName(): String {
        return accountHolderDataParam?.let {
            if (it.firstName == null) {
                it.lastName
            }
            if (it.lastName == null) {
                it.firstName
            }
            "${it.firstName} ${it.lastName}"
        } ?: ""
    }

    fun accZip(): String {
        return accountHolderDataParam?.billingAddress?.zip ?: ""
    }

    fun accAddress(): String {
        accountHolderDataParam?.billingAddress?.apply {
            val elements = listOfNotNull(address1, address2, city, state, country)
            return elements.joinToString(" ")
        }
        return ""
    }

    //region Actions

    fun setShouldRefreshAfterClose() {
        shouldRefreshAfterClose = true
    }

    fun onCardSelected(selectedCard: AnnualConsent?) {
        selectedCardId = selectedCard?.id
    }

    fun deleteSelectedCard() {
        selectedCard?.let {
            deleteCard(it)
        }
    }

    fun openNewCardSheet() {
        viewModelScope.launch(workContext) {
            _openNewCardSheet.emit(OpenNewCardSheetUiState.Open)
        }
    }

    fun closeNewCardSheet() {
        viewModelScope.launch(workContext) {
            _openNewCardSheet.emit(OpenNewCardSheetUiState.Close)
            if (shouldRefreshAfterClose) {
                fetchAnnualConsents()
            }
        }
    }

    fun payWithNewCard(viewData: AddNewCardViewData) {
        val config = paymentSheetConfig ?: return
        val params = AddNewCardHelper.toChargeCreditCardBodyParams(
            viewData = viewData,
            endCustomer = config.endCustomer,
            accountHolder = config.accountHolder,
            amounts = config.amounts,
            purchaseItems = config.purchaseItems,
            consentCreator = config.consentCreator,
        )
        if (viewData.shouldSaveCard) {
            addNewCard(config, viewData)
        } else {
            payWithNewCard(params)
        }
    }

    fun addNewCard(viewData: AddNewCardViewData) {
        val config = customerSheetConfig ?: return
        val params = AddNewCardHelper.toCreateAnnualConsentBodyParams(
            viewData = viewData,
            endCustomer = config.endCustomer,
            accountHolder = config.accountHolder,
            consentCreator = config.consentCreator,
        )
        addNewCard(params)
    }

    fun payWithSavedCard(annualConsent: AnnualConsent) {
        payWithSavedCard(annualConsent.id)
    }

    fun payWithSavedCard(consentId: Int) {
        val config = paymentSheetConfig ?: return
        val params = ProcessPaymentAnnualParams(
            consentId,
            config.amounts.totalAmount,
        )
        payWithSavedCard(params)
    }

    //endregion

    //region Completions

    fun completeWithResult(result: CustomerSheetResult) {
        viewModelScope.launch(workContext) {
            _customerSheetResult.emit(result)
        }
    }

    fun completeWithResult(result: PaymentSheetResult) {
        viewModelScope.launch(workContext) {
            _paymentSheetResult.emit(result)
        }
    }

    fun completeWithError(error: Throwable) {
        viewModelScope.launch(workContext) {
            _errorState.emit(error)
        }
    }

    //endregion

    //region Helpers

    private fun deleteCard(card: AnnualConsent) {
        viewModelScope.launch(workContext) {
            _deleteCardResult.emit(DeleteCardUiState.Loading)
            val params = CancelAnnualConsentBodyParams(card.id)
            val result = cancelAnnualConsent.cancelAnnualConsent(params)

            when (result.status) {
                NetworkResource.Status.SUCCESS -> {
                    result.data?.let {
                        deletedConsentIDs.add(it.cancelledConsentId)
                        _deleteCardResult.emit(DeleteCardUiState.Success(it))
                        onCardSelected(null)
                        fetchAnnualConsents()
                    } ?: _deleteCardResult.emit(
                        DeleteCardUiState.Error(
                            EasyPayWidgetException(
                                result.error
                            )
                        )
                    )
                }

                NetworkResource.Status.DECLINED,
                NetworkResource.Status.ERROR,
                -> {
                    _deleteCardResult.emit(DeleteCardUiState.Error(EasyPayWidgetException(result.error)))
                }
            }
        }
    }

    private fun addNewCard(config: PaymentSheet.Configuration, viewData: AddNewCardViewData) {
        val params = AddNewCardHelper.toCreateAnnualConsentBodyParams(
            viewData = viewData,
            endCustomer = config.endCustomer,
            accountHolder = config.accountHolder,
            consentCreator = config.consentCreator,
        )
        addNewCard(params)
    }

    private fun payWithNewCard(params: ChargeCreditCardBodyParams) {
        viewModelScope.launch(workContext) {
            _payWithNewCardResult.emit(PayWithNewCardUiState.Loading)
            val result = chargeCreditCard.chargeCreditCard(params)

            when (result.status) {
                NetworkResource.Status.SUCCESS -> {
                    result.data?.mapToPaymentSheetCompletedData()?.let {
                        _payWithNewCardResult.emit(PayWithNewCardUiState.Success(it))
                    } ?: _payWithNewCardResult.emit(
                        PayWithNewCardUiState.Error(
                            EasyPayWidgetException(result.error)
                        )
                    )
                }

                NetworkResource.Status.ERROR -> {
                    _payWithNewCardResult.emit(
                        PayWithNewCardUiState.Error(
                            EasyPayWidgetException(
                                result.error
                            )
                        )
                    )
                }

                NetworkResource.Status.DECLINED -> {
                    result.data?.let {
                        _payWithNewCardResult.emit(PayWithNewCardUiState.Declined(it))
                    } ?: _payWithNewCardResult.emit(
                        PayWithNewCardUiState.Error(
                            EasyPayWidgetException(result.error)
                        )
                    )
                }
            }
        }
    }

    private fun addNewCard(params: CreateAnnualConsentBodyParams) {
        viewModelScope.launch(workContext) {
            _addNewCardResult.emit(AddNewCardUiState.Loading)
            val result = createAnnualConsent.createAnnualConsent(params)

            when (result.status) {
                NetworkResource.Status.SUCCESS -> {
                    result.data?.let {
                        _addNewCardResult.emit(AddNewCardUiState.Success(it))
                        addedConsents.add(
                            ConsentExcerpt(
                                consentId = it.consentId,
                                expirationMonth = params.creditCardInfo.expMonth,
                                expirationYear = params.creditCardInfo.expYear,
                                last4digits = params.last4digits
                            )
                        )
                    } ?: _addNewCardResult.emit(
                        AddNewCardUiState.Error(
                            EasyPayWidgetException(
                                result.error
                            )
                        )
                    )
                }

                NetworkResource.Status.DECLINED,
                NetworkResource.Status.ERROR,
                -> {
                    _addNewCardResult.emit(AddNewCardUiState.Error(EasyPayWidgetException(result.error)))
                }
            }
        }
    }

    private fun payWithSavedCard(params: ProcessPaymentAnnualParams) {
        viewModelScope.launch(workContext) {
            _payWithSavedCardResult.emit(PayWithSavedCardUiState.Loading)
            val result = processPaymentAnnual.processPaymentAnnual(params)

            when (result.status) {
                NetworkResource.Status.SUCCESS -> {
                    result.data?.mapToPaymentSheetCompletedData()?.let {
                        _payWithSavedCardResult.emit(PayWithSavedCardUiState.Success(it))
                    } ?: _payWithSavedCardResult.emit(
                        PayWithSavedCardUiState.Error(
                            EasyPayWidgetException(result.error)
                        )
                    )
                }

                NetworkResource.Status.ERROR -> {
                    _payWithSavedCardResult.emit(
                        PayWithSavedCardUiState.Error(
                            EasyPayWidgetException(result.error)
                        )
                    )
                }

                NetworkResource.Status.DECLINED -> {
                    result.data?.let {
                        _payWithSavedCardResult.emit(PayWithSavedCardUiState.Declined(it))
                    } ?: _payWithSavedCardResult.emit(
                        PayWithSavedCardUiState.Error(
                            EasyPayWidgetException(result.error)
                        )
                    )
                }
            }
        }
    }

    //endregion

    //region Data fetching

    private fun fetchAnnualConsents() {
        viewModelScope.launch(workContext) {
            _paymentMethods.emit(PaymentMethodsUiState.Loading)
            val result = listAnnualConsents.listAnnualConsents(listAnnualConsentsParams)
            when (result.status) {
                NetworkResource.Status.SUCCESS -> {
                    result.data?.let {
                        _paymentMethods.emit(PaymentMethodsUiState.Success(it.consents))
                    } ?: _paymentMethods.emit(
                        PaymentMethodsUiState.Error(
                            EasyPayWidgetException(
                                result.error
                            )
                        )
                    )
                }

                NetworkResource.Status.ERROR -> {
                    _paymentMethods.emit(PaymentMethodsUiState.Error(EasyPayWidgetException(result.error)))
                }

                NetworkResource.Status.DECLINED -> {}
            }
        }
    }

    //endregion

}