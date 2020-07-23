package my.weather.myapplication

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// 전체 body
/*base: "stations"
clouds: {all: 75}
cod: 200
coord: {lon: -0.13, lat: 51.51}
dt: 1582882970
id: 2643743
main: {temp: 278.02, feels_like: 273.46, temp_min: 276.48, temp_max: 279.26, pressure: 1011, humidity: 81}
name: "London"
rain: {1h: 0.25}
sys: {type: 1, id: 1414, country: "GB", sunrise: 1582872569, sunset: 1582911434}
timezone: 0
visibility: 10000
weather: [{id: 500, main: "Rain", description: "light rain", icon: "10d"}]
wind: {speed: 4.1, deg: 160}*/

// 전체의 body 내에서 각 내용을 받는데
// base 자체는 String 타입이기 때문에 그대로 String으로 받는다
// Clouds의 경우에는 자체가 프리미티브 타입이 아니라서 Class로 생성하고
// Clouds 내부의 all 이라는 Int타입을 선언했다
// 그리고 Totalweather에서 Clouds 객체를 clouds로 선언했다
// Parsing을 위해서 각 body의 KEY값을 동일한 변수로 선언해줘야 한다 (base... clouds... etc)

// : Serializable 은 데이터가 KEY VALUE에 맞는 값으로 연결해주는 역할을 함

class Totalweather (
   // var base : String?  = null  base와 clouds는 예시라서 사용하지는 않음
   //  var clouds : Clouds? = null
    var main : Main? = null,
    // Weather은 []로 묶인 배열타입이기 때문에 weather타입의 ArrayList로 생성했음
    var weather : ArrayList<Weather>? = null
) : Serializable

/*class Clouds{
    var all : Int? = null
}*/

class Main(
    var humidity : Int? = null,
    var pressure : Int? = null,
    var temp : Double? = null,
    @SerializedName("temp_max") // 파싱값을 변수로 받을 때 이름을 수정하는 방법
    var tempMax : Double? = null,
    @SerializedName("temp_min")
    var tempMin : Double? = null

) : Serializable

class Weather(
    var description : String? = null,
    var icon : String? = null,
    var main : String? = null

) : Serializable