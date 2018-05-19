package pl.angelhackkrakow.pickmeup

import ai.api.android.AIConfiguration
import ai.api.model.AIError
import ai.api.model.AIResponse
import ai.api.ui.AIButton
import android.Manifest
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1)
        val config = AIConfiguration(DIALOG_FLOW_TOKEN,
                ai.api.AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System)
        micButton.initialize(config)
        micButton.setResultsListener(object : AIButton.AIButtonListener {
            override fun onCancelled() {
            }

            override fun onResult(result: AIResponse) {

                Log.d("ApiAi", "onResult")
            }

            override fun onError(error: AIError?) {
                Log.d("ApiAi", "onError")
            }
        })
    }

    companion object {
        const val DIALOG_FLOW_TOKEN = "dbb59867471149ecafd254c7263b8fd7"
    }
}
