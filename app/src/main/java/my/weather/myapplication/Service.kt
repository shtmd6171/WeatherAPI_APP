package my.weather.myapplication

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Service {

    // request 항목 작성
    // query param 이전에 변환부분이 없는 부분까지 value로 작성
    @GET("/data/2.5/weather/")

    // query param 부분은 KEY/VALUE 값으로 설정되어 있고 Query 내부에 KEY를
    // 그 뒤에 변수 값을 지정해준다 q=London(Location)와 appid=KEY를 의미
    // 함수 getWeatherInfoOfLocation의 리턴 타입은  Call<JsonObject>로 지정해줌
    fun getWeatherInfoOfLocation(@Query("q") Location : String, @Query("APPID") KEY : String
    ) : Call<Totalweather>

    // Call<T>의 T는 response를 어떤 타입으로 파싱해서 받을지 명시하는 부분인데
    // 이 전에 <JsonObject>를 통해 통신시에 오브젝트 타입이 profiler로 넘어오는 것을 확인했다
    // 이제 Totalweather 타입으로 응답을 받을 것으로 지정했기 때문에 Totalweather 타입의 각 KEY에 VALUE값을 전달 받는다


    @GET("/data/2.5/weather/")
    fun getWeatherInfoOfCoordinates(
        @Query("lat") latitude : Double,
        @Query("lon") longitude : Double,
        @Query("APPID") KEY : String,
        @Query("units") units : String,
        @Query("lang") language : String
    ): Call<Totalweather>



}