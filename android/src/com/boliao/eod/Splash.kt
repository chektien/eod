/**
 * WHAT IS THIS?
 * Example android app using a mix of libraries, e.g., libgdx for graphics.
 *
 * 0. browse through an overview of the code structure
 *
 * # WEEK08: SERVICES
 * Run through several use cases for different background processing requirements.
 *
 * 1. revise persistent storage by using prefs to determine if username exists.
 * 2. create an IntentService to "encrypt" username in background
 * 3. observe the started service: GameStateService
 * 4. observe the binding code provided in GameStateService
 * 5. configure notifications for the GameStateService when bugs spawn
 * 6. convert the started service to a foreground service
 * 7. create a scheduled service (once app boots) to remind user to charge the phone periodically
 *
 * # WEEK09: THREADING
 * A persistent weather widget.
 *
 * 1. See the use of raw java threads in the bug spawning code in GameStateService
 * 2. Create an Asynctask to encrypt usernames in the background
 * 3. Create a weather worker Handlerthread to fetch weather updates in the background
 * 4. Replace Asynctask with coroutine approach
 *
 * # WEEK10: RECEIVERS
 * A static receiver on boot for reminders and
 * dynamically broadcasting steps to be received by another app
 *
 * 1. adding a static OnBootReceiver ON_BOOT via the manifest
 * 2. create an intent to be dynamically broadcasted to the world (on your device)
 * # WEEK10.5: NETWORKING
 * Fetching and showing the weather from a RESTful API.
 *
 * 1. Setting network permissions
 * 2. Using networking libs Volley
 */

package com.boliao.eod

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.coroutines.*

/**
 * This is the splash view that records who is playing.
 */
class Splash : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var startAndroidLauncher: Intent

    private fun launchGame() {
        startActivity(startAndroidLauncher)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // init launch game intent
        startAndroidLauncher = Intent(this@Splash, AndroidLauncher::class.java)

        // get refs to UI components
        val playBtn = findViewById<Button>(R.id.play_btn)
        val usernameEdtTxt = findViewById<EditText>(R.id.name_edtxt)
        val msgTxtView = findViewById<TextView>(R.id.msg_txtview)
        val weatherTxtView = findViewById<TextView>(R.id.weather_txtview)

        // show splash text
        msgTxtView.setText(R.string.welcome_note)

        // TODO THREADING 2: create a persistent weather widget
        // An MVVM Splash ViewModel is already set up.
        // Splash Activity View -> Splash ViewModel -> WeatherRepo Model
        // WeatherRepo currently has a mock stub to return static mock data, provided live by
        // weatherData in SplashViewModel.
        // - set up weatherTextView here to observe the weatherData
        // - goto WeatherRepo for THREADING 3
        // Q: Do I (Splash Activity) need to know about WeatherRepo?

        // TODO NETWORKING 1: init the network request queue singleton object (volley)
        // - goto NETWORKING 0 in manifest
        // - create NetWorkRequestQueue singleton
        // - set NetworkRequestQueue's context to this
        // - goto NETWORKING 2 in WeatherRepo
        NetworkRequestQueue.setContext(this)

        // val splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        // val splashViewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        val splashViewModel: SplashViewModel by viewModels()
        splashViewModel.weatherData.observe(this, Observer {
            weatherTxtView.text = it
        })

        splashViewModel.loginStatus.observe(this, Observer {
            if (it) {
                msgTxtView.text = "LOGIN DONE. Starting..."
                launchGame()
            } else {
                msgTxtView.text = "Name OREDI exist liao..."
            }
        })

        // start game on click "PLAY"
        playBtn.setOnClickListener {
            msgTxtView.text = "Encrypting in coroutine heaven..."
            splashViewModel.login(usernameEdtTxt.text.toString())
        }

        // provide a way to stop the service
        findViewById<Button>(R.id.exit_btn).setOnClickListener {
            AndroidLauncher.startServiceIntent?.let {
                stopService(it)
            }
            finish()
        }

    }

    companion object {
        private const val TAG = "Splash"

        /**
         * [DEPRECATED] AsyncTask to "encrypt" username
         * - heavy lifting in the background to be posted back to UI
         * - static class so as to prevent leaks
         * - internal ctor to only allow enclosing class to construct
         * - need a ref to update UI thread, so use WeakReference (a.k.a. shared_ptr)
         * - onProgressUpdate(Integer... progress) left as an exercise
         * - note: publishProgress(Integer) is in built to pass progress to above from doInBackground
         */
        /*
        private class EncryptTask internal constructor(act: Splash) : AsyncTask<String?, Void?, Boolean>() {
            // hold the Activity to get all the UI elements
            // - use weak reference so that it does not leak mem when activity gets killed
            var wr_splash: WeakReference<Splash> = WeakReference(act)

            override fun onPreExecute() {
                super.onPreExecute()
                val splash = wr_splash.get()
                if (splash != null) {
                    (splash.findViewById<View>(R.id.msg_txtview) as TextView).text = "encrypting"
                }
            }

            override fun doInBackground(vararg str: String?): Boolean {
                try {
                    Thread.sleep(3000)
                    // do something to the str
                } catch (e: InterruptedException) {
                    return false
                }
                return true
            }

            override fun onPostExecute(b: Boolean) {
                super.onPostExecute(b)
                val splash = wr_splash.get()
                if (splash != null) {
                    (splash.findViewById<View>(R.id.msg_txtview) as TextView).text = "The encryption is:$b"
                    splash.launchGame()
                }
            }
        }
         */
    }
}