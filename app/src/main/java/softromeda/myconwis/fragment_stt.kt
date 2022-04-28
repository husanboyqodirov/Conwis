package softromeda.myconwis

import android.app.Activity.RESULT_OK
import android.content.*
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.util.*
import kotlin.collections.ArrayList

class Fragment_stt: Fragment() {
    private val REQUEST_CODE_SPEECH_INPUT = 100
    private lateinit var btnListen: Button
    private lateinit var btnCopy: Button
    private lateinit var btnClear: Button
    private lateinit var btnShare: Button
    private lateinit var resTextView: TextView
    lateinit var setLang: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stt, container, false)
        val sharedPreferences = activity?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            if(sharedPreferences.contains("PreferredLang")) {
                setLang = sharedPreferences.getString("PreferredLang", "").toString()
            }
        }
        activity?.setTitle("ConWis - Speech to Text")

        btnListen = view.findViewById(R.id.btnListen)
        btnCopy = view.findViewById(R.id.btnCopy)
        btnClear = view.findViewById(R.id.btnClear)
        btnShare = view.findViewById(R.id.btnShare)

        resTextView = view.findViewById(R.id.txtResult)

        btnListen.setOnClickListener {
            SpeechFunction()
        }
        btnCopy.setOnClickListener {
            val textToCopy = resTextView.text
            val clipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", textToCopy)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_LONG).show()
        }
        btnClear.setOnClickListener {
            resTextView.setText("")
        }
        btnShare.setOnClickListener{
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, resTextView.text.toString())
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
        return view
    }
    private fun SpeechFunction() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        if(setLang == "Korean") {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREA.toString())
        }
        else {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something...")
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (exp: ActivityNotFoundException) {
            Toast.makeText(context, "Speech Not Supported.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if(resultCode == RESULT_OK || null != data) {
                val res: ArrayList<String> = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>
                resTextView.text = res[0]
            }
        }
    }
}