package com.example.firstproject

import Scripts.UI.CalcUI
import Scripts.UI.ConverterUI
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TableRow
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.os.Looper
import android.os.Handler
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.properties.Delegates
import Scripts.Functions.Gemini


class MainActivity : AppCompatActivity() {

    private lateinit var side_menu: LinearLayout
    private lateinit var current_menu: ConstraintLayout
    private lateinit var calc_menu: ConstraintLayout
    private lateinit var unit_menu: ConstraintLayout
    private lateinit var info_menu: LinearLayout

    private lateinit var menu_list: List<ConstraintLayout>
    private lateinit var menu_btn_list: List<Button>
    private lateinit var infoList: List<ScrollView>

    private lateinit var show_info: Button
    private lateinit var show_other: Button
    private var side_width by Delegates.notNull<Int>()

    private var startX: Float = 0f
    private var swipeThreshold: Int = 100

    ///////////настройки пользователя
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gestureDetector: GestureDetector
    lateinit var clipboard: ClipboardManager

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)

        val editor = sharedPreferences.edit()
        editor.putString("locale", languageCode)
        editor.apply()
        // Применение новой конфигурации
        resources.updateConfiguration(config, resources.displayMetrics)
    }
    private fun onSwipe(layout: LinearLayout, targetMargin: Int, left: Boolean = true){
        val params = layout.layoutParams as ViewGroup.MarginLayoutParams

        if(left) {
            params.leftMargin += targetMargin
            params.leftMargin = params.leftMargin.coerceIn(-side_width, side_width)
        }
        else{
            params.rightMargin -= targetMargin
            params.rightMargin = params.rightMargin.coerceIn(-side_width, side_width)
        }

        layout.layoutParams = params
    }

    private fun animateMargins(layout: LinearLayout, targetMargin: Int, left: Boolean = true) {
        val params = layout.layoutParams as ViewGroup.MarginLayoutParams
        val currentMargin = if(left)
            params.leftMargin
        else
            params.rightMargin

        val animator = ValueAnimator.ofInt(currentMargin, targetMargin)
        animator.addUpdateListener { animation ->
            val newMargin = animation.animatedValue as Int
            if(left)
                params.leftMargin = newMargin
            else
                params.rightMargin = newMargin
            layout.layoutParams = params
        }
        animator.interpolator = DecelerateInterpolator()
        animator.duration = 200// Длительность анимации
        animator.start()
        Vibrate()
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if(targetMargin == 0){
                    show_info.visibility = View.VISIBLE
                    show_other.visibility = View.VISIBLE
                }
                else {
                    show_info.visibility = View.GONE
                    show_other.visibility = View.GONE
                }
            },
            200 // value in milliseconds
        )

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun animateGradient(){
        val background = findViewById<View>(R.id.gradient).background as GradientDrawable
        val gradientAnimator = ObjectAnimator.ofFloat(background, "gradientRadius", 0f, 100000f)
        gradientAnimator.duration = 1300 // Длительность анимации
        gradientAnimator.interpolator = AccelerateDecelerateInterpolator() // Интерполяция
        gradientAnimator.start()
    }

    fun Vibrate(time: Long = 10 ){
        if(!sharedPreferences.getBoolean("vibrate", false)) return

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) { // Vibrator availability checking
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(time)
            }
        }
    }

    private fun chooseMenu(menu: ConstraintLayout, info: ScrollView){
        for (i in menu_list){
            i.visibility = View.GONE
        }
        for (i in infoList){
            i.visibility = View.GONE
        }
        current_menu = menu
        info.visibility = View.VISIBLE
        menu.visibility = View.VISIBLE
        menu.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 0f }
        animateMargins(side_menu, 0, false)

    }

    private fun colorButtons(btn: Button){
        for (i in menu_btn_list){
            i.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(com.google.android.material.R.color.material_dynamic_secondary20))
        }
        btn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(com.google.android.material.R.color.material_dynamic_primary40))
    }


    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val apiKey = "AIzaSyC4sgG4EfTT8lIhPKdsTprq51BuuP_oXzw"
        val userPrompt = "Напиши короткую историю про медведя-космонавта"


        // Обновляем конфигурацию приложения в соответствии с сохранённым языком
        sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
        val languageCode = sharedPreferences.getString("locale", "en")
        if (languageCode != null) setLocale(languageCode)

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //////////////Инициализция UI
        side_menu = findViewById(R.id.side_menu)
        calc_menu = findViewById(R.id.calculator)
        unit_menu = findViewById(R.id.unitConv)
        info_menu = findViewById(R.id.info_menu)
        menu_list = listOf(calc_menu, unit_menu)
        current_menu = calc_menu

        CalcUI(this).start()
        ConverterUI(this).start()
        Gemini().callGeminiAPI(userPrompt, apiKey)
            { response ->
                runOnUiThread {
                    if (response != null) {
                        findViewById<TextView>(R.id.exit_app).text = response
                    } else {
                        findViewById<TextView>(R.id.exit_app).text = "Ошибка запроса"
                    }
                }
        }

        //////////////Самые важные поля
        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val background = findViewById<View>(R.id.gradient).background as GradientDrawable
        background.gradientRadius = 0f

        // Определение объекта GestureDetector
        gestureDetector = GestureDetector(this, GestureListener())
        //////////////Видео
        val videoUri = Uri.parse("android.resource://" + packageName +"/"+R.raw.anim_logo)

        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(this, videoUri)

        // Настройка AudioAttributes без запроса аудиофокуса
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
            .setLegacyStreamType(AudioAttributes.USAGE_MEDIA)
            .build()

        mediaPlayer.setAudioAttributes(audioAttributes)

        // Подготовка видео и настройка VideoView
        mediaPlayer.setOnPreparedListener {
            // Запуск видео
            mediaPlayer.start()
        }
        mediaPlayer.setVolume(0f, 0f)

        val start_video = findViewById<VideoView>(R.id.start_anim)
        start_video.setVideoURI(videoUri)

        start_video.setOnPreparedListener {
            start_video.start()
        }

        // После завершения видео начинаем анимацию градиента
        start_video.setOnCompletionListener {
            start_video.visibility = View.GONE
            animateGradient()
        }

        mediaPlayer.prepareAsync()

        //////////////Сылочка
        val link = findViewById<TextView>(R.id.author_link).setOnClickListener{
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://t.me//neco_nevsky"))
            startActivity(browserIntent)
        }

        ////////////Инфо
        val calcInfo = findViewById<ScrollView>(R.id.calc_info)
        val converterInfo = findViewById<ScrollView>(R.id.converter_info)
        infoList = listOf(calcInfo, converterInfo)

        ////////////Кнопки перехода
        val toCalc = findViewById<Button>(R.id.to_calc)
        val toUnits = findViewById<Button>(R.id.to_units)
        menu_btn_list = listOf(toCalc, toUnits)
        toCalc.setOnClickListener {
            colorButtons(toCalc)
            chooseMenu(calc_menu, calcInfo)
        }
        toUnits.setOnClickListener {
            colorButtons(toUnits)
            chooseMenu(unit_menu, converterInfo)
        }

        val exit_app = findViewById<TextView>(R.id.exit_app).setOnClickListener { finishAffinity() }

        /////////////Перевод
        val translate = findViewById<Button>(R.id.translation).setOnClickListener {
            Vibrate()
            val currentLocale = resources.configuration.locale.language // Получаем текущий язык

            if (currentLocale == "ru") {
                setLocale("en") // Если язык русский, переключаем на английский
            } else {
                setLocale("ru") // Иначе переключаем на русский
            }
            recreate()
        }

        ///////////Установка вибрации
        val vibrate_btn = findViewById<Button>(R.id.vibrate)
        if(!sharedPreferences.getBoolean("vibrate", false))
            vibrate_btn.backgroundTintList =
            ColorStateList.valueOf(resources.getColor(com.google.android.material.R.color.material_dynamic_tertiary10))
        else
            vibrate_btn.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(com.google.android.material.R.color.material_dynamic_tertiary40))

        vibrate_btn.setOnClickListener {
            val editor = sharedPreferences.edit()
            if(!sharedPreferences.getBoolean("vibrate", false)){
                editor.putBoolean("vibrate", true)
                it.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(com.google.android.material.R.color.material_dynamic_tertiary40))
            }
            else{
                editor.putBoolean("vibrate", false)
                it.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(com.google.android.material.R.color.material_dynamic_tertiary10))
            }
            editor.apply()
            Vibrate()
        }


        /////////Тут мы устанавливааем размеры кнопок и Получаем размеры экрана
        val hex_row = findViewById<TableRow>(R.id.hex_row)
        val upper_btns = findViewById<TableRow>(R.id.upper_btns)

        val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics
        val screenWidth = displayMetrics.widthPixels - 20

        // Определяем GridLayout с кнопками
        val gridLayout = findViewById<androidx.gridlayout.widget.GridLayout>(R.id.button_layout)

        // Количество столбцов
        var columnCount = 4

        // Рассчитываем размер кнопки как ширину экрана делённую на количество столбцов
        var buttonSize = screenWidth / columnCount
        // Программно изменяем размер каждой кнопки
        for (i in 0 until gridLayout.childCount) {
            val child: View = gridLayout.getChildAt(i)
            child.updateLayoutParams<androidx.gridlayout.widget.GridLayout.LayoutParams> {
                width = buttonSize
                height = buttonSize
            }
        }

        columnCount = 6
        buttonSize = screenWidth / columnCount

        for (j in 0 until columnCount) {
            val child: View = hex_row.getChildAt(j)
            child.updateLayoutParams<TableRow.LayoutParams> {
                width = buttonSize
                height = buttonSize
            }
        }

        for (j in 0 until columnCount) {
            val child: View = upper_btns.getChildAt(j)
            child.updateLayoutParams<TableRow.LayoutParams> {
                width = buttonSize
                height = buttonSize
            }
        }

        /////////Тут мы меняем значение лайатуов и устанавливаем анимашки
        side_width = (screenWidth * 0.75f).roundToInt()
        val side_scroll = findViewById<ScrollView>(R.id.side_scroll)
        side_scroll.updateLayoutParams {height = (displayMetrics.heightPixels * 0.6f).roundToInt()}

        show_other = findViewById(R.id.show_other)
        show_other.setOnClickListener {
            current_menu.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 0f }
            animateMargins(info_menu, 0)
            animateMargins(side_menu, -side_width, false)
            show_info.visibility = View.GONE
            show_other.visibility = View.GONE
        }
        val hide_menu = findViewById<Button>(R.id.hide_menu).setOnClickListener {
            current_menu.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 0f }
            animateMargins(side_menu, 0, false)
        }
        show_info = findViewById(R.id.info)
        show_info.setOnClickListener {
            current_menu.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 1f }
            animateMargins(side_menu, 0, false)
            animateMargins(info_menu, -side_width)
            show_info.visibility = View.GONE
            show_other.visibility = View.GONE
        }
        val hide_info = findViewById<Button>(R.id.hide_info).setOnClickListener {
            current_menu.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 1f }
            animateMargins(info_menu, 0)
        }

        var initialX = 0f


        // Включаем полноэкранный режим
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Для Android 11 и выше
            window.insetsController?.let { controller ->
                controller.hide(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Для Android 10 и ниже
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }

        // Прозрачный статус-бар и навигационная панель
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        val mainLayout: View = findViewById(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Устанавливаем отступы для отображения под системными панелями
            view.updatePadding(
                top = systemBarsInsets.top,
                bottom = systemBarsInsets.bottom
            )
            insets
        }

        // Полноэкранный режим
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        if (ev != null) {
            gestureDetector.onTouchEvent(ev)
        }
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            startX = ev.x
        }
        if (ev?.action == MotionEvent.ACTION_UP) {
            swipeThreshold = 100
            if (ev.x - startX > 150f) {
                if(info_menu.marginLeft == 0 && side_menu.marginRight != -side_width) {
                    current_menu.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 0f }
                    animateMargins(side_menu, -side_width, false)
                }
                else if(side_menu.marginRight == 0 && info_menu.marginLeft != 0) {
                    current_menu.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 1f }
                    animateMargins(info_menu, 0)
                }
            } else if(ev.x - startX < -150f){
                if(side_menu.marginRight == 0 && info_menu.marginLeft != -side_width) {
                    current_menu.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 1f }
                    animateMargins(info_menu, -side_width)
                }
                else if(side_menu.marginRight != 0 && info_menu.marginLeft == 0) {
                    current_menu.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 0f }
                    animateMargins(side_menu, 0,false)
                }
            }
            else if(side_menu.marginRight in -side_width..(-side_width * 0.75f).roundToInt()){
                    current_menu.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 0f }
                    animateMargins(side_menu, -side_width, false)
                }
            else if(info_menu.marginLeft in -side_width..(-side_width * 0.75f).roundToInt()) {
                current_menu.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 1f }
                animateMargins(info_menu, -side_width)
            }
            else if(Math.abs(ev.x - startX) > 100){
                current_menu.updateLayoutParams<ConstraintLayout.LayoutParams> { horizontalBias = 0.5f }
                animateMargins(side_menu, 0,false)
                animateMargins(info_menu, 0)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        private var startX: Float = 0f
        private var varX: Float = 0f
        private var motion = ""
        // Считываем начало свайпа (нажатие)
        override fun onDown(e: MotionEvent): Boolean {
            varX = e.x
            startX = e.x
            motion = ""
            return true
        }

        override fun onScroll(
            e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float
        ): Boolean {
            val currentX = e2.x
            var deltaX = currentX - varX

            // Считываем только горизонтальное движение
            if (Math.abs(deltaX) > swipeThreshold) {
                if(motion == ""){
                    swipeThreshold = 3
                    show_info.visibility = View.GONE
                    show_other.visibility = View.GONE
                }
                if (deltaX > 0) {
                    if(motion == "")
                        deltaX -= 100
                    motion = "right"

                    if(info_menu.marginLeft == 0) {
                        current_menu.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            horizontalBias = 0f
                        }
                        onSwipe(side_menu, deltaX.roundToInt(), false)
                    }
                    else if(side_menu.marginRight == 0) {
                        current_menu.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            horizontalBias = 1f
                        }
                        onSwipe(info_menu, deltaX.roundToInt())
                    }
                } else{
                    if(motion == "")
                        deltaX += 100
                    motion = "left"
                    if(side_menu.marginRight == 0) {
                        current_menu.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            horizontalBias = 1f
                        }
                        onSwipe(info_menu, deltaX.roundToInt())
                    }
                    else if(info_menu.marginLeft == 0) {
                        current_menu.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            horizontalBias = 0f
                        }
                        onSwipe(side_menu, deltaX.roundToInt(), false)
                    }
                }
                // Обновляем начальную точку для плавного свайпа
                varX = currentX
            }
            return true
        }
    }

}

