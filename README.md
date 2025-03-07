# EasyPay Android SDK

EasyPay library offers an access to the EasyPay API for a seamless integration with Android
applications.

[General Easy Pay Developer Documentation](https://easypaysoftware.com/en/home)


Table of contents
=================

* [Installation](#installation)
    * [Requirements](#requirements)
    * [Configuration - Gradle / Maven dependency](#configuration---gradle--maven-dependency)
* [Get started](#get-started)
    * [Integration](#integration)
    * [Using widgets](#using-widgets)
        * [PaymentSheet](#paymentsheet)
        * [CustomerSheet](#customersheet)
* [Public methods in the EasyPay SDK](#public-methods-in-the-easypay-sdk)
    * [1. Charge Credit Card (CreditCardSale_Manual)](#1-charge-credit-card-creditcardsale_manual)
    * [2. List Annual Consents (ConsentAnnual_Query)](#2-list-annual-consents-consentannual_query)
    * [3. Create Annual Consent (ConsentAnnual_Create_MAN)](#3-create-annual-consent-consent)
    * [4. Cancel Annual Consent (ConsentAnnual_Cancel)](#4-cancel-annual-consent-consentannual_cancel)
    * [5. Process Payment Annual Consent (ConsentAnnual_ProcPayment)](#5-process-payment-annual-consent-consentannual_procpayment)
* [SecureTextField](#securetextfield)
* [How to properly consume the API response](#how-to-properly-consume-the-api-response)
* [Possible Exceptions](#possible-exceptions)
    * [EasyPaySdkException](#easypaysdkexception)
    * [EasyPayApiException](#easypayapiexception)
* [Semantic Versioning](#semantic-versioning)
* [Feature flags](#feature-flags)
    * [Rooted device detection](#rooted-device-detection)

## Installation

### Requirements

* Android 6.0 (API level 23) and above
* Gradle 8.2 and above
* Android Gradle Plugin 8.2.1
* Kotlin 1.9.22 and above

### Configuration - Gradle / Maven dependency

Add `easypay` to your dependencies in the `build.gradle` file.

```
dependencies {
    implementation 'com.easypaysolutions:easypay:1.1.6'
    
    // If you want to use widgets, add the following line
    implementation 'com.easypaysolutions:easypay-widgets:1.1.6'
}
```

## Get started

### Integration

1. Prerequisites - get API key, HMAC secret and optional Sentry DSN from EasyPay.

2. Configure the EasyPay class at the very beginning of the application lifecycle, e.g. in the main
   Application class (in the onCreate() method).

```
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        EasyPay.init(applicationContext, "YOUR_API_KEY", "YOUR_HMAC_SECRET", "SENTRY_DSN")
    }
}
```

3. Please keep in mind that during EasyPay initialization, the RSA certificate download process
   begins. Proceeding with any call before downloading has finished will result with an exception (
   RSA_CERTIFICATE_NOT_FETCHED). You can check the download status by accessing the following enum:

```
EasyPayConfiguration.getInstance().getRsaCertificateFetchingStatus()
```

### Using widgets

#### PaymentSheet

EasyPay's prebuilt payment UI component that allows you to collect credit card information in a
secure way and process payments.

1. Initialize a `PaymentSheet` inside `onCreate` of your checkout Fragment or Activity, passing a
   method to handle the payment result.

```
import com.easypaysolutions.payment_sheet.PaymentSheet
import com.easypaysolutions.payment_sheet.utils.PaymentSheetResult

class PaymentSheetFragment : Fragment() {
    private lateinit var paymentSheet: PaymentSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
    }
    
    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        // implemented in the next steps
    }
}
```

2. When the customer taps the payment button, call `present` method on the `PaymentSheet` instance
   with the configuration. PaymentSheet.Configuration requires the following parameters:
    - AmountsParam - total amount of the payment;
    - ConsentCreatorParam - annual consent details, that contains the following parameters:
      - limitLifeTime - the maximum amount that can be charged in total,
      - limitPerCharge - the maximum amount that can be charged per transaction,
      - merchantId - the ID of the merchant that the consent is created for,
      - startDate - the date when the consent is created,
      - either customerReferenceId or consentId - to identify the customer.
    Other parameters are optional.

```
// ...
import com.easypaysolutions.repositories.annual_consent.create.ConsentCreatorParam
import com.easypaysolutions.repositories.charge_cc.AmountsParam

class PaymentSheetFragment : Fragment() {
    // ...
    
    private fun presentPaymentSheet() {
        val totalAmount: Double = 1000.0
        val consentCreator = ConsentCreatorParam(
            limitLifeTime = 100000.0,
            limitPerCharge = 1000.0,
            merchantId = 1,
            startDate = Date(),
            customerReferenceId = "CUSTOMER_REFERENCE_ID"
        )
    
        val config = PaymentSheet.Configuration
            .Builder()
            .setAmounts(AmountsParam(totalAmount))
            .setConsentCreator(consentCreator)
            .build()
            
        paymentSheet.present(config)
    }
}
```

3. Handle the payment result in the `onPaymentSheetResult` method.

```
// ...
class PaymentSheetFragment : Fragment() {
    // ...
    
    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Failed -> {
                // Handle failure
            }
    
            is PaymentSheetResult.Completed -> {
                // Handle successful payment
            }
    
            is PaymentSheetResult.Canceled -> {
                // Handle cancellation
            }
        }
    }
}
```

#### CustomerSheet

EasyPay's prebuilt UI component that lets your customers manage their saved credit cards.

1. Initialize a `CustomerSheet` inside `onCreate` of your checkout Fragment or Activity, passing a
   method to handle the customer sheet result.

```
import com.easypaysolutions.customer_sheet.CustomerSheet
import com.easypaysolutions.customer_sheet.utils.CustomerSheetResult

class CustomerSheetFragment : Fragment() {
    private lateinit var customerSheet: CustomerSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerSheet = CustomerSheet(this, ::onCustomerSheetResult)
    }
    
    private fun onCustomerSheetResult(customerSheetResult: CustomerSheetResult) {
        // implemented in the next steps
    }
}
```

2. To present the customer sheet, call the `present` method on the `CustomerSheet` instance, passing
   the configuration. CustomerSheet.Configuration requires the following parameters:
    - ConsentCreatorParam - annual consent details, that contains the following parameters:
        - limitLifeTime - the maximum amount that can be charged in total,
        - limitPerCharge - the maximum amount that can be charged per transaction,
        - merchantId - the ID of the merchant that the consent is created for,
        - startDate - the date when the consent is created,
        - either customerReferenceId or consentId - to identify the customer.
    Other parameters are optional.

```
// ...
import com.easypaysolutions.repositories.annual_consent.create.ConsentCreatorParam

class CustomerSheetFragment : Fragment() {
    // ...
    
    private fun presentCustomerSheet() {
        val consentCreator = ConsentCreatorParam(
            limitLifeTime = 100000.0,
            limitPerCharge = 1000.0,
            merchantId = 1,
            startDate = Date(),
            customerReferenceId = "CUSTOMER_REFERENCE_ID"
        )
    
        val config = CustomerSheet.Configuration
            .Builder()
             .setConsentCreator(consentCreator)
            .build()
            
        customerSheet.present(config)
    }
}
```

3. Handle the customer sheet result in the `onCustomerSheetResult` method.

```
// ...
class CustomerSheetFragment : Fragment() {
    // ...
    
    private fun onCustomerSheetResult(customerSheetResult: CustomerSheetResult) {
        when (customerSheetResult) {
            is CustomerSheetResult.Failed -> {
                // Handle failure
            }

            is CustomerSheetResult.Selected -> {
                // Handle selected card - customerSheetResult.annualConsentId
            }
        }
    }
}
```

### Screenshots

#### Save Card 
<img src="https://easypayconfig.com/SDKImages/Android/Store.png" alt="store-phone" width=20% height=20%>
<img src="https://easypayconfig.com/SDKImages/Android/StoreDark.png" alt="store-phone-dark" width=20% height=20%>
<img src="https://easypayconfig.com/SDKImages/Android/Store_tablet.png" alt="store-tablet" width=25% height=25%>
<img src="https://easypayconfig.com/SDKImages/Android/StoreDark_tablet.png" alt="store-tablet-dark" width=25% height=25%>

#### Manage Cards

<img src="https://easypayconfig.com/SDKImages/Android/Manage.png" alt="manage-phone" width=20% height=20%>
<img src="https://easypayconfig.com/SDKImages/Android/ManageDark.png" alt="manage-phone-dark" width=20% height=20%>

#### Store and Pay

<img src="https://easypayconfig.com/SDKImages/Android/StoreAndPay.png" alt="store-pay-phone" width=20% height=20%>
<img src="https://easypayconfig.com/SDKImages/Android/StoreAndPayDark.png" alt="store-pay-phone-dark" width=20% height=20%>
<img src="https://easypayconfig.com/SDKImages/Android/StoreAndPay_tablet.png" alt="store-pay-tablet" width=25% height=25%>
<img src="https://easypayconfig.com/SDKImages/Android/StoreAndPayDark_tablet.png" alt="store-pay-tablet-dark" width=25% height=25%>

## Public methods in the EasyPay SDK

[Easy Pay API Documentation](https://easypaypi.com/APIDocsDev/)

### 1. Charge Credit Card (CreditCardSale_Manual)

This method processes a credit card when the credit card details are entered manually. Details
include the card number, expiration date, CVV, card holder name and address.

```
ChargeCreditCard().chargeCreditCard(params: ChargeCreditCardBodyParams): NetworkResource<ChargeCreditCardResult>
```

#### Request parameters

* ChargeCreditCardBodyParams
    * encryptedCardNumber: SecureData<String>
    * creditCardInfo: CreditCardInfoParam
    * accountHolder: AccountHolderDataParam
    * endCustomer: EndCustomerDataParam?
    * amounts: AmountsParam
    * purchaseItems: PurchaseItemsParam
    * merchantId: Int

#### Response result

* ChargeCreditCardResult
    * [Fields listed in the API documentation]

### 2. List Annual Consents (ConsentAnnual_Query)

A query that returns annual consent details. Depending on the query sent, a single consent or
multiple consents may be returned.

```
ListAnnualConsents().listAnnualConsents(params: ListAnnualConsentsBodyParams): NetworkResource<ListAnnualConsentsResult>
```

#### Request parameters

* ListAnnualConsentsBodyParams
    * merchantId: Int?
    * customerReferenceId: String?
    * rpguid: String?
Either customerReferenceId or rpguid must be provided to get the list of consents of a specific customer.

#### Response result

* ListAnnualConsentsResult
    * [Fields listed in the API documentation]

### 3. Create Annual Consent (ConsentAnnual_Create_MAN)

This method creates an annual consent by sending the credit card details, which includes: card
number, expiration date, CVV, and card holder contact data. It is not created by swiping the card
through a reader device.

```
CreateAnnualConsent().createAnnualConsent(params: CreateAnnualConsentBodyParams): NetworkResource<CreateAnnualConsentResult>
```

#### Request parameters

* CreateAnnualConsentBodyParams
    * encryptedCardNumber: SecureData<String>
    * creditCardInfo: CreditCardInfoParam
    * accountHolder: AccountHolderDataParam
    * endCustomer: EndCustomerDataParam?
    * consentCreator: ConsentCreatorParam

#### Response result

* CreateAnnualConsentResult
    * [Fields listed in the API documentation]

### 4. Cancel Annual Consent (ConsentAnnual_Cancel)

Cancels an annual consent. Credit card data is removed from the system after the cancellation is
complete.

```
CancelAnnualConsent().cancelAnnualConsent(params: CancelAnnualConsentBodyParams): NetworkResource<CancelAnnualConsentResult>
```

#### Request parameters

* CancelAnnualConsentBodyParams
    * consentId: Int

#### Response result

* CancelAnnualConsentResult
    * [Fields listed in the API documentation]

### 5. Process Payment Annual Consent (ConsentAnnual_ProcPayment)

This method uses the credit card stored on file to process a payment for an existing consent.

```
ProcessPaymentAnnual().processPaymentAnnual(params: ProcessPaymentAnnualBodyParams): NetworkResource<ProcessPaymentAnnualResult>
```

#### Request parameters

* ProcessPaymentAnnualBodyParams
    * consentId: Int

#### Response result

* ProcessPaymentAnnualResult
    * [Fields listed in the API documentation]

## SecureTextField

The SDK contains a component called SecureTextField which ensures a safe input of number of numbers
for credit card. It is a subclass of TextInputEditText which enables freedom of styling as needed.

SecureTextField supports only XML layout configuration:

```
<com.easypaysolutions.utils.secured.SecureTextField
    ... />
```

To get the SecureData from the SecureTextField, use the following property:

```
val secureData = secureTextField.secureData
```

Data is already encrypted and can be used in the API calls directly without any additional
encryption.

## How to properly consume the API response

All requests are suspended functions, so they should be called from a coroutine scope. The result of
the request is wrapped in a NetworkResource object, which can be handled in the following way:

```
viewModelScope.launch {
    // Example of suspended function call
    val result = ChargeCreditCard().chargeCreditCard(params)
    when (result) {
        is NetworkResource.Status.SUCCESS -> {
            // Handle success
        }
        is NetworkResource.Status.ERROR -> {
            // Handle error
        }
        is NetworkResource.Status.DECLINED -> {
            // Handle declined
        }
    }
}
```

More information about consuming the API response can be found in
the [EasyPay REST API documentation](https://easypaysoftware.com/en/rest-api#how-to-properly-consume-the-api-response).

## Possible Exceptions

### EasyPaySdkException

Exceptions that are thrown by the SDK.

| Exception name                         | Suggested solution                                                                                                                                           |
|----------------------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| EASY_PAY_CONFIGURATION_NOT_INITIALIZED | Check if ```EasyPay.init(...)``` method has been called.                                                                                                     |
| MISSED_SESSION_KEY                     | Check if correct SESSION_KEY has been provided in the ```EasyPay.init(...)``` method.                                                                        |
| MISSED_HMAC_SECRET                     | Check if correct HMAC_SECRET has been provided in the ```EasyPay.init(...)``` method.                                                                        |
| RSA_CERTIFICATE_NOT_FETCHED            | RSA certificate might not be fetched yet. Check the status by calling the ```EasyPayConfiguration.getInstance().getRsaCertificateFetchingStatus()``` method. |
| RSA_CERTIFICATE_FETCH_FAILED           | Contact EasyPay.                                                                                                                                             |
| RSA_CERTIFICATE_PARSING_ERROR          | Contact EasyPay.                                                                                                                                             |

### EasyPayApiException

Exceptions that are thrown by the EasyPay API.

## Semantic Versioning

Semantic versioning follows a three-part version number: MAJOR.MINOR.PATCH.

Increment the:

- MAJOR version when you make incompatible API changes,
- MINOR version when you add functionality in a backwards-compatible manner, and
- PATCH version when you make backwards-compatible bug fixes.

## Feature flags

### Rooted device detection

To enable rooted device detection feature, call the following method before calling the
EasyPay.init(...) method:

```
EasyPayFeatureFlagManager.setRootedDeviceDetectionEnabled(true)
```