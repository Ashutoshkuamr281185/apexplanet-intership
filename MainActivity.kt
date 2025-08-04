package com.P1.mywetherapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.P1.mywetherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*



class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("jaipur")
        SearchCity()
    }

    private fun SearchCity() {
        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val call = retrofit.getWeatherData(
            cityName,
            "34a1d464f946408053f8b2d83699019e",
            "metric"
        )

        call.enqueue(object : Callback<WetherApp> {
            override fun onResponse(call: Call<WetherApp>, response: Response<WetherApp>) {
                val weatherData = response.body()
                weatherData?.let {
                    val temperature = it.main.temp.toString()
                    val humidity = it.main.humidity
                    val windSpeed = it.wind.speed
                    val sunRise = it.sys.sunrise.toLong()
                    val sunSet = it.sys.sunset.toLong()
                    val seaLevel = it.main.pressure
                    val condition = it.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = it.main.temp_max
                    val minTemp = it.main.temp_min

                    binding.temp.text = "$temperature°C"
                    binding.wether.text = condition
                    binding.maxTemp.text = "Max Temp: $maxTemp°C"
                    binding.minTemp.text = "Min Temp: $minTemp°C"
                    binding.humidity.text = "$humidity%"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sea.text = "$seaLevel hPa"
                    binding.sunrise.text = "${time(sunRise)}"

                    binding.sunset.text = "${time(sunSet)}"
                    binding.condition.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text = "$cityName"


                    changeImagesAccordingToWeather(condition)


                }
            }



            override fun onFailure(call: Call<WetherApp>, t: Throwable) {
                Log.e("WEATHER", "Error: ${t.message}")
            }
        })
    }
    private fun changeImagesAccordingToWeather(condition: String) {
        when (condition) {

            "Sky", "Sunny" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy", "Haze"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }

        binding.lottieAnimationView.playAnimation()
    }
    private fun date(): String {
        val sdf = SimpleDateFormat("HH:MM", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }

    private fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
}