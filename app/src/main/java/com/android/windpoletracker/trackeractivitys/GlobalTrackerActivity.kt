package com.android.windpoletracker.trackeractivitys

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.windpoletracker.Adapter
import com.android.windpoletracker.R
import com.android.windpoletracker.graphactivitys.GlobalGraphActivity
import kotlinx.android.synthetic.main.activity_global.*
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class GlobalTrackerActivity : AppCompatActivity() {
    private lateinit var list: List<TextView>
    private lateinit var links: MutableList<String>
    private var counter = 0
    private var textidcounter = 0
    private var callSuccess = false
    private val trackerGlobalKey = "TrackerGlobal"
    private val globalValueKey = "ValueGlobal"
    private val globalDateKey = "DateGlobal"
    private var dirtyWorkaround = ""

    private val globalHistory = arrayListOf<String>(
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_global)
        list = listOf(myID4)
        setSupportActionBar(toolbar)

        checkHistory()

        fab.setOnClickListener { view ->
            useSoup()
        }
        fab1.setOnClickListener { view ->
            if (myID4.text.contains("Bitte neu laden")) {
                Toast.makeText(this, "Daten sind fehlerhaft bitte neu laden!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                writeData(myID4)
            }
        }
        fab3.setOnClickListener { view ->
            val intent = Intent(this, GlobalGraphActivity::class.java)
            startActivity(intent)
        }
        try {
            useSoup()
        } catch (e: Exception) {
            // handler
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun checkHistory() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences != null) {
            counter = sharedPreferences.getInt(trackerGlobalKey, 0)
            if (counter != 0) {
                fillList(counter)
            }
        }
    }

    private fun writeData(myID: TextView) {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var value: String
        val sdf = SimpleDateFormat("dd.MM.yyyy 'um' HH:mm:ss")
        val currentDateAndTime = sdf.format(Date())
        if (sharedPreferences != null) {
            counter = sharedPreferences.getInt(trackerGlobalKey, 0)
            with(sharedPreferences.edit()) {
                counter += 1
                val countervalue = counter
                val key = "ValueGlobal$counter"
                val counterRegValue = dirtyWorkaround
                val keyRegValue = "TrackerGlobal$counter"
                val keyDate = "DateGlobal$counter"

                value = " " + currentDateAndTime + ",\n" + myID.text.toString()
                putString(key, value)
                putString(keyRegValue, counterRegValue)
                putString(keyDate, currentDateAndTime)
                putInt(trackerGlobalKey, countervalue)
                putInt(globalValueKey, countervalue)
                putInt(globalDateKey, countervalue)
                apply()
            }
            when {
                counter == 0 -> {
                }
                else -> Toast.makeText(this, "$value has been added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun fillList(value: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        recview.apply {
            for (item: Int in value downTo 1) {
                if (sharedPreferences != null) {
                    val valueItem = "ValueGlobal$item"
                    globalHistory.add(sharedPreferences.getString(valueItem, "error"))
                }
            }
            layoutManager = LinearLayoutManager(context)
            adapter = Adapter(globalHistory)
        }
    }


    fun useSoup() {
        try {
            doAsync {
                var doc = Jsoup.connect("http://192.168.1.3/showvalue.htm").timeout(5000).get()
                links = doc
                    .select("tr")
                    .eachText()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()

        }
    }

    fun taskDone() {
        runOnUiThread {
            for (e in links) {
                when (counter) {
                    6 -> {
                        var x = e.substring(5, e.length)
                        if (x.toLowerCase().contains("global")) {
                            val p = Pattern.compile("[0-9]{4}|[0-9]{3}|[0-9]{2}")
                            val m = p.matcher(x)
                            if (m.find()) {
                                val z = m.group()
                                dirtyWorkaround = z
                            }
                        } else {
                            x = ("Bitte neu laden")
                        }
                        list[textidcounter].text = x
                        textidcounter++
                    }
                }
                counter++
            }
            counter = 0
            textidcounter = 0
        }

    }

    @SuppressLint("StaticFieldLeak")
    inner class doAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
        init {
            execute()
        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                handler()
                callSuccess = true
            } catch (e: Exception) {
                callSuccess
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (callSuccess) {
                taskDone()
            }
        }
    }
}

