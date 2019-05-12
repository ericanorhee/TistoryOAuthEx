package net.ienlab.tistoryoauth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileNotFoundException
import java.lang.Exception
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    val TISTORY_CLIENT_ID = "c2039b74cd4c05bea578955526f35ed4"
    val TISTORY_CLIENT_SECRET = "c2039b74cd4c05bea578955526f35ed42c7c506f4b17caea3f4357a2a31102e418175811"
    val TISTORY_REDIRECT_URL = "https://www.ienlab.net"

    var isLogin = false

    val LOGIN_INTENT = "loginIntent"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        var tistoryPref = applicationContext.getSharedPreferences("TistoryOAuthLoginPreferenceData", Context.MODE_PRIVATE)
        var tistoryURL = URL(
            "https://www.tistory.com/oauth/authorize?" +
                    "client_id=$TISTORY_CLIENT_ID" +
                    "&redirect_uri=$TISTORY_REDIRECT_URL" +
                    "&response_type=token"
        )
        isLogin = sharedPreferences.getBoolean("isLogin", false)
        if (isLogin) img_LoginTistory.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.img_tistory_logout))


        btn_LoginTistory.setOnClickListener {
            Log.d("mymy", sharedPreferences.getBoolean("isLogin", false).toString())
            if (!sharedPreferences.getBoolean("isLogin", false)) {
                Thread {
                    val cookieManager = CookieManager.getInstance()
                    val cookie = cookieManager.getCookie(tistoryURL.host)
                    val connection = tistoryURL.openConnection() as HttpsURLConnection

                    connection.setRequestProperty("Cookie", cookie)
                    connection.connect()

                    try {
                        var inputStream = connection.inputStream
                        inputStream.close()
                        if (connection.url.toString().contains("popup")) {
                            startActivity(Intent(this@MainActivity, TistoryLoginWeb::class.java))
                        } else if ("access_token" in connection.url.toString()) {
                            var urls = connection.url.toString().split("=", "&")
                            tistoryPref.edit().putString("ACCESS_TOKEN", urls[1]).apply()
                            sharedPreferences.edit().putBoolean("isLogin", true).apply()
                            img_LoginTistory.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.img_tistory_logout))
                        }
                    } catch (e: java.lang.Exception) {
                        startActivity(Intent(this@MainActivity, TistoryLoginWeb::class.java))
                    }


                }.start()
            } else {
                Thread {
                    var logoutURL = URL("https://www.tistory.com/auth/logout?redirectUrl=$TISTORY_REDIRECT_URL")
                    val cookieManager = CookieManager.getInstance()
                    val cookie = cookieManager.getCookie(tistoryURL.host)
                    val connection = logoutURL.openConnection() as HttpsURLConnection

                    connection.setRequestProperty("Cookie", cookie)
                    connection.connect()
                    try {
                        var inputStream = connection.inputStream
                        inputStream.close()
                        sharedPreferences.edit().putBoolean("isLogin", false).apply()
                        img_LoginTistory.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.img_tistory_login))
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }.start()
            }
        }

        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                img_LoginTistory.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.img_tistory_logout))
            }
        }, IntentFilter(LOGIN_INTENT))


    }
}
