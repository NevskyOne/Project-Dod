package Scripts.Functions

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

class UnitConverter {
    //units that we can change
    var fromUnit = Unit.Bit
    var toUnit = Unit.Bit

    //prefixes that we can change
    var fromPrefix = Prefix.None
    var toPrefix = Prefix.None

    //main converter function, returns converted number
    fun convert(number: Float): Any {
        //firstly convert units
        var result = when(fromUnit) {
            Unit.Bit -> if(toUnit == Unit.Byte) number / 8 else number
            Unit.Byte -> if(toUnit == Unit.Bit) number * 8 else number
        }

        //secondly find difference between prefixes
        val diff = toPrefix.ordinal - fromPrefix.ordinal

        //thirdly multiply or divide difference
        result = when {
            diff < 0 -> result * (1024f.pow(abs(diff)))
            diff > 0 -> result / (1024f.pow(abs(diff)))
            else -> result
        }

        return if(result.toString().toIntOrNull() == null)
            result
        else
            result.roundToInt()
    }
}

enum class Unit{
    Bit, Byte
}

enum class Prefix{
    None, Kilo, Mega, Giga, Tera, Peta, Exa
}