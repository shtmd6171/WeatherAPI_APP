package my.weather.myapplication

import android.app.Application
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// <application android:name=".WeatherApplication" 을 지정해줘야 함

// Application은 App이 생성될 때 단 하나만 생성되고 (싱글톤), 종료시 삭제됨
// Application은 가장먼저 호출되므로, 최우선 수행을 원할 때, 여러 Activity에서 사용될걸 미리 정의해 둠
// 여기서는 Application에서 retrofit을 생성해두고 Activity들이 가져다 쓰게 끔 사용함
class WeatherApplication : Application() {

    var service : Service? = null

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        setupRetrofit()
    }

    private fun setupRetrofit() {

        // profiler 대신 stetho로 디버깅을 하기위한 필요 절차
        val httpClient = OkHttpClient.Builder()
        // 네트워크 통신 가로채기 (통신 여부 확인)를 위한 intercept
        httpClient.addNetworkInterceptor(StethoInterceptor())
        val client = httpClient.build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // stetho 디버깅을 위한 설정
            .build()

        service = retrofit.create(Service::class.java)
    }

    fun requestService(): Service? {
        return service
    }
}