package com.android.windpoletracker.graphactivitys

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import kotlinx.android.synthetic.main.activity_top_graph.*
import java.util.*


class TopGraphActivity : AppCompatActivity() {

    private var counter = 0
    private val trackerTopKey = "TrackerTop"
    private val graphData: ArrayList<ValueDataEntry> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.android.windpoletracker.R.layout.activity_top_graph)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        checkHistory()
        createGraph()
    }

    private fun checkHistory() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences != null) {
            counter = sharedPreferences.getInt(trackerTopKey, 0)
            if (counter != 0) {
                fillList(counter)
            }
        }
    }

    private fun fillList(value: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        for (item: Int in value downTo 1) {
            if (item % 2 == 0) {
                if (sharedPreferences != null) {
                    val valueData = "TrackerTop$item"
                    val dateData = "DateTop$item"
                    val t = sharedPreferences.getString(valueData, "error")
                    val y = t.replace(",", "").toFloat()
                    graphData.add(
                        ValueDataEntry(
                            sharedPreferences.getString(dateData, "error"),
                            y
                        )
                    )
                }
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
        line.yAxis(0).title("m/s")

        line.data(graphData as List<DataEntry>?)
        val anyChartView = any_chart_view as AnyChartView
        anyChartView.setChart(line)
    }
}
