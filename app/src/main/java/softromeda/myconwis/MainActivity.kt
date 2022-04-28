package softromeda.myconwis

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_stt.*
import kotlinx.android.synthetic.main.fragment_tts.*
import kotlinx.android.synthetic.main.fragment_tts.layoutTTS
import softromeda.hadisisharif.FireXabar
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    var doubleBackToExitOnce:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.menuTTS -> {
                    val frag_tts = Fragment_tts()
                    loadFragment(1, frag_tts)
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                R.id.menuSTT -> {
                    val frag_stt = Fragment_stt()
                    loadFragment(2, frag_stt)
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                R.id.menu_pronunciation -> {
                    val intent = Intent(this, PronunciationActivity::class.java)
                    startActivity(intent)
                }
                R.id.menu_update -> {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.org"))
                    startActivity(browserIntent)
                }
                R.id.menu_settings -> {
                    val intent = Intent(this, PreferencesActivity::class.java)
                    startActivity(intent)
                }
                R.id.menu_feedback -> {
                    send_feedback()
                }
                R.id.menu_about -> {
                    var dialogView: View = View.inflate(this, R.layout.about_layout, null)
                    var dlg = AlertDialog.Builder(this)
                    dlg.setView(dialogView)
                    dlg.setPositiveButton("OK", null)
                    dlg.show()
                    true
                }
            }
            true
        }

        btnTTS.setOnClickListener {
            val frag_tts = Fragment_tts()
            loadFragment(1, frag_tts)
        }
        btnSTT.setOnClickListener {
            val frag_stt = Fragment_stt()
            loadFragment(2, frag_stt)
        }
    }


    override fun onBackPressed() {
        if(doubleBackToExitOnce){
            super.onBackPressed()
            return
        }
        this.doubleBackToExitOnce = true
        Toast.makeText(this, "Please press again to exit.", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({
            kotlin.run { doubleBackToExitOnce = false }
        }, 2000)

    }

    fun send_feedback() {
        val dialogView: View = View.inflate(this, R.layout.xabar_layout, null)
        val dlg = AlertDialog.Builder(this)
        dlg.setTitle("Send your Feedback")
        dlg.setIcon(R.drawable.email)
        dlg.setView(dialogView)
        dlg.setPositiveButton("Send") { dialog, which ->
            run {
                val dlgName: EditText = dialogView.findViewById(R.id.editIsm)
                val dlgEmail: EditText = dialogView.findViewById(R.id.editEmail)
                val dlgXabar: EditText = dialogView.findViewById(R.id.txtXabar)
                val dlgRating: RatingBar = dialogView.findViewById(R.id.txtRating)

                if (dlgName.text.isNullOrEmpty()) {
                    val toast = Toast.makeText(this, "Enter your name!", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                } else if (dlgEmail.text.isNullOrEmpty()) {
                    val toast = Toast.makeText(this, "Enter your Email!", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                } else if (dlgXabar.text.isNullOrEmpty()) {
                    val toast = Toast.makeText(this, "Type your message...!", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                } else {
                    val dlName = dlgName.text.toString()
                    val dlEmail = dlgEmail.text.toString()
                    val dlXabar = dlgXabar.text.toString()
                    val dlRating = dlgRating.rating.toString()

                    val ref = FirebaseDatabase.getInstance().getReference("messages")
                    val xabarId = ref.push().key
                    val xabar = xabarId?.let { FireXabar(it, dlName, dlEmail, dlXabar, dlRating) }
                    if (xabarId != null) {
                        ref.child(xabarId).setValue(xabar).addOnCompleteListener {
                            val toast = Toast.makeText(this@MainActivity, "Your message was sent!", Toast.LENGTH_LONG)
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
                        }
                    }
                }
            }
        }
        dlg.setNegativeButton("Cancel", null)
        dlg.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return when (item.itemId) {
            R.id.m_chiqish -> {
                exitProcess(-1)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    private fun loadFragment(fid: Int, fragment: Fragment) {

        if(fid == 1) {
            layoutSTT.setVisibility(View.GONE);
        } else if (fid == 2) {
            layoutTTS.setVisibility(View.GONE);
        }

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentView, fragment)
        fragmentTransaction.commit()
    }
}