package my.weather.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_open_weather.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OpenWeatherActivity : AppCompatActivity(), LocationListener {

    private val APPID : String = "500d0cb81b9f20395fa1e45179a8b5f5"
    private val PERMISSION_REQUEST_CODE = 2000
    private lateinit var backPressHolder: onBackPressHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_weather)


        backPressHolder = onBackPressHolder()
        getLocationinfo()

        setting.setOnClickListener {
            startActivity(Intent(this,AccoundSettingActivity::class.java))

        }
    }

    // 기존 메소드인 onBackPressed()의 superCall이 아닌
    // onBackPressHolder에서 지정한 onBackPressed()를 실행한다
    override fun onBackPressed() {
        backPressHolder.onBackPressed()
    }

    private fun drawCurrentWeather(currentWeather : Totalweather) {
        //public inline fun <T, R> with(receiver: T, f: T.() -> R): R = receiver.f()
        // receiver인 T는 currentWeather
        // f는 currentWeather.{멤버 변수 접근 내용}
        // 리턴 값인 R은 currnentWeather.{멤버 변수에 대한 접근 내용}

        // with은 확장 함수 없이 () 안의 receiver에게 {} 안에 있는 멤버 변수의 값에 접근 할 때 사용한다.
        // 날씨 정보를 받은 currentWeather를 통해서 각 멤버의 값을 가져온다
        with(currentWeather){

            // getOrNull에서 Null 반환시 .? SafeCode 실행
            this.weather?.getOrNull(0)?.let {

                it.icon?.let { val glide = Glide.with(this@OpenWeatherActivity)
                    glide.load(Uri.parse("https://openweathermap.org/img/w/" + it + ".png"))
                        .into(current_icon) }
                it.main?.let { current_main.text = it }

                it.description?.let { current_description.text = it }

            }


            this.main?.tempMax?.let {
                // text의 포맷 설정
                String.format("%.1f",it)
                current_max.text = it.toString() }
            this.main?.temp?.let {
                String.format("%.1f",it)
                current_now.text = it.toString() }
            this.main?.tempMin?.let {
                String.format("%.1f",it)
                current_min.text = it.toString() }

            // 결과 출력이 준비되면 로딩창을 삭제하고 내용을 출력함
            loading_view.visibility = View.GONE
            weather_view.visibility = View.VISIBLE


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                // 위치 권한이 없을 때는 위치 권한을 얻고 끝난다
                // 그렇기 때문에 onActivityResult를 통해서
                // 위치정보를 얻은 즉시 getLocationinfo()의 권한이 있을때의 코드부분이 실행된다
                getLocationinfo()
            }

        }
    }

    private fun requestWeatherInfoOfLocation(latitude : Double, longitude : Double){
        (application as WeatherApplication)
            .requestService()
            ?.getWeatherInfoOfCoordinates(latitude,longitude,APPID,"metric","kr")
            ?.enqueue(object : Callback<Totalweather>{
                override fun onFailure(call: Call<Totalweather>, t: Throwable) {
                    // 로딩화면의 TEXT를 변경
                    loading_text.text = "로딩 실패"
                }
                override fun onResponse( call: Call<Totalweather>, response: Response<Totalweather>) {

                    if(response.isSuccessful){
                        val totalwether = response.body()
                        totalwether?.let {
                            drawCurrentWeather(it)
                        }
                    }else loading_text.text = "로딩 실패"
                }
            })

    }

    private fun getLocationinfo(){
        if(Build.VERSION.SDK_INT >= 23
            && ContextCompat.checkSelfPermission
                (this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),PERMISSION_REQUEST_CODE
            )
        } else {
            // 권한이 있어 즉시 위도 경도를 수집한다
            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            // getLastKnownLocation으로 가장 최근 위치정보를 수집한 내용을 가져온다
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if(location!=null){
                val latitude = location?.latitude
                val longitude = location?.longitude
                requestWeatherInfoOfLocation(latitude = latitude, longitude = longitude)

                // 만약 가장 최근 위치정보를 수집한 내용이 없다면
                // requestLocationUpdates를 통해 새롭게 위치정보를 업데이트 해야한다
            } else {
                // requestLocationUpdates에 필요한 각 인수들을 작성하고
                // LocationListener를 마지막에 삽입하는데 현재 클래스인
                // OpenWeatherActivity가 LocationListener를 구현하는 클래스로 나타내고
                // LocationListener의 필요한 메소드를 현재 클래스에 구현해 this가 곧 LocationListener가 될 수 있다

                // 이외에 따로 class를 LocationListener를 구현해 삽입하거나
                // object : LocationListener {impliments ...} 형식으로 구현할 수 있다
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    3000L,0F,this)
                // 한 번 업데이트 후 위치 정보가 지속적으로 갱신되는 것을 종료함
                locationManager.removeUpdates(this)
            }

        }
    }

    //(1) 이전 전부 설정하는 방식
 /*   private fun setRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(Service::class.java)

        service.getWeatherInfoOfLocation("London",APPID)
            .enqueue(object : Callback<JsonObject>{
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                }
            })
    }*/

    // 도시 이름으로 요청하는 방식
    private fun requestCurrentWeather() {
        Log.d("test","???")
        // application을 weatherApplication의 형태로 불러내 weatherApplication의 requestService()를 호출
        // application를 이용, WeatherApplication의 setupRetrofit()을 계속해서 재사용이 가능하다
        // 만약 application을 이용하지 않았다면 setupRetrofit()의 내용을 각각 전부 설정(1) 해야 했다
        (application as WeatherApplication)
            // requestService()는 return 값이 결국  weatherApplication의 sevice이기 때문에
            // 형태는 service.getWeatherInfoOfLocation("London",APPID) ... 와 동일함
            .requestService()
            ?.getWeatherInfoOfLocation("London",APPID)
                // JsonObject의 타입을 전부 Totalweather 타입으로 변경해 줬음
            ?.enqueue(object : Callback<Totalweather> {
                override fun onFailure(call: Call<Totalweather>, t: Throwable) {

                }
                override fun onResponse(call: Call<Totalweather>, response: Response<Totalweather>) {
                    // 응답 받은 내용이 response의 body 부분에 명시되어 있기 때문에 이를 호출해 입력받음
                    val totalweather = response.body()
                }
            })
    }

    // LocationListener의 구현 부분
    override fun onLocationChanged(location: Location?) {
        val latitude = location?.latitude
        val longitude = location?.longitude
        if(latitude != null && longitude != null) {
            requestWeatherInfoOfLocation(latitude = latitude, longitude = longitude)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

    // BackButton Pressed 시 Toast 호출 후 종료
    inner class onBackPressHolder() {
        private var backPressHolder : Long = 0

        fun onBackPressed() {
            if(System.currentTimeMillis() > backPressHolder+2000){
                backPressHolder = System.currentTimeMillis()
                showBackToast()
                return
            }
            if(System.currentTimeMillis() <= backPressHolder+2000){
                finishAffinity()
            }
        }
        fun showBackToast() {
            Toast.makeText(
                this@OpenWeatherActivity,
                "한번 더 누르시면 종료됩니다.",
                Toast.LENGTH_SHORT).show()
        }
    }
}
