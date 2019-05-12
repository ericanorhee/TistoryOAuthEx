package net.ienlab.tistoryoauth

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_tistory_login_web.*

class TistoryLoginWeb : AppCompatActivity() {

    val TISTORY_CLIENT_ID = "c2039b74cd4c05bea578955526f35ed4"
    val TISTORY_REDIRECT_URL = "https://www.ienlab.net"

    val LOGIN_INTENT = "loginIntent"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tistory_login_web)

        var sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        var tistoryPref = applicationContext.getSharedPreferences("TistoryOAuthLoginPreferenceData", Context.MODE_PRIVATE)

        var tistoryURL =
            "https://www.tistory.com/oauth/authorize?" +
                    "client_id=$TISTORY_CLIENT_ID" +
                    "&redirect_uri=$TISTORY_REDIRECT_URL" +
                    "&response_type=token"

        webview.settings.javaScriptEnabled = true
        webview.webViewClient = object: WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if ("access_token" in url!!) {
                    sharedPreferences.edit().putBoolean("isLogin", true).apply()
                    var urls = url.split("=", "&")
                    tistoryPref.edit().putString("ACCESS_TOKEN", urls[1]).apply()
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(Intent(LOGIN_INTENT))
                    finish()
                }
            }
        }
        webview.loadUrl(tistoryURL)

    }
}
