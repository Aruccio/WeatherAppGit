package com.example.weatherappka

import android.app.ActionBar
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    val city: String = "KNUROW"
    val api: String="e9fc7d526d6eeebffcd1f29c962598fd"
    val lang :String="pl"
 //   var iselder : Boolean=false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WeatherTask().execute()

        val bReload = findViewById(R.id.bReload) as Button

        bReload.setOnClickListener {
            Toast.makeText(this@MainActivity, "Aktualizuję dane.", Toast.LENGTH_SHORT).show()
            WeatherTask().execute()
        }


    }
    inner class WeatherTask() : AsyncTask<String, Void, String>()
    {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loaderbar).visibility= View.VISIBLE
        }

        override fun doInBackground(vararg params: String?): String {
           var response:String?
           try {
                response= URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$api&lang=$lang")
                    .readText(Charsets.UTF_8)
           }
           catch(e:Exception)
           {
               response=null
           }

            return response.toString()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val updatetat : Long = jsonObj.getLong("dt")
                val updateattext = "Zaktualizowano: "+SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatetat*1000))
                val temp = main.getString("temp")+"°C"
                val tempMin = "Min.Temp: "+main.getString("temp_min")+"°C"
                val tempMax = "Max.Temp: "+main.getString("temp_max")+"°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val sunrise:Long = sys.getLong("sunrise")
                val sunset :Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")+" m/s²"
                val weatherDescripion = weather.getString("description").uppercase()
                val address = jsonObj.getString("name")+", "+sys.getString("country")

                findViewById<TextView>(R.id.tAdress).text = address
                findViewById<TextView>(R.id.tAktualization).text = updateattext
                findViewById<TextView>(R.id.tStatus).text = weatherDescripion
                findViewById<TextView>(R.id.tTemp).text = temp
                findViewById<TextView>(R.id.tTempMin).text = tempMin
                findViewById<TextView>(R.id.tTempMax).text = tempMax
                findViewById<TextView>(R.id.tSunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.tSunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
                findViewById<TextView>(R.id.tWind).text = windSpeed
                findViewById<TextView>(R.id.tPressure).text = pressure
                findViewById<TextView>(R.id.tHumidity).text = humidity


                findViewById<ProgressBar>(R.id.loaderbar).visibility=View.GONE
                findViewById<RelativeLayout>(R.id.main_fragment).visibility=View.VISIBLE

            }
            catch(e:Exception)
            {
                print(".......... "+e);
            }
        }
    }
}