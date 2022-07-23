package com.hussein.note.base

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.telephony.TelephonyManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity
import okio.buffer
import okio.source
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


open class BaseActivity : LocaleAwareCompatActivity() {
    protected var dialog: MaterialDialog? = null
//    var adView: AdView? = null
//    lateinit var mInterstitialAd: InterstitialAd
    val fontKey = "fontsize"
//    private var mRewardedAd: RewardedAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        applyTheme()
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);


        activity = this
        tDb= com.hussein.note.base.TinyDB(this)
        lang = userLang

    }

    open fun isEmailValid(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun convertTime(time: Long): String? {

        val date = Date(time)
        // hh small if you want it 12 hours -- HH if 24 hours
        val format = SimpleDateFormat("dd MMM yyyy hh:mm a")

        return format.format(date)
    }

//    private fun applyTheme() {
//        val tinyDB = TinyDB(this)
//        when ((tinyDB.getString("theme"))) {
//            "light" -> {
//                setTheme(R.style.AppTheme_Base);
//                window.statusBarColor = ContextCompat.getColor(this, R.color.lightblue)
//                window.navigationBarColor = ContextCompat.getColor(this, R.color.lightblue)
//            }
//            "dark" -> {
//                setTheme(R.style.AppTheme_second);
//                window.statusBarColor = ContextCompat.getColor(this, R.color.primary_text)
//                window.navigationBarColor = ContextCompat.getColor(this, R.color.primary_text)
//            }
//            else -> {
//                setTheme(R.style.AppTheme_Base);
//                window.statusBarColor = ContextCompat.getColor(this, R.color.lightblue)
//                window.navigationBarColor = ContextCompat.getColor(this, R.color.lightblue)
//            }
//        }
//    }

    open fun getUserCountry(context: Context): String? {
        try {
            val tm = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            val simCountry = tm.simCountryIso
            if (simCountry != null && simCountry.length == 2) { // SIM country code is available
                return simCountry.lowercase(Locale.US)
            } else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                val networkCountry = tm.networkCountryIso
                if (networkCountry != null && networkCountry.length == 2) { // network country code is available
                    return networkCountry.lowercase(Locale.US)
                }
            }
        } catch (e: java.lang.Exception) {
        }
        return null
    }

//    fun shareText(text: String) {
//        val shareIntent = Intent()
//        shareIntent.action = Intent.ACTION_SEND
//        shareIntent.putExtra(
//            Intent.EXTRA_TEXT,
//            text + "\n" +getString(R.string.sharedFromAyah) + "\n" + "https://play.google.com/store/apps/details?id=com.hussein.aya&hl=ar&gl=US"
//        )
//        shareIntent.type = "text/plain"
//        startActivity(shareIntent)
//    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    open fun startNotification(
//        name: String,
//        pic: Int,
//        reciter: String,
//        cancel: String,
//        id: Int,
//        pause: Int
//    ) {
//        val intent = Intent(this, AudioPlayService::class.java)
//        intent.putExtra("name", name)
//        intent.putExtra("pic", pic)
//        intent.putExtra("reciter", reciter)
//        intent.putExtra("cancel", cancel)
//        intent.putExtra("id", id)
//        intent.putExtra("pause", pause)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent)
//        } else {
//            startService(intent)
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    open fun startNotification(name: String, pic: Int, reciter: String, cancel: String, id: Int) {
//        val intent = Intent(this, AudioPlayService::class.java)
//        intent.putExtra("name", name)
//        intent.putExtra("pic", pic)
//        intent.putExtra("reciter", reciter)
//        intent.putExtra("cancel", cancel)
//        intent.putExtra("id", id)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent)
//        } else {
//            startService(intent)
//        }
//    }

    fun getJsonFromPath(filePath: String): String? {
        val yourFile = File(filePath)
        val stream = FileInputStream(yourFile)
        var jString: String? = null
        jString = try {
            val fc = stream.channel
            val bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())
            /* Instead of using default, pass in a decoder. */Charset.defaultCharset().decode(bb)
                .toString()
        } finally {
            stream.close()
        }
        return jString
    }

    open fun getRootDirPathGlobal(context: Context): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val file = ContextCompat.getExternalFilesDirs(
                context.applicationContext,
                null
            )[0]
            file.absolutePath
        } else {
            context.applicationContext.filesDir.absolutePath
        }
    }

    fun getFontSize(): Int {
        return getInt(fontKey, activity)
    }


//    fun showRewardAd(function: () -> (Unit)) {
//        val adRequest = AdRequest.Builder()
//            .build()
//        mRewardedAd = RewardedAd(this, "ca-app-pub-4482062120791422/7998313556")
//        mRewardedAd!!.loadAd(adRequest, object : RewardedAdLoadCallback() {
//            override fun onRewardedAdLoaded() {
//                super.onRewardedAdLoaded()
//                mRewardedAd!!.show(this@BaseActivity, object : RewardedAdCallback() {
//                    override fun onUserEarnedReward(p0: RewardItem) {
//                        function()
//                    }
//                })
//            }
//
//            override fun onRewardedAdFailedToLoad(p0: LoadAdError?) {
//                super.onRewardedAdFailedToLoad(p0)
//
//
//            }
//        })
//    }
//
//    private fun countDown() {
//        makeToast(this, "سيظهر اعلان قريبا ")
//        val timer = object : CountDownTimer(300, 300) {
//            override fun onTick(millisUntilFinished: Long) {
//            }
//
//            override fun onFinish() {
//                mInterstitialAd.show()
//            }
//        }
//        timer.start()
//    }
//
//    fun showInterstitalAd(function: () -> (Unit)) {
//        if (getInt(AppBilling.pillKey, activity) != 1) {
//            if (mInterstitialAd?.isLoaded!!) {
//                mInterstitialAd!!.show()
//            } else {
//                function()
//
//            }
//            mInterstitialAd!!.adListener = object : AdListener() {
//                override fun onAdLoaded() {
//                    //Here set a flag to know that your
////                    Log.d("addddd", "onAdLoaded: ")
//                }
//
//                override fun onAdClosed() {
//                    // Proceed to the next activity.
//                    function()
//                    mInterstitialAd!!.loadAd(AdRequest.Builder().build())
////                    Log.d("addddd", "onAdClosed: ")
//                }
//
//                override fun onAdFailedToLoad(p0: LoadAdError?) {
//                    super.onAdFailedToLoad(p0)
//                    function()
////                    Log.d("addddd", "onAdFailedToLoad: ")
//
//                }
//
//
//            }
//        }
//    }
//
//    open fun showInterstitalAd(intent: Intent?) {
//        if (getInt(AppBilling.pillKey, activity) != 1) {
//            if (mInterstitialAd!!.isLoaded) {
//                mInterstitialAd!!.show()
//            } else {
//                startActivity(intent)
//            }
//            mInterstitialAd!!.adListener = object : AdListener() {
//                override fun onAdLoaded() {
//                    //Here set a flag to know that your
//                }
//
//                override fun onAdClosed() {
//                    // Proceed to the next activity.
//                    mInterstitialAd!!.loadAd(AdRequest.Builder().build())
//                    startActivity(intent)
//                }
//            }
//        } else {
//            startActivity(intent)
//        }
//    }
//
//    fun showInterstitalAd() {
//        if (getInt(AppBilling.pillKey, activity) != 1) {
//            if (mInterstitialAd!!.isLoaded) {
//                mInterstitialAd!!.show()
//            }
//            mInterstitialAd!!.adListener = object : AdListener() {
//                override fun onAdLoaded() {
//                    //Here set a flag to know that your
//                }
//
//                override fun onAdClosed() {
//                    // Proceed to the next activity.
//                    mInterstitialAd!!.loadAd(AdRequest.Builder().build())
//                }
//            }
//        }
//    }

    //    fun showInterstitalAd(intent: Intent?) {
//        if (getInt(AppBilling.pillKey, activity) != 1) {
//            if (mInterstitialAd!!.isLoaded) {
//                mInterstitialAd!!.show()
//            } else {
//                startActivity(intent)
//            }
//            mInterstitialAd!!.adListener = object : AdListener() {
//                override fun onAdLoaded() {
//                    //Here set a flag to know that your
//                }
//
//                override fun onAdClosed() {
//                    // Proceed to the next activity.
//                    mInterstitialAd!!.loadAd(AdRequest.Builder().build())
//                    startActivity(intent)
//                }
//            }
//        } else {
//            startActivity(intent)
//        }
//    }
    fun colorized(text: String, word: String, argb: Int): Spannable {
        val spannable: Spannable = SpannableString(text)
        var substringStart = 0
        var start: Int
        while (text.indexOf(word, substringStart).also { start = it } >= 0) {
            spannable.setSpan(
                ForegroundColorSpan(argb), start, start + word.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            substringStart = start + word.length
        }
        return spannable
    }

//    fun addsRequest() {
//        val adRequest = AdRequest.Builder()
//            .build()
//        mInterstitialAd = InterstitialAd(this)
//        mInterstitialAd!!.adUnitId =
//            getString(R.string.interstitial_full_screen) // الرقم التعريفي للوحدة الاعلانية - اعلان بيني
//        mInterstitialAd!!.loadAd(adRequest)
//    }
//
//    fun writeToJsonFile(clss: ArrayList<HisnModel?>?) {
//        val gson = Gson()
//        val json = gson.toJson(clss)
//        val jsonString = json.toString()
//        val directory = File(BookDetails.getRootDirPath(MyApplication.getAppContext()) + "/")
//        if (!directory.exists()) directory.mkdir()
//        val newFile = File(directory, "myText.json")
//        if (!newFile.exists()) {
//            try {
//                newFile.createNewFile()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//        try {
//            val fOut = FileOutputStream(newFile)
//            val outputWriter = OutputStreamWriter(fOut)
//            outputWriter.write(jsonString)
//            outputWriter.close()
//
//            //display file saved message
//            Toast.makeText(
//                baseContext, "File saved successfully!",
//                Toast.LENGTH_SHORT
//            ).show()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    fun changeLang(languageToLoad: String?) {
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        activity!!.resources.updateConfiguration(config, activity!!.resources.displayMetrics)
    }

    fun deviceLang(): String {
        return Locale.getDefault().language
    }

    fun stayScreenOn() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    fun makeToast(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun makeToast(context: Context?, message: String?, Period: Int) {
        Toast.makeText(context, message, Period).show()
    }

    fun makeToast(context: Context?, message: Int) {
        Toast.makeText(context, getString(message), Toast.LENGTH_SHORT).show()
    }

//    fun loadNativeAd(
//        recyclerView: RecyclerView,
//        adapter: RecyclerView.Adapter<*>?,
//        count: Int,
//        type: String?
//    ) {
//        if (getInt(AppBilling.pillKey, activity) != 1 && isNetworkAvailable()) {
//            try {
//                val admobNativeAdAdapter = AdmobNativeAdAdapter.Builder
//                    .with(
//                        getString(R.string.native_ad_unit_id),  //Create a native ad id from admob console
//                        adapter,  //The adapter you would normally set to your recyClerView
//                        type //Set it with "small","medium" or "custom"
//                    )
//                    .adItemIterval(count) //native ad repeating interval in the recyclerview
//                    .build()
//                recyclerView.adapter = admobNativeAdAdapter
//            } catch (e: Exception) {
//                Log.d("ssss", "loadNativeAd: " + e.message)
//            }
//        } else {
//            recyclerView.adapter=adapter
//        }
//    }

    //    public  void lunchFragment(int pos, int container, Fragment fragment, String key) {
    //        getSupportFragmentManager()
    //                .beginTransaction()
    //                .addToBackStack("h")
    //                .replace(container, fragment)
    //                .commit();
    //        Bundle bundle = new Bundle();
    ////                    bundle.putString("String", model.getNames());
    //        bundle.putInt(key, pos);
    //        fragment.setArguments(bundle);
    //    }
    fun lunchFragment(container: Int, fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack("h")
            .replace(container, fragment)
            .commit()
    }
    fun lunchFragment(container: Int, fragment: Fragment, value: String?, key2: String?) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack("h")
            .replace(container, fragment)
            .commit()
        val bundle = Bundle()
        //                    bundle.putString("String", model.getNames());
        bundle.putString(key2, value)
        fragment.arguments = bundle
    }

    fun lunchFragment(
        pos: Int,
        container: Int,
        fragment: Fragment,
        key: String?,
        value: String?,
        key2: String?
    ) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack("h")
            .replace(container, fragment)
            .commit()
        val bundle = Bundle()
        //                    bundle.putString("String", model.getNames());
        bundle.putInt(key, pos)
        bundle.putString(key2, value)
        fragment.arguments = bundle
    }

//    fun readFromText(ayadata: MutableList<ayamodel?>, name: String) {
//        val reader: BufferedReader
//        var num: Int
//        try {
//            val file = assets.open("$name.txt")
//            reader = BufferedReader(InputStreamReader(file))
//            var line: String? = ""
//            Log.i("StackOverflow", line!!)
//            num = 0
//            while (line != null) {
//                line = reader.readLine()
//                ayadata.add(ayamodel(line))
//                num++
//            }
//        } catch (ioe: IOException) {
//            ioe.printStackTrace()
//        }
//    }

    fun showMessage(titleResId: Int, messageResId: Int, posResText: Int): MaterialDialog? {
        MaterialDialog.Builder(this)
            .title(titleResId)
//            .titleColor(ContextCompat.getColor(activity,R.color.textColour))
            .show()
        return dialog
    }

    fun showConfirmationMessage(
        titleResId: Int, messageResId: Int, posResText: Int, negText: Int,
        onPosAction: SingleButtonCallback?,
        onnegAction: SingleButtonCallback?
    ): MaterialDialog? {
        MaterialDialog.Builder(this)
            .title(titleResId)

            .content(messageResId)
            .positiveText(posResText)
            .negativeText(negText)
            .onPositive(onPosAction!!)
            .onNegative(onnegAction!!)
            .cancelable(false)
            .show()
        return dialog
    }

    fun showConfirmationMessage(
        titleResId: Int, messageResId: Int, posResText: Int,
        onPosAction: SingleButtonCallback?
    ): MaterialDialog? {
        MaterialDialog.Builder(this)
            .title(titleResId)
            .content(messageResId)
            .positiveText(posResText)
            .onPositive(onPosAction!!)
            .show()
        return dialog
    }

//    fun showMessage(title: String?, message: String?, posText: String?): MaterialDialog? {
//        MaterialDialog.Builder(applicationContext)
//            .title(title!!)
//            .content(message!!)
//            .positiveText(posText!!)
//            .onPositive { dialog, which ->
//                if (dialog.isShowing) {
//                    try {
//                        dialog.dismiss()
//                    } catch (e: Exception) {
//                        Log.i("Exception", e.toString())
//                    }
//                }
//            }
//            .show()
//        return dialog
//    }

    fun showProgressBar(message: Int): MaterialDialog? {
        dialog = MaterialDialog.Builder(this)
            .progress(true, 0)
            .content(message)
            .cancelable(false)
            .show()
        return dialog
    }

    fun hideProgressBar() {
        if (dialog != null && dialog!!.isShowing && activity != null && !activity!!.isFinishing) {
            try {
                dialog!!.dismiss()
            } catch (e: IllegalArgumentException) {
                Log.e("hige", e.toString())
            }
        }
    }

//    fun showadds() {
//        if (getInt(AppBilling.pillKey, activity) != 1) {
//            val adContainerView: FrameLayout
//            MobileAds.initialize(this) { }
//            adContainerView = findViewById(R.id.adView_container)
//            // Step 1 - Create an AdView and set the ad unit ID on it.
//            adView = AdView(this)
//            adView!!.adUnitId = getString(R.string.banner_ad_unit_id)
//            adContainerView.addView(adView)
//            val adRequest = AdRequest.Builder()
//                .build()
//            val adSize = adSize
//            // Step 4 - Set the adaptive ad size on the ad view.
//            adView!!.adSize = adSize
//
//            // Step 5 - Start loading the ad in the background.
//            adView!!.loadAd(adRequest)
//            //        AdView adView = new AdView(this);
////        adView.setAdSize(AdSize.SMART_BANNER);
////        adView.setAdUnitId(String.valueOf(R.string.banner_ad_unit_id));
////        AdRequest adRequest = new AdRequest.Builder()
////                .build();
////        AdView mAdView =  findViewById(R.id.adView);
////        mAdView.loadAd(adRequest);
//        } else {
//        }
//    }
//
//    // Step 2 - Determine the screen width (less decorations) to use for the ad width.
//    private val adSize: AdSize
//        private get() {
//            // Step 2 - Determine the screen width (less decorations) to use for the ad width.
//            val display = windowManager.defaultDisplay
//            val outMetrics = DisplayMetrics()
//            display.getMetrics(outMetrics)
//            val widthPixels = outMetrics.widthPixels.toFloat()
//            val density = outMetrics.density
//            val adWidth = (widthPixels / density).toInt()
//
//            // Step 3 - Get adaptive ad size and return for setting on the ad view.
//            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
//        }

    fun check(intent: Intent, ar: Array<String?>?, en: Array<String?>?) {
        var lang = userLang
        if (lang!!.isEmpty()) {
            lang = deviceLang()
        }
        if (lang.equals("ar", ignoreCase = true)) {
            intent.putExtra("1", ar)
        } else {
            intent.putExtra("1", en)
        }
    }

    fun check(intent: Intent, ar: Array<String?>?, en: Array<String?>?, num: Array<String?>?) {
        var lang = userLang
        if (lang!!.isEmpty()) {
            lang = deviceLang()
        }
        if (lang.equals("ar", ignoreCase = true)) {
            intent.putExtra("1", ar)
            intent.putExtra("11", num)
        } else {
            intent.putExtra("1", en)
            intent.putExtra("11", num)
        }
    }

    fun changeFont(textView: TextView, activity: BaseActivity, font: String?) {
        val tf_regular = Typeface.createFromAsset(activity.assets, font)
        textView.typeface = tf_regular
    }

    companion object {
        lateinit var activity: BaseActivity
        var lang: String? = null
        lateinit var tDb: TinyDB

        @JvmField
        var FORMAT = "%02d:%02d:%02d"
        lateinit var int_shared: SharedPreferences
        lateinit var string_shared: SharedPreferences
        lateinit var edit: SharedPreferences.Editor
        var prefName = "localization"
        var preflangName = "lang"
        var clickCount: Int = -1

        @JvmField
        var k = "la"

        @JvmStatic
        fun setInt(key: String?, value: Int, context: Context) {
            int_shared = context.getSharedPreferences(prefName, MODE_PRIVATE)
            edit = int_shared.edit()
            edit.putInt(key, value)
            edit.apply()
        }

        @JvmStatic
        fun getInt(key: String?, context: Context?): Int {
            int_shared = context!!.getSharedPreferences(prefName, MODE_PRIVATE)
            return if (int_shared != null) {
                int_shared!!.getInt(key, 0)
            } else 0
        }

//        fun turnOnFireBaseNotifications() {
//            FirebaseMessaging.getInstance().subscribeToTopic("notifications")
//            FirebaseMessaging.getInstance().subscribeToTopic("ayah")
////            FirebaseMessaging.getInstance().subscribeToTopic("test")
//
//        }
//
//        fun SearchDialog(
//            stringList: List<String?>?, toolbarTitle: String?, searchBoxHint: String?,
//            backPressed: Boolean, single: DialogListener.Single?
//        ) {
//            val filterDialog = FilterDialog(activity)
//            filterDialog.toolbarTitle = toolbarTitle
//            filterDialog.searchBoxHint = searchBoxHint
//            filterDialog.setList(stringList)
//            filterDialog.disableBackButton()
//            filterDialog.backPressedEnabled(backPressed)
//            filterDialog.setOnCloseListener { view: View? -> filterDialog.dispose() }
//            filterDialog.show(single)
//        }
//
//        @JvmStatic
//        fun turnOfFireBaseNotifications() {
//            FirebaseMessaging.getInstance().unsubscribeFromTopic("notifications")
//            FirebaseMessaging.getInstance().unsubscribeFromTopic("ayah")
////            FirebaseMessaging.getInstance().unsubscribeFromTopic("test")
//
//        }

        fun setString(key: String?, value: String?, context: Context) {
            string_shared = context.getSharedPreferences(preflangName, MODE_PRIVATE)
            edit = string_shared.edit()
            edit.putString(key, value)
            edit.apply()
        }

        @JvmStatic
        fun getString(key: String?, context: Context?): String? {
            string_shared = context!!.getSharedPreferences(preflangName, MODE_PRIVATE)
            return if (string_shared != null) {
                string_shared!!.getString(key, "")
            } else null
        }

        @JvmStatic
        val userLang: String?
            get() = getString(k, activity)

        fun colorized(text: String, word: String, argb: Int): Spannable {
            val spannable: Spannable = SpannableString(text)
            var substringStart = 0
            var start: Int
            while (text.indexOf(word, substringStart).also { start = it } >= 0) {
                spannable.setSpan(
                    ForegroundColorSpan(argb), start, start + word.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                substringStart = start + word.length
            }
            return spannable
        }
//        fun getJsonFromAssets(context: Context, fileName: String): String? {
//            val jsonString: String
//            jsonString = try {
//                val `is` = context.assets.open(fileName!!)
//                val size = `is`.available()
//                val buffer = ByteArray(size)
//                `is`.read(buffer)
//                `is`.close()
//                String(buffer, charset("UTF-8"))
//            } catch (e: IOException) {
//                e.printStackTrace()
//                return null
//            }
//            return jsonString
//        }

        fun getJsonFromAssets(context: Context, filePath: String): String? {
            try {
                val source = context.assets.open(filePath).source().buffer()
                return source.readByteString().string(Charset.forName("utf-8"))

            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        //    public static int getLang(Context context) {
        //        int_shared=context.getSharedPreferences(prefName,MODE_PRIVATE);
        //        lang=getInt(key,context);
        //
        //        return lang;
        //    }
        //    public static boolean isArabic(){
        //        return getLang(MyApplication.getAppContext()) == 1;
        //    }
        @JvmStatic
        fun parseTime(milliseconds: Long, format: String?): String {
            return String.format(
                format!!,
                TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(milliseconds)
                ),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(milliseconds)
                )
            )
        }
        fun getConnectionType(context: Context): Int {
            var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi
            val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (cm != null) {
                    val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
                    if (capabilities != null) {
                        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                            result = 2
                        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                            result = 1
                        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                            result = 3
                        }
                    }
                }
            } else {
                if (cm != null) {
                    val activeNetwork = cm.activeNetworkInfo
                    if (activeNetwork != null) {
                        // connected to the internet
                        if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                            result = 2
                        } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                            result = 1
                        } else if (activeNetwork.type == ConnectivityManager.TYPE_VPN) {
                            result = 3
                        }
                    }
                }
            }
            return result
        }
        fun isNetworkAvailable(): Boolean {
            val connectivityManager =
                activity.getSystemService(
                    CONNECTIVITY_SERVICE
                ) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
        @JvmStatic
        fun changeTextColor(textView: TextView){
            if (tDb.getInt("textColor")!=0){
                textView.setTextColor(tDb.getInt("textColor"))

            }
        }
        @JvmStatic
        fun changeTextColor(textView: TextView,textView1: TextView){
            if (tDb.getInt("textColor")!=0){
                textView.setTextColor(tDb.getInt("textColor"))
                textView1.setTextColor(tDb.getInt("textColor"))

            }
        }
        @JvmStatic
        fun changeTextColor(textView: TextView,textView1: TextView,textView2: TextView){
            if (tDb.getInt("textColor")!=0){
                textView.setTextColor(tDb.getInt("textColor"))
                textView1.setTextColor(tDb.getInt("textColor"))
                textView2.setTextColor(tDb.getInt("textColor"))

            }
        }

    }
}