package app.isketch.belowaveragewifi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import com.google.android.material.textfield.TextInputEditText
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class MainActivity : AppCompatActivity() {
    var WEntConfig = WifiEnterpriseConfig()
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var WiMan = this.getSystemService(Context.WIFI_SERVICE) as WifiManager
        setupWifiConfig()
        var progBar = findViewById<ProgressBar>(R.id.prog_bar)
        var userText = findViewById<TextInputEditText>(R.id.text_username)
        var userPass = findViewById<EditText>(R.id.text_password)
        var button = findViewById<Button>(R.id.button_join)
        button.setOnClickListener {
            progBar.visibility = View.VISIBLE
            button.text = "Please wait, WiFi should join shortly..."
            WiMan.removeNetworkSuggestions(WiMan.networkSuggestions.toList())
            WEntConfig.identity = userText.text.toString()
            WEntConfig.password = userPass.text.toString()
            var wsugg = WifiNetworkSuggestion.Builder().setSsid("belowaverage").setIsHiddenSsid(true).setWpa2EnterpriseConfig(WEntConfig).build()
            var wList = ArrayList<WifiNetworkSuggestion>()
            wList.add(wsugg)
            WiMan.addNetworkSuggestions(wList)
            startActivity(Intent(android.provider.Settings.ACTION_WIFI_SETTINGS))
        }
        userText.requestFocus()
    }

    override fun onResume() {
        var progBar = findViewById<ProgressBar>(R.id.prog_bar)
        var button = findViewById<Button>(R.id.button_join)
        progBar.visibility = View.INVISIBLE
        button.text = "Join WiFi"
        super.onResume()
    }
    fun setupWifiConfig() {
        var cf = CertificateFactory.getInstance("X.509")
        var cert = cf.generateCertificate(resources.openRawResource(R.raw.rootca)) as X509Certificate
        WEntConfig.eapMethod = WifiEnterpriseConfig.Eap.PEAP
        WEntConfig.domainSuffixMatch = "wifi.belowaverage.org"
        WEntConfig.caCertificate = cert
    }
}
