package tech.bharatx.samplesdkintegration

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import tech.bharatx.alternatedata.AlternateDataManager
import tech.bharatx.common.BharatXCommonUtilManager
import tech.bharatx.creditaccess.CreditAccessManager
import tech.bharatx.securityhelpers.SecurityStorageManager

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // testing credentials
    SecurityStorageManager.storePartnerId(this@MainActivity, "bharatx")
    SecurityStorageManager.storePartnerApiKey(this@MainActivity, "testKey")

    // enable BharatX credit notifications
    CreditAccessManager.register(this@MainActivity)

    // This is the same color as BharatX's default theme color
    SecurityStorageManager.storeThemeColorPreference(this@MainActivity, Color.parseColor("#4892CB"))

    // confirm whether user wants to go ahead with the transaction
    BharatXCommonUtilManager.confirmTransactionWithUser(
        this,
        object : BharatXCommonUtilManager.TransactionConfirmationListener {

          override fun onUserConfirmedTransaction() {
            Toast.makeText(this@MainActivity, "Transaction confirmed", Toast.LENGTH_LONG).show()
            startBharatXTransaction()
          }

          override fun onUserCancelledTransaction() {
            Toast.makeText(this@MainActivity, "Transaction cancelled", Toast.LENGTH_LONG).show()
          }

          override fun onUserAcceptedPrivacyPolicy() {
            // request/register for Alternate Data collection once user has accepted the privacy
            // policy
            if (AlternateDataManager.areAllAlternateDataPermissionsGranted(this@MainActivity)) {
              AlternateDataManager.register(this@MainActivity)
            } else {
              AlternateDataManager.requestAlternateDataPermissions(this@MainActivity)
            }
          }

          override fun onUserDeniedPrivacyPolicy() {
            Toast.makeText(
                this@MainActivity,
                "Please restart the app to accept our privacy policy and continue",
                Toast.LENGTH_LONG)
                .show()
          }
        })
  }

  override fun onRequestPermissionsResult(
      requestCode: Int, permissions: Array<out String>, grantResults: IntArray
  ) {
    if (requestCode == AlternateDataManager.alternateDataPermissionsRequestCode) {
      if (AlternateDataManager.areAllAlternateDataPermissionsGranted(this)) {
        // all permissions granted
        AlternateDataManager.register(this@MainActivity)
      } else {
        Toast.makeText(
            this,
            "We need to collect your data to determine your credit limit, please enable permissions in settings",
            Toast.LENGTH_LONG)
            .show()
      }
    }
  }

  private fun startBharatXTransaction() {
    val transactionId = "123" // test transaction id - obtain from server
    CreditAccessManager.registerTransactionId(
        this,
        transactionId,
        object : CreditAccessManager.RegisterTransactionListener {
          override fun onRegistered() {
            // simulate processing
            Handler(Looper.getMainLooper())
                .postDelayed(
                {
                  BharatXCommonUtilManager.closeBharatXProgressDialog()
                  Toast.makeText(
                      this@MainActivity,
                      "Thank you for your purchase. A jar of cookies for your web browser will arrive shortly :)",
                      Toast.LENGTH_LONG)
                      .show()
                },
                60000)
          }

          override fun onUserCancelledTransaction() {}

          override fun onFailure() {}
        })
  }
}
