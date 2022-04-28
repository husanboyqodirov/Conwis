package softromeda.myconwis

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.pronunciation_layout.*
import java.util.*

class PronunciationActivity: AppCompatActivity(), TextToSpeech.OnInitListener {
    private val REQUEST_CODE_SPEECH_INPUT = 100
    lateinit var textToSpeech: TextToSpeech
    var pronounced = false
    var cntAll: Int = 0
    var cntCorrect: Int = 0
    var cntAttemptsAll: Int = 0
    var cntAttemptsCorrect: Int = 0
    var cntAttemptsWrong: Int = 0

    var perCorrect: Float = 0F
    var perAttemptsCorrect: Float = 0F
    var perAttemptsWrong: Float = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pronunciation_layout)
        title = "Pronunciation Exercise"
        generateWord()
        textToSpeech = TextToSpeech(this, this)

        btnPronounce.setOnClickListener {
            SpeechFunction()
        }

        btnNext.setOnClickListener {
            if (!pronounced) {
                val mAlertDialogBuilder = AlertDialog.Builder(this)
                mAlertDialogBuilder.setMessage("You haven't pronounced the word. Do you want to continue? You will lose points.")
                mAlertDialogBuilder.setPositiveButton("Yes"){_, _, ->
                    generateWord()
                }
                mAlertDialogBuilder.setNegativeButton("No", null)
                val mAlertDialog = mAlertDialogBuilder.create()
                mAlertDialog.show()
            } else {
                generateWord()
            }
        }

        imgSpeakUp.setOnClickListener {
            textToSpeechFunction()
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_finish -> {
                if (cntCorrect != 0) {
                    perCorrect = ((cntCorrect.toFloat()/cntAll.toFloat())*100)
                }
                if (cntAttemptsCorrect != 0) {
                    perAttemptsCorrect = ((cntAttemptsCorrect.toFloat()/cntAttemptsAll.toFloat())*100)
                }
                if (cntAttemptsWrong != 0) {
                    perAttemptsWrong = ((cntAttemptsWrong.toFloat()/cntAttemptsAll.toFloat())*100)
                }

                val mAlertDialogBuilder = AlertDialog.Builder(this)
                mAlertDialogBuilder.setTitle("Do you want to finish exercise?")
                mAlertDialogBuilder.setMessage("Words: ${cntAll}\nPronouned: ${cntCorrect}  (${"%.2f".format(perCorrect)}%)\nTotal attempts:  ${cntAttemptsAll}\nCorrect attempts: ${cntAttemptsCorrect}  (${"%.2f".format(perAttemptsCorrect)}%)\nWrong attempts: ${cntAttemptsWrong}  (${"%.2f".format(perAttemptsWrong)}%)")
                mAlertDialogBuilder.setPositiveButton("Yes"){_, _, ->
                    finish();
                }
                mAlertDialogBuilder.setNegativeButton("No", null)
                val mAlertDialog = mAlertDialogBuilder.create()
                mAlertDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("SetTextI18n")
    fun generateWord() {
        btnPronounce.text = "Pronounce"
        cntAll++
        val random = (0..152).random()
        val randWord = resources.openRawResource(R.raw.wordlist).bufferedReader().useLines { it.elementAtOrNull(random) ?: "" }
        val randPronounce = resources.openRawResource(R.raw.pronunciation_list).bufferedReader().useLines { it.elementAtOrNull(random) ?: "" }
        txtWord.text = randWord
        txtPronunciation.text = "[$randPronounce]"
        showGif()
        btnNext.visibility = View.INVISIBLE
    }

    private fun SpeechFunction() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something...")
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (exp: ActivityNotFoundException) {
            Toast.makeText(this, "Speech Not Supported.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if(resultCode == RESULT_OK || null != data) {
                val res: ArrayList<String> = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>
                val pronouncedWord = res[0].toUpperCase()
                if (pronouncedWord == txtWord.text.toString().toUpperCase()) {
                    btnPronounce.text = "Pronounce"
                    btnNext.text = "Next"
                    cntAttemptsAll++
                    imgResult.setImageResource(R.drawable.correct);
                    imgResult.visibility = View.VISIBLE
                    btnNext.visibility = View.VISIBLE
                    cntCorrect++
                    cntAttemptsCorrect++
                    pronounced = true

                } else {
                    btnNext.text = "Skip"
                    btnPronounce.text = "Retry";
                    cntAttemptsAll++
                    imgResult.setImageResource(R.drawable.wrong);
                    imgResult.visibility = View.VISIBLE
                    btnNext.visibility = View.VISIBLE
                    cntAttemptsWrong++
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS) {
            val res: Int = textToSpeech.setLanguage((Locale.US))
            if(res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "This language is not supported.", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Failed to initialize.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun textToSpeechFunction() {
        val strText = txtWord.text.toString()
        textToSpeech.speak(strText, TextToSpeech.QUEUE_FLUSH, null)
    }

    override fun onDestroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.pronunciation_menu, menu)
        return true
    }

    fun showGif() {
        Glide.with(this).load(R.drawable.listening).into(imgResult)
    }

}