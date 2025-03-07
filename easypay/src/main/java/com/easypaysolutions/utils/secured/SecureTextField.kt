package com.easypaysolutions.utils.secured

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.R
import androidx.appcompat.widget.AppCompatEditText
import com.easypaysolutions.networking.rsa.RsaHelper
import org.koin.java.KoinJavaComponent

class SecureTextField @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.editTextStyle,
) : AppCompatEditText(context, attrs, defStyleAttr), SecureWidget<String> {

    private val rsaHelper: RsaHelper by KoinJavaComponent.inject(RsaHelper::class.java)
    private var realData = ""

    override var secureData: SecureData<String> = SecureData("")

    companion object {
        private const val MAX_SECURE_LENGTH = 16
        private const val SECURE_INPUT_TYPE =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    }

    init {
        isSingleLine = true
        transformationMethod = AsteriskPasswordTransformationMethod()
        inputType = SECURE_INPUT_TYPE
        filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_SECURE_LENGTH))
    }

    //region Public

    var realTextLengthChanged: (Int) -> Unit = {}

    fun getRealTextLength(): Int = realData.length

    //endregion

    //region Overridden methods

    override fun getText(): Editable? {
        return try {
            Editable.Factory.getInstance().newEditable(secureData.data)
        } catch (e: NullPointerException) {
            null
        }
    }

    override fun getEditableText(): Editable {
        return try {
            Editable.Factory.getInstance().newEditable(secureData.data)
        } catch (e: NullPointerException) {
            Editable.Factory.getInstance().newEditable("")
        }
    }

    override fun setInputType(type: Int) {
        super.setInputType(SECURE_INPUT_TYPE)
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int,
    ) {
        if (text.isNullOrEmpty() || text.length > MAX_SECURE_LENGTH) return
        val encryptedData = rsaHelper.encrypt(text.toString())
        realData = text.toString()
        secureData = SecureData(encryptedData)
        realTextLengthChanged(realData.length)
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        // does nothing
    }

    override fun removeTextChangedListener(watcher: TextWatcher?) {
        // does nothing
    }

    //endregion

    //region AsteriskPasswordTransformationMethod

    internal class AsteriskPasswordTransformationMethod : PasswordTransformationMethod() {

        override fun getTransformation(source: CharSequence, view: View): CharSequence {
            return PasswordCharSequence(source)
        }

        inner class PasswordCharSequence(private val source: CharSequence) : CharSequence {

            override val length: Int
                get() = source.length

            override fun get(index: Int): Char = '•'

            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                return source.subSequence(startIndex, endIndex)
            }
        }
    }

    //endregion

}