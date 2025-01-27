package Scripts.UI

import Scripts.Functions.CasualCalc
import Scripts.Other.NoKeyboardEditText
import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.firstproject.MainActivity
import com.example.firstproject.R

class CalcUI(private val mainAct: MainActivity) {

    @RequiresApi(Build.VERSION_CODES.S)
    fun start() {

        val calc = CasualCalc()
        val mainInput = mainAct.findViewById<NoKeyboardEditText>(R.id.main_input)
        val result = mainAct.findViewById<TextView>(R.id.result)

        /////////////Основные кнопочки
        val btn_1 = mainAct.findViewById<Button>(R.id.button_1)
        btn_1.setOnClickListener { calc.addSymbol(mainInput, "1", result); mainAct.Vibrate() }
        val btn_2 = mainAct.findViewById<Button>(R.id.button_2)
        btn_2.setOnClickListener { calc.addSymbol(mainInput, "2", result); mainAct.Vibrate() }
        val btn_3 = mainAct.findViewById<Button>(R.id.button_3)
        btn_3.setOnClickListener { calc.addSymbol(mainInput, "3", result); mainAct.Vibrate() }
        val btn_4 = mainAct.findViewById<Button>(R.id.button_4)
        btn_4.setOnClickListener { calc.addSymbol(mainInput, "4", result); mainAct.Vibrate() }
        val btn_5 = mainAct.findViewById<Button>(R.id.button_5)
        btn_5.setOnClickListener { calc.addSymbol(mainInput, "5", result); mainAct.Vibrate() }
        val btn_6 = mainAct.findViewById<Button>(R.id.button_6)
        btn_6.setOnClickListener { calc.addSymbol(mainInput, "6", result); mainAct.Vibrate() }
        val btn_7 = mainAct.findViewById<Button>(R.id.button_7)
        btn_7.setOnClickListener { calc.addSymbol(mainInput, "7", result); mainAct.Vibrate() }
        val btn_8 = mainAct.findViewById<Button>(R.id.button_8)
        btn_8.setOnClickListener { calc.addSymbol(mainInput, "8", result); mainAct.Vibrate() }
        val btn_9 = mainAct.findViewById<Button>(R.id.button_9)
        btn_9.setOnClickListener { calc.addSymbol(mainInput, "9", result); mainAct.Vibrate() }
        val btn_0 = mainAct.findViewById<Button>(R.id.button_0)
        btn_0.setOnClickListener { calc.addSymbol(mainInput, "0", result); mainAct.Vibrate() }
        val btn_a = mainAct.findViewById<Button>(R.id.button_a).setOnClickListener {
            calc.addSymbol(mainInput, "a", result); mainAct.Vibrate()
        }
        val btn_b = mainAct.findViewById<Button>(R.id.button_b).setOnClickListener {
            calc.addSymbol(mainInput, "b", result); mainAct.Vibrate()
        }
        val btn_c = mainAct.findViewById<Button>(R.id.button_c).setOnClickListener {
            calc.addSymbol(mainInput, "c", result); mainAct.Vibrate()
        }
        val btn_d = mainAct.findViewById<Button>(R.id.button_d).setOnClickListener {
            calc.addSymbol(mainInput, "d", result); mainAct.Vibrate()
        }
        val btn_e = mainAct.findViewById<Button>(R.id.button_e).setOnClickListener {
            calc.addSymbol(mainInput, "e", result); mainAct.Vibrate()
        }
        val btn_f = mainAct.findViewById<Button>(R.id.button_f).setOnClickListener {
            calc.addSymbol(mainInput, "f", result); mainAct.Vibrate()
        }
        val btn_dot = mainAct.findViewById<Button>(R.id.dot_button).setOnClickListener {
            calc.addSymbol(mainInput, ".", result); mainAct.Vibrate()
        }
        val btn_plus = mainAct.findViewById<Button>(R.id.plus).setOnClickListener {
            calc.addSymbol(mainInput, "+", result); mainAct.Vibrate()
        }
        val btn_minus = mainAct.findViewById<Button>(R.id.minus).setOnClickListener {
            calc.addSymbol(mainInput, "-", result); mainAct.Vibrate()
        }
        val btn_multiplay = mainAct.findViewById<Button>(R.id.multiplication).setOnClickListener {
            calc.addSymbol(mainInput, "*", result); mainAct.Vibrate()
        }
        val btn_devide = mainAct.findViewById<Button>(R.id.devide).setOnClickListener {
            calc.addSymbol(mainInput, "/", result); mainAct.Vibrate()
        }
        val btn_br_op = mainAct.findViewById<Button>(R.id.bracket_open).setOnClickListener {
            calc.addSymbol(mainInput, "(", result); mainAct.Vibrate()
        }
        val btn_br_cl = mainAct.findViewById<Button>(R.id.bracket_close).setOnClickListener {
            calc.addSymbol(mainInput, ")", result); mainAct.Vibrate()
        }
        val btn_exponent = mainAct.findViewById<Button>(R.id.exponent).setOnClickListener {
            calc.addSymbol(mainInput, "^", result); mainAct.Vibrate()
        }
        val btn_root = mainAct.findViewById<Button>(R.id.root).setOnClickListener {
            calc.addSymbol(mainInput, "√", result); mainAct.Vibrate()
        }

        val btn_sum = mainAct.findViewById<Button>(R.id.button_equals).setOnClickListener {
            calc.result(mainInput, result); mainAct.Vibrate(50)
        }

        val btn_ac = mainAct.findViewById<Button>(R.id.all_clear).setOnClickListener {
            calc.allClear(mainInput, result); mainAct.Vibrate()
        }
        val btn_del = mainAct.findViewById<Button>(R.id.delete).setOnClickListener {
            calc.delSymbol(mainInput, result); mainAct.Vibrate()
        }


        //////////Создаем нажатия на кнопки перевода систем счисления
        val hex_row = mainAct.findViewById<TableRow>(R.id.hex_row)

        hex_row.visibility = View.GONE
        val num_buttons =
            listOf<Button>(btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9)

        val bin_btn = mainAct.findViewById<Button>(R.id.bin_btn)
        val oct_btn = mainAct.findViewById<Button>(R.id.oct_btn)
        val dex_btn = mainAct.findViewById<Button>(R.id.dex_btn)
        val hex_btn = mainAct.findViewById<Button>(R.id.hex_btn)
        bin_btn.setOnClickListener {
            mainAct.Vibrate()
            calc.toSystem(2, mainInput, result)

            it.backgroundTintList =
                ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary40))
            oct_btn.backgroundTintList =
                ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary10))
            dex_btn.backgroundTintList =
                ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary10))
            hex_btn.backgroundTintList =
                ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary10))

            hex_row.visibility = View.GONE
            for (i in 0..9) {
                if (i in 0..1) {
                    num_buttons[i].backgroundTintList =
                        ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary40))
                    num_buttons[i].isClickable = true
                } else {
                    num_buttons[i].backgroundTintList =
                        ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary10))
                    num_buttons[i].isClickable = false
                }
            }
        }

        oct_btn.setOnClickListener {
            mainAct.Vibrate()
            calc.toSystem(8, mainInput, result)

            it.backgroundTintList =
                ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary40))
            bin_btn.backgroundTintList =
                ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary10))
            dex_btn.backgroundTintList =
                ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary10))
            hex_btn.backgroundTintList =
                ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary10))

            hex_row.visibility = View.GONE
            for (i in 0..9) {
                if (i in 0..7) {
                    num_buttons[i].backgroundTintList =
                        ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary40))
                    num_buttons[i].isClickable = true
                } else {
                    num_buttons[i].backgroundTintList =
                        ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary10))
                    num_buttons[i].isClickable = false
                }
            }
        }

        dex_btn.setOnClickListener {
            mainAct.Vibrate()
            calc.toSystem(10, mainInput, result)

            it.backgroundTintList =
                ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary40))
            bin_btn.backgroundTintList =
                ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary10))
            oct_btn.backgroundTintList =
                ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary10))
            hex_btn.backgroundTintList =
                ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary10))

            hex_row.visibility = View.GONE
            for (i in 0..9) {
                num_buttons[i].backgroundTintList =
                    ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary40))
                num_buttons[i].isClickable = true
            }
        }

        hex_btn.setOnClickListener {
            mainAct.Vibrate()
            calc.toSystem(16, mainInput, result)

            it.backgroundTintList =
                ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary40))
            bin_btn.backgroundTintList =
                ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary10))
            dex_btn.backgroundTintList =
                ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary10))
            oct_btn.backgroundTintList =
                ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary10))

            hex_row.visibility = View.VISIBLE
            for (i in 0..9) {
                num_buttons[i].backgroundTintList =
                    ColorStateList.valueOf(mainAct.resources.getColor(com.google.android.material.R.color.material_dynamic_primary40))
                num_buttons[i].isClickable = true
            }
        }
    }
}