package com.android.windpoletracker.graphactivitys

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import kotlinx.android.synthetic.main.activity_bottom_graph.*
import java.util.*


class BottomGraphActivity : AppCompatActivity() {

    private var counter = 0
    private val trackerBotKey = "TrackerBot"
    private val graphData: ArrayList<ValueDataEntry> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.android.windpoletracker.R.layout.activity_bottom_graph)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        checkHistory()
        createGraph()
    }

    private fun checkHistory() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences != null) {
            counter = sharedPreferences.getInt(trackerBotKey, 0)
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
                    val valueData = "TrackerBot$item"
                    val dateData = "DateBot$item"
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
        val pie = AnyChart.line()
        pie.background()

        pie.dataArea().background().enabled(true)
        pie.dataArea().background().fill("#D3D3D3 0.2")

        pie.xAxis(0).enabled(true)
        pie.xAxis(0).orientation("Bottom")
        pie.xAxis(0).title("Entries")
        pie.xAxis(0).staggerMode(true)
        pie.xAxis(0).labels().rotation(-90)
        pie.xScroller(true)

        pie.xAxis(0).staggerMaxLines(3)

        pie.yAxis(0).enabled()
        pie.yAxis(0).orientation("left")
        pie.yAxis(0).title("m/s")


        pie.data(graphData as List<DataEntry>?)
        val anyChartView = any_chart_view as AnyChartView
        anyChartView.setChart(pie)
    }
}
