package com.example.weatherappka

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.findNavController
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AllElder.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllElder : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val city: String = "GLIWICE"
    val api: String="e9fc7d526d6eeebffcd1f29c962598fd"
    val lang :String="pl"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_elder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WeatherTask(view,city,api,lang);

        //zmien tryb
        view.findViewById<Button>(R.id.bChange).apply {
            setOnClickListener {

                findNavController().navigate(R.id.action_allElder_to_allNormal)

            }
        }

        //przeladuj dane
        view.findViewById<Button>(R.id.bReload).apply {
            setOnClickListener {
                WeatherTask(view, city, api, lang).execute()

            }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AllElder.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllElder().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        //klasa pobierajaca dane z API
        class WeatherTask(view: View, c:String, a:String, l:String) : AsyncTask<String, Void, String>()
        {
            var v: View = view;
            var city =c;
            var api = a;
            var lang = l;

            override fun onPreExecute() {
                super.onPreExecute()
            //    v.findViewById<ProgressBar>(R.id.loaderbar).visibility= View.VISIBLE
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

                    //całe sciagniecie danych z API oraz podpięcie ich do odpowiednich textBox

                    val jsonObj = JSONObject(result)
                    val main = jsonObj.getJSONObject("main")
                    val sys = jsonObj.getJSONObject("sys")
                    val wind = jsonObj.getJSONObject("wind")
                    val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                    val updatetat : Long = jsonObj.getLong("dt")
                    val updateattext = "Zaktualizowano: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                        Date(updatetat*1000)
                    )
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

                    v.findViewById<TextView>(R.id.tAdress).text = address
                    v.findViewById<TextView>(R.id.tAktualization).text = updateattext
                    v.findViewById<TextView>(R.id.tStatus).text = weatherDescripion
                    v.findViewById<TextView>(R.id.tTemp).text = temp
                    v.findViewById<TextView>(R.id.tTempMin).text = tempMin
                    v.findViewById<TextView>(R.id.tTempMax).text = tempMax
                    v.findViewById<TextView>(R.id.tSunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                        Date(sunrise*1000)
                    )
                    v.findViewById<TextView>(R.id.tSunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                        Date(sunset*1000)
                    )
                    v.findViewById<TextView>(R.id.tWind).text = windSpeed
                    v.findViewById<TextView>(R.id.tPressure).text = pressure
                    v.findViewById<TextView>(R.id.tHumidity).text = humidity


                    v.findViewById<ProgressBar>(R.id.loaderbar).visibility=View.GONE
                    v.findViewById<RelativeLayout>(R.id.main_fragment).visibility=View.VISIBLE

                }
                catch(e:Exception)
                {
                    print(".......... "+e);
                }
            }
        }
    }
}