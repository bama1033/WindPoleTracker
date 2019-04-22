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

class TopTrackerActivity : AppCompatActivity() {

    private lateinit var list: List<TextView>
    private lateinit var links: MutableList<String>
    private var counter = 0
    private var textidcounter = 0

    private val trackerBotKey = "TrackerBot"


    private val bottomHistory = arrayListOf<String>(
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_tracker)
        list = listOf(myID4, myID5)
        setSupportActionBar(toolbar)

        checkHistory()

        fab.setOnClickListener { view ->
            useSoup()
        }
        fab1.setOnClickListener { view ->
            writeData(myID4)
            writeData(myID5)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun checkHistory() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences != null) {
            counter = sharedPreferences.getInt(trackerBotKey, 0)
            if (counter.equals(0)) {
            } else {
                fillList(counter)
            }
        }
    }

    private fun writeData(myID: TextView) {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//        var trackerBotValue: Int
        var value: String
        if (sharedPreferences != null) {
            counter = sharedPreferences.getInt(trackerBotKey, 0)
            with(sharedPreferences.edit()) {
                counter += 1
                val countervalue = counter
                val key = "ValueBot$counter"
                value = myID.text.toString()
                putString(key, value)
                putInt(trackerBotKey, countervalue)
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
                    val value = "ValueBot$item"
                    bottomHistory.add(sharedPreferences.getString(value, "error"))
                }
            }
            layoutManager = LinearLayoutManager(context)
            adapter = Adapter(bottomHistory)
        }
    }


    fun useSoup() {
        try {
            doAsync {
                var doc = Jsoup.connect("http://192.168.1.3/showvalue.htm").timeout(5000).get()
                links = doc
                    .select("tr")
                    .eachText()
//                .attr("href")
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()

        }
    }

    fun taskDone() {
        runOnUiThread {
            for (e in links) {
                when (counter) {
                    in 5..6 -> {
                        var x = e.substring(5, e.length)
                        list[textidcounter].text = x
//                        list[textidcounter].text = e
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


