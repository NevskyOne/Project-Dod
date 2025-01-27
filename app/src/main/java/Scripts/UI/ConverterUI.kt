package Scripts.UI

import Scripts.Functions.Prefix
import Scripts.Functions.Unit
import Scripts.Functions.UnitConverter
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.res.Resources
import android.text.Layout
import android.util.DisplayMetrics
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat.getString
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import com.example.firstproject.MainActivity
import com.example.firstproject.R
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.roundToInt


class ConverterUI(private val mainAct: MainActivity) {
    fun start() {
        val converter = UnitConverter()

        val fromUnits = mainAct.findViewById<MaterialSwitch>(R.id.fromUnits)
        val fromPrefix = mainAct.findViewById<AutoCompleteTextView>(R.id.fromPrefix)
        val toUnits = mainAct.findViewById<MaterialSwitch>(R.id.toUnits)
        val toPrefix = mainAct.findViewById<AutoCompleteTextView>(R.id.toPrefix)
        val convertInput = mainAct.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.convertInput)
        val convertOutput = mainAct.findViewById<TextView>(R.id.convertOutput)
        val copyButton = mainAct.findViewById<Button>(R.id.copyOutput)

        val fromLayout = mainAct.findViewById<LinearLayout>(R.id.fromLayout)
        val toLayout = mainAct.findViewById<LinearLayout>(R.id.toLayout)

        val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics
        val targetWidth = (displayMetrics.widthPixels * 0.4).roundToInt()
        val targetHeight = (displayMetrics.widthPixels * 0.45).roundToInt()
        fromLayout.updateLayoutParams{
            width = targetWidth
            height = targetHeight
        }
        toLayout.updateLayoutParams {
            width = targetWidth
            height = targetHeight
        }

        fromPrefix.setText("")
        toPrefix.setText("")
        val adapter = ArrayAdapter.createFromResource(mainAct, R.array.BinPrefixes, R.layout.list_item)
        fromPrefix.setAdapter(adapter)
        toPrefix.setAdapter(adapter)

        fromPrefix.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                converter.fromPrefix = Prefix.entries[position]

                val txt = convertInput.text.toString()
                convertOutput.text = if(txt != "") converter.convert(txt.toFloat()).toString() else ""
            }
        }
        fromUnits.setOnCheckedChangeListener { _, value ->
            if(value) {
                converter.fromUnit = Unit.entries[1]
                fromUnits.text = getString(mainAct,R.string.bytes)
            }
            else {
                converter.fromUnit = Unit.entries[0]
                fromUnits.text = getString(mainAct,R.string.bits)
            }

            val txt = convertInput.text.toString()
            convertOutput.text = if(txt != "") converter.convert(txt.toFloat()).toString() else ""
        }

        toPrefix.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                converter.toPrefix = Prefix.entries[position]

                val txt = convertInput.text.toString()
                convertOutput.text = if(txt != "") converter.convert(txt.toFloat()).toString() else ""
            }
        }

        toUnits.setOnCheckedChangeListener { _, value ->
            if(value) {
                converter.toUnit = Unit.entries[1]
                toUnits.text = getString(mainAct,R.string.bytes)
            }
            else {
                converter.toUnit = Unit.entries[0]
                toUnits.text = getString(mainAct,R.string.bits)
            }

            val txt = convertInput.text.toString()
            convertOutput.text = if(txt != "") converter.convert(txt.toFloat()).toString() else ""
        }

        convertInput.addTextChangedListener {
            val txt = convertInput.text.toString()
            convertOutput.text = if(txt != "") converter.convert(txt.toFloat()).toString() else ""
        }

        copyButton.setOnClickListener {
            val clip = ClipData.newPlainText("", convertOutput.text)
            mainAct.clipboard.setPrimaryClip(clip)
        }

    }

}