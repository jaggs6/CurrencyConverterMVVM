package uk.co.mgntech.currency_converter.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uk.co.mgntech.currency_converter.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
    }
}
