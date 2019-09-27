package com.android.windpoletracker

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.windpoletracker.trackeractivitys.BottomTrackerActivity
import com.android.windpoletracker.trackeractivitys.GlobalTrackerActivity
import com.android.windpoletracker.trackeractivitys.TopTrackerActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {
    private lateinit var list: List<TextView>

    private var counter = 0
    private var textidcounter = 0
    private lateinit var links: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list = listOf(myID1, myID2, myID3, myID4, myID5)
        fab.setOnClickListener {
            useSoup()
        }
        button.setOnClickListener {
            val intent = Intent(this, GlobalTrackerActivity::class.java)
            startActivity(intent)
        }
        button2.setOnClickListener {
            val intent = Intent(this, TopTrackerActivity::class.java)
            startActivity(intent)
        }
        button3.setOnClickListener {
            val intent = Intent(this, BottomTrackerActivity::class.java)
            startActivity(intent)
        }
//        val pie = AnyChart.line()
//        pie.background()
//        val data: ArrayList<ValueDataEntry> = ArrayList()
//        data.add(ValueDataEntry("01.07", 10000))
//        data.add(ValueDataEntry("02.07", 12000))
//        data.add(ValueDataEntry("03.07", 18000))
//
//        pie.dataArea().background().enabled(true)
//        pie.dataArea().background().fill("#D3D3D3 0.2")
//
//
//        pie.data(data as List<DataEntry>?)
//        val anyChartView = any_chart_view as AnyChartView
//        anyChartView.setChart(pie)
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
                    in 4..8 -> {
                        val matcher2 = Pattern.compile("[AIN.{0,10}]").matcher(e)
                        if (matcher2.find()) {
                            var x = e.substring(5, e.length)
                            list[textidcounter].text = x
                        }
                        textidcounter++
                    }
                }
                counter++
            }
            counter = 0
            textidcounter = 0
//            useSoup() if Autorefresh should be turned on
        }

    }

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



