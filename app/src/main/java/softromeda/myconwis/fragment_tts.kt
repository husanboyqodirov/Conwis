package softromeda.myconwis

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_tts.*
import java.util.*

class Fragment_tts: Fragment(), TextToSpeech.OnInitListener  {

    var setLang = "English"
    lateinit var textToSpeech: TextToSpeech
    lateinit var btnSpeak: Button
    lateinit var editText: EditText


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tts, container, false)
        btnSpeak = view.findViewById(R.id.btnSpeak)
        editText = view.findViewById(R.id.edtText)
        textToSpeech = TextToSpeech(activity, this)
        btnSpeak.setOnClickListener {
            textToSpeechFunction()
        }
        val sharedPreferences = activity?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            if(sharedPreferences.contains("PreferredLang")) {
                setLang = sharedPreferences.getString("PreferredLang","").toString()
            }
        }

        activity?.setTitle("ConWis - Text to Speech")

        return view
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS) {

            if(setLang == "Korean") {
                val res: Int = textToSpeech.setLanguage((Locale.KOREA))
                if(res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, "This language is not supported.", Toast.LENGTH_SHORT).show()
                } else {
                    btnSpeak.isEnabled = true
                    textToSpeechFunction()
                }
            }
            else {
                val res: Int = textToSpeech.setLanguage((Locale.US))
                if(res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, "This language is not supported.", Toast.LENGTH_SHORT).show()
                } else {
                    btnSpeak.isEnabled = true
                    textToSpeechFunction()
                }
            }

        } else {
            Toast.makeText(context, "Failed to initialize.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun textToSpeechFunction() {
        val strText = editText.text.toString()
        textToSpeech.speak(strText, TextToSpeech.QUEUE_FLUSH, null)
        if(editText.text.isNotEmpty()) {
            Toast.makeText(context, strText, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        if(textToSpeech != null) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }
}