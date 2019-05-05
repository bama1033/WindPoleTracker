package com.android.windpoletracker

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_top_tracker.*
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

class TopTrackerActivity : AppCompatActivity() {

    private lateinit var list: List<TextView>
    private lateinit var links: MutableList<String>
    private var counter = 0
    private var textidcounter = 0

    private val trackerTopKey = "TrackerTop"


    private val topHistory = arrayListOf<String>(
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_tracker)
        list = listOf(myID4, myID5)
        setSupportActionBar(toolbar)

        checkHistory()

        fab.setOnClickListener { view ->
            useSoup()
        }
        fab1.setOnClickListener { view ->
            if (myID4.text.contains("Bitte neu laden") || myID5.text.contains("Bitte neu laden")) {
                Toast.makeText(this, "Daten sind fehlerhaft bitte neu laden!", Toast.LENGTH_SHORT).show()
            } else {
                writeData(myID4)
                writeData(myID5)
            }
        }
        try {
            useSoup()
        }
        catch (e: Exception) {
            // handler
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun checkHistory() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences != null) {
            counter = sharedPreferences.getInt(trackerTopKey, 0)
            if (counter.equals(0)) {
            } else {
                fillList(counter)
            }
        }
    }

    private fun writeData(myID: TextView) {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//        var trackerBotValue: Int
        var value:String
        val sdf = SimpleDateFormat("dd.MM.yyyy 'um' HH:mm:ss")
        val currentDateandTime = sdf.format(Date())
        if (sharedPreferences != null) {
            counter = sharedPreferences.getInt(trackerTopKey, 0)
            with(sharedPreferences.edit()) {
                counter += 1
                val countervalue = counter
                val key= "ValueTop$counter"
                value= currentDateandTime+", " + myID.text.toString()
                putString(key, value)
                putInt(trackerTopKey, countervalue)
                apply()
            }
//            trackerBotValue = sharedPreferences.getInt(trackerBotKey, 0)
            if (counter.equals(0)) {
            } else {
//                fillList(trackerBotValue)
                Toast.makeText(this, "$value has been added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun fillList(value: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        recview.apply {
            for (item: Int in value downTo 1) {
                if (sharedPreferences != null) {
                    val value= "ValueTop$item"
                    topHistory.add(sharedPreferences.getString(value, "error"))
                }
            }
            layoutManager = LinearLayoutManager(context)
            adapter = Adapter(topHistory)
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
                    in 4..5 -> {
                        var x = e.substring(5, e.length)
                        if (x.toLowerCase().contains("windrichtung top")||x.toLowerCase().contains("windgeschw bot"))
                        else{
                            x=("Bitte neu laden")
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
            handler()
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            taskDone()
        }
    }
}


