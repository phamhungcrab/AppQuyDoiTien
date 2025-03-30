package com.example.equation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var fromFlagImageView: ImageView
    private lateinit var fromCurrencyTextView: TextView
    private lateinit var toFlagImageView: ImageView
    private lateinit var toCurrencyTextView: TextView
    private lateinit var fromAmountEditText: EditText
    private lateinit var toAmountEditText: EditText
    private lateinit var exchangeRateTextView: TextView

    // Dữ liệu tiền tệ
    private val currencies = listOf(
        Currency("USD", "United States - Dollar", 1.0, R.drawable.ic_flag_us),
        Currency("EUR", "Europe - Euro", 0.924, R.drawable.ic_flag_eu),
        Currency("JPY", "Japan - Yen", 151.47, R.drawable.ic_flag_jp),
        Currency("GBP", "United Kingdom - Pound", 0.792, R.drawable.ic_flag_gb),
        Currency("VND", "Vietnam - Dong", 24750.0, R.drawable.ic_flag_vn)
    )

    private var fromCurrency = Currency("USD", "United States - Dollar", 1.0, R.drawable.ic_flag_us)
    private var toCurrency = Currency("EUR", "Europe - Euro", 0.924, R.drawable.ic_flag_eu)
    private var isFromAmountChanging = false
    private var isToAmountChanging = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ Spinner
        val fromCurrencySpinner = findViewById<Spinner>(R.id.fromCurrencySpinner)
        val toCurrencySpinner = findViewById<Spinner>(R.id.toCurrencySpinner)

        // Tạo Adapter cho Spinner
        val currencyCodes = currencies.map { it.code }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencyCodes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Gán Adapter cho Spinner
        fromCurrencySpinner.adapter = adapter
        toCurrencySpinner.adapter = adapter

        // Xử lý khi chọn tiền tệ NGUỒN
        fromCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                fromCurrency = currencies[position]
                updateCurrencyDisplay()
                updateExchangeRate()
                convertFromCurrency() // Tự động tính toán lại
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Xử lý khi chọn tiền tệ ĐÍCH
        toCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                toCurrency = currencies[position]
                updateCurrencyDisplay()
                updateExchangeRate()
                convertFromCurrency() // Tự động tính toán lại
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Ánh xạ view
        fromFlagImageView = findViewById(R.id.fromFlagImageView)
        fromCurrencyTextView = findViewById(R.id.fromCurrencyTextView)
        toFlagImageView = findViewById(R.id.toFlagImageView)
        toCurrencyTextView = findViewById(R.id.toCurrencyTextView)
        fromAmountEditText = findViewById(R.id.fromAmountEditText)
        toAmountEditText = findViewById(R.id.toAmountEditText)
        exchangeRateTextView = findViewById(R.id.exchangeRateTextView)

        // Thiết lập dữ liệu ban đầu
        updateCurrencyDisplay()
        updateExchangeRate()

        // Lắng nghe thay đổi từ ô nhập From
        fromAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!isToAmountChanging && !isFromAmountChanging) {
                    isFromAmountChanging = true
                    convertFromCurrency()
                    isFromAmountChanging = false
                }
            }
        })

        // Lắng nghe thay đổi từ ô nhập To
        toAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!isFromAmountChanging && !isToAmountChanging) {
                    isToAmountChanging = true
                    convertToCurrency()
                    isToAmountChanging = false
                }
            }
        })
    }

    private fun updateCurrencyDisplay() {
        fromFlagImageView.setImageResource(fromCurrency.flagResId)
        fromCurrencyTextView.text = fromCurrency.fullName
        toFlagImageView.setImageResource(toCurrency.flagResId)
        toCurrencyTextView.text = toCurrency.fullName
    }

    private fun updateExchangeRate() {
        val rate = toCurrency.rate / fromCurrency.rate
        exchangeRateTextView.text = String.format("1 %s = %.3f %s",
            fromCurrency.code, rate, toCurrency.code)
    }

    private fun convertFromCurrency() {
        val amountText = fromAmountEditText.text.toString()
        if (amountText.isEmpty()) {
            toAmountEditText.setText("")
            return
        }

        try {
            val amount = amountText.toDouble()
            val result = amount * (toCurrency.rate / fromCurrency.rate)
            toAmountEditText.setText(String.format("%.2f", result))
        } catch (e: NumberFormatException) {
            toAmountEditText.setText("")
        }
    }

    private fun convertToCurrency() {
        val amountText = toAmountEditText.text.toString()
        if (amountText.isEmpty()) {
            fromAmountEditText.setText("")
            return
        }

        try {
            val amount = amountText.toDouble()
            val result = amount * (fromCurrency.rate / toCurrency.rate)
            fromAmountEditText.setText(String.format("%.2f", result))
        } catch (e: NumberFormatException) {
            fromAmountEditText.setText("")
        }
    }

    data class Currency(
        val code: String,
        val fullName: String,
        val rate: Double, // Tỷ giá so với USD
        val flagResId: Int
    )
}