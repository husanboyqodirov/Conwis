package softromeda.myconwis

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_preferences.*
import softromeda.myconwis.R

class PreferencesActivity: AppCompatActivity() {

    val languagesToUse = arrayOf("English (US)", "Korean")
    var checkedLangIndex = 0
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        val sharedPreferences = this.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        var checkedLang = sharedPreferences.getString("PreferredLang", "")
        val editor = sharedPreferences.edit()

        if(checkedLang == "Korean") {
            checkedKorean.isChecked = true
        } else if (checkedLang == "English (US)") {
            checkedEnglishUS.isChecked = true
        }

        checkedEnglishUS.setOnClickListener{
            checkedEnglishUS.toggle()
            checkedKorean.toggle()
        }
        checkedKorean.setOnClickListener {
            checkedKorean.toggle()
            checkedEnglishUS.toggle()
        }

        btnSelectTTSLang.setOnClickListener{
            if(checkedEnglishUS.isChecked) {
                editor.putString("PreferredLang", languagesToUse[0])
            } else {
                editor.putString("PreferredLang", languagesToUse[1])

            }
            editor.apply()
            Toast.makeText(this@PreferencesActivity,
                "${sharedPreferences.getString("PreferredLang", "")}" +
                        " is selected as default language!", Toast.LENGTH_LONG).show()

        }
    }
}