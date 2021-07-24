package tech.bharatx.samplesdkintegration

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import tech.bharatx.common.BharatXTransactionManager
import tech.bharatx.common.BharatXUserManager

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    BharatXUserManager(this)
        .phoneNumber("+911234567890")
        .id("user-${System.currentTimeMillis()}")
        .name("Andrey Breslav")
        .gender("Male")
        .age(20)
        .dob("2016-02-05", "yyyy-MM-dd")
        .address("20, Tech Street, Bengaluru")
        .prop("customKey1", "customValue1")
        .register()

    bharatx_pay_button.setOnClickListener {
      CookieStoreApis()
          .initiateTransaction(
          object : CookieStoreApis.ApiListener<String> {
            override fun onComplete(res: String) {
              // confirm whether user wants to go ahead with the transaction
              BharatXTransactionManager.confirmTransactionWithUser(
                  this@MainActivity,
                  10000, // ask user for confirmation for 100 rupees
                  res, // res is our transaction id
                  object : BharatXTransactionManager.TransactionStatusListener() {

                    override fun onSuccess() {
                      Toast.makeText(
                          this@MainActivity,
                          "Thank you for your purchase. A jar of cookies for your web browser will arrive shortly :)",
                          Toast.LENGTH_LONG)
                          .show()
                    }

                    override fun onFailure(
                        transactionFailureReason: BharatXTransactionManager.TransactionFailureReason
                    ) {
                      Toast.makeText(
                          this@MainActivity,
                          "Transaction failed due to ${transactionFailureReason.name}",
                          Toast.LENGTH_LONG)
                          .show()
                    }
                  })
            }
          })
    }
  }

  // Merchant API call helper
  class CookieStoreApis {
    fun initiateTransaction(apiListener: ApiListener<String>) {
      // initiate transaction in your server and get the transaction id
      apiListener.onComplete("txn-${System.currentTimeMillis()}")
    }

    interface ApiListener<T> {
      fun onComplete(res: T)
    }
  }
}
