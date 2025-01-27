package Scripts.Functions

import android.graphics.Color
import android.widget.TextView
import com.github.keelar.exprk.Expressions
import com.google.android.material.textfield.TextInputEditText
import java.math.BigInteger
import kotlin.math.pow

class CasualCalc {

    private var inputText = ""
    private var result = ""
    private var currentBase = 10

    fun addSymbol(input: TextInputEditText, simbol: String, res: TextView){
        val sb = StringBuilder(input.text)
        var index = input.selectionStart

        if (index == 0 && (simbol == "*" || simbol == "/" || simbol == "+" || simbol == "^")) return

        if (index > 0 && sb[index - 1].toString().toIntOrNull() == null && sb[index - 1] != 'a' && sb[index - 1] != 'b' && sb[index - 1] != 'c' && sb[index - 1] != 'd' && sb[index - 1] != 'e' && sb[index - 1] != 'f' && simbol == ".") {
            return //если . не после цифры, то низя
        }
        else if (index > 0 && (simbol == "*" || simbol == "/" || simbol == "^" || simbol == "-" || simbol == "+") && (sb[index - 1] == '*' || sb[index - 1] == '/' || sb[index - 1] == '^' || sb[index - 1] == '-' || sb[index - 1] == '+')) {
            sb.set(index - 1, simbol[0])  //если символы, то заменяем предыдущий на этот
            index -= 1
        }
        else if ((simbol == "*" || simbol == "/" || simbol == "^" || simbol == "-" || simbol == "+") && (sb.length > index && (sb[index] == '*' || sb[index] == '/' || sb[index] == '^' || sb[index] == '-' || sb[index] == '+'))) {
            sb.set(index, simbol[0]) //если символы, то заменяем следующий на этот
        }
        else if (index > 0 && (sb[index - 1].toString().toIntOrNull() != null || sb[index - 1] == 'a' || sb[index - 1] == 'b' || sb[index - 1] == 'c' || sb[index - 1] == 'd' || sb[index - 1] == 'e' || sb[index - 1] == 'f' || sb[index - 1] == ')') && simbol == "(") {
            sb.insert(index, '*')
            index += 1
            sb.insert(index, simbol) //если скобка то перед ней умножение
        }
        else if (index > 0 && (sb[index - 1].toString().toIntOrNull() != null || sb[index - 1] == 'a' || sb[index - 1] == 'b' || sb[index - 1] == 'c' || sb[index - 1] == 'd' || sb[index - 1] == 'e' || sb[index - 1] == 'f' || sb[index - 1] == ')') && simbol == "√"){
            sb.insert(index, "^(1/${getPreviousNumb(index-1)})")
            val firstInd = getPreviousInd(index-1)
            sb.deleteRange(firstInd,index)
            index = firstInd-1
        }
        else
            sb.insert(index, simbol) //остальное внедряем

        inputText = sb.toString()


        input.setText(inputText)
        input.setSelection(index+1)
        res.text = ""
        scaleText(input)
        if(inputText == "" || inputText.toIntOrNull() != null) return
        try {

            //переводим в инпуте
            if(currentBase != 10)
                convertInput(10, input)
            //переводим в результате
            if(currentBase != 10)
                result = convertNumber(Expressions().eval(inputText).toString(), 10, currentBase)
            else
                result = Expressions().eval(inputText).toString()
            res.setTextColor(Color.argb(255, 240, 240, 240))
            res.text = result
        }
        catch (_: Exception){
            res.text = ""
        }
    }

    fun delSymbol(input: TextInputEditText, res: TextView){
        val sb = StringBuilder(input.text)
        val index = input.selectionStart
        if(index < 1) return

        sb.deleteCharAt(index-1)
        inputText = sb.toString()

        input.setText(inputText)
        input.setSelection(index-1)
        result = ""
        res.text = result
        scaleText(input)
        if(inputText == "" || inputText.toIntOrNull() != null) return
        try {
            //переводим в инпуте
            if(currentBase != 10)
                convertInput(10, input)
            //переводим в результате
            if(currentBase != 10)
                result = convertNumber(Expressions().eval(inputText).toString(), 10, currentBase)
            else
                result = Expressions().eval(inputText).toString()
            res.setTextColor(Color.argb(255, 240, 240, 240))
            res.text = result
        }
        catch (e: Exception){
            print(e.toString())
        }
    }

    fun allClear(input: TextInputEditText, res: TextView){
        inputText = ""
        result = ""
        input.setText(inputText)
        input.textSize = 90f
        res.text = result
        res.setTextColor(Color.argb(255, 240, 240, 240))
    }

    fun result(input: TextInputEditText, res: TextView){
        inputText = input.text.toString()
        result = res.text.toString()
        if(inputText == "") return
        try {
            //переводим в инпуте
            if(currentBase != 10)
                convertInput(10, input)
            //переводим в результате
            if(currentBase != 10)
                result = convertNumber(Expressions().eval(inputText).toString(), 10, currentBase)
            else
                result = Expressions().eval(inputText).toString()
            res.setTextColor(Color.argb(255, 240, 240, 240))
            res.text = ""

            input.setText(result)
            input.setSelection(result.length)
            scaleText(input)
        }
        catch (e: ArithmeticException){
            res.text = "Arithmetic error"
            res.setTextColor(Color.argb(255, 255, 0, 0))
        }
        catch (e: Exception){
            res.text = "Format error"
            res.setTextColor(Color.argb(255, 255, 0, 0))
        }
    }

    private fun scaleText(input: TextInputEditText){
        if(inputText.length < 6)
            input.textSize = 90f
        else{
            input.textSize = (90f - (inputText.length-6)*7.5f).coerceIn(40f,90f)
        }
    }

    private fun getPreviousNumb(lastInd: Int): String{
        var prevNum = ""
        val sb = StringBuilder(prevNum)
        val inp = inputText

        var i = lastInd
        var sim: String = inp[i].toString()

        while(i >= 0 && (sim.toIntOrNull() != null || sim == "a" || sim == "b" || sim == "c" || sim == "d" || sim == "e" || sim == "f" || sim == ".")) {
            sb.append(sim)
            i-=1
            if(i >= 0)
                sim = inp[i].toString()
        }
        prevNum = sb.toString().reversed()
        return prevNum
    }

    private fun getPreviousInd(lastInd: Int): Int{
        var prevNum = lastInd
        val inp = inputText

        var i = lastInd
        var sim: String = inp[i].toString()

        while(i >= 0 && sim.toIntOrNull() != null || sim == "a" || sim == "b" || sim == "c" || sim == "d" || sim == "e" || sim == "f" || sim == ".") {
            prevNum = i
            i-=1
            if(i >= 0)
                sim = inp[i].toString()
        }
        return prevNum
    }

    fun toSystem(base: Int, input: TextInputEditText, res: TextView){
        inputText = input.text.toString()
        result = res.text.toString()
        //переводим в инпуте
        convertInput(base, input)
        input.setText(inputText)
        //переводим в результате
        convertResult(base,res)
        res.text = result
        //устанавливаем новую систему
        currentBase = base
        input.setSelection(inputText.length)
        scaleText(input)
    }

    private fun convertInput(base: Int, input: TextInputEditText){
        var inpText = input.text.toString()
        if(inpText != "") {
            val numbers = Regex("[0-9a-f.]+").findAll(inpText)
                .map(MatchResult::value)
                .toList()
            for (i in numbers.sortedWith(compareBy { it.length }).reversed()) {
                inpText = inpText.replace(i, convertNumber(i, currentBase, base))
            }
        }
        inputText = inpText
    }

    private fun convertResult(base: Int, res: TextView){
        val resText = res.text.toString()
        if(resText != "")
            result = convertNumber(resText, currentBase, base)
    }

    //алгоритмы перевода
    private fun convertNumber(input: String, fromBase: Int, toBase: Int): String {
        if (fromBase !in listOf(2, 8, 10, 16) || toBase !in listOf(2, 8, 10, 16)) {
            throw IllegalArgumentException("Основания должны быть 2, 8, 10 или 16")
        }

        val parts = input.split(".")
        val integerPart = parts[0]
        val fractionalPart = if (parts.size > 1) parts[1] else null

        // Переводим целую часть
        val integerResult = BigInteger(integerPart, fromBase).toString(toBase)

        // Переводим дробную часть
        val fractionalResult = fractionalPart?.let {
            val decimalFraction = convertFractionFromBase(it, fromBase)
            convertFractionToBase(decimalFraction, toBase, 20) // Ограничим 10 знаками
        }

        return if (fractionalResult != null) {
            "$integerResult.$fractionalResult"
        } else {
            integerResult
        }
    }

    // Собственная функция для перевода дробной части из исходной системы счисления в десятичную
    private fun convertFractionFromBase(fraction: String, fromBase: Int): Double {
        var decimal = 0.0
        fraction.forEachIndexed { index, char ->
            val digit = char.digitToInt(fromBase)
            decimal += digit / fromBase.toDouble().pow((index + 1).toDouble())
        }
        return decimal
    }

    // Собственная функция для перевода дробной части из десятичной в целевую систему счисления
    private fun convertFractionToBase(decimal: Double, toBase: Int, maxDigits: Int): String {
        var fraction = decimal
        val sb = StringBuilder()

        for (i in 0 until maxDigits) {
            fraction *= toBase
            val integerPart = fraction.toInt()
            sb.append(integerPart.toString(toBase))
            fraction -= integerPart

            if (fraction == 0.0) break  // Прерываем цикл, если дробь превратилась в ноль
        }

        return sb.toString().ifEmpty { "0" }
    }
}