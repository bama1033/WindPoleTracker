package com.android.windpoletracker.graphactivitys

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.android.windpoletracker.R
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import kotlinx.android.synthetic.main.activity_global_graph.*
import java.util.*

class GlobalGraphActivity : AppCompatActivity() {

    private var counter = 0
    private val trackerGlobalKey = "TrackerGlobal"
    private val globalDateKey = "DateGlobal"
    private val graphData: ArrayList<ValueDataEntry> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_global_graph)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        checkHistory()
        createGraph()
    }

    private fun checkHistory() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences != null) {
            counter = sharedPreferences.getInt(trackerGlobalKey, 0)
            if (counter == 0) {
            } else {
                fillList(counter)
            }
        }
    }

    private fun fillList(value: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        for (item: Int in value downTo 1) {
            if (sharedPreferences != null) {
                val valueData = "TrackerGlobal$item"
                val dateData = "DateGlobal$item"
                graphData.add(
                    ValueDataEntry(
                        sharedPreferences.getString(dateData, "error"),
                        sharedPreferences.getString(valueData, "error").toInt()
                    )
                )

            }
        }
    }

    private fun createGraph() {
        val line = AnyChart.line()
        line.background()
        line.animation(true)

        line.dataArea().background().enabled(true)
        line.dataArea().background().fill("#D3D3D3 0.2")

        line.xAxis(0).enabled(true)
        line.xAxis(0).orientation("Bottom")
        line.xAxis(0).title("Entries")
        line.xAxis(0).staggerMode(true)
        line.xAxis(0).labels().rotation(-90)
        line.xScroller(true)

        line.xAxis(0).staggerMaxLines(3)

        line.yAxis(0).enabled()
        line.yAxis(0).orientation("left")
        line.yAxis(0).title("W/m²")

        line.data(graphData as List<DataEntry>?)
        val anyChartView = any_chart_view as AnyChartView
        anyChartView.setChart(line)
    }
}
