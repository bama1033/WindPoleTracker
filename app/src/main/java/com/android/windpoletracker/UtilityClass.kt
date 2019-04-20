package com.android.windpoletracker

import android.content.Context
import android.os.AsyncTask
import android.widget.TextView
import android.widget.Toast
import org.jsoup.Jsoup
import java.util.regex.Pattern


private lateinit var links: MutableList<String>
private lateinit var list: List<TextView>
private var counter = 0
private var textidcounter = 0
fun useSoup(c: Context) {
    try {
        doAsync {
            var doc = Jsoup.connect("http://192.168.1.3/showvalue.htm").timeout(5000).get()
            links = doc
                .select("tr")
                .eachText()
//                .attr("href")
        }
    } catch (e: Exception) {
        Toast.makeText(c, e.toString(), Toast.LENGTH_SHORT).show()
    }
}

fun taskDone() {
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

class doAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
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