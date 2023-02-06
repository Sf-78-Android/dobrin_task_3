package com.training.weatherapp.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.util.*
import com.training.weatherapp.R
import com.training.weatherapp.constatns.ApiConfig
import com.training.weatherapp.constatns.ApiConfig.METRIC_UNIT
import com.training.weatherapp.constatns.Constants.CELSIUS_SYMBOL
import com.training.weatherapp.constatns.Constants.DATE_ALLOCATION
import com.training.weatherapp.constatns.Constants.FAHRENHEIT_SYMBOL
import com.training.weatherapp.constatns.Constants.LR_SYMBOL
import com.training.weatherapp.constatns.Constants.MM_SYMBOL
import com.training.weatherapp.constatns.Constants.US_SYMBOL
import com.training.weatherapp.databinding.ActivityMainBinding
import com.training.weatherapp.models.WeatherResponse
import com.training.weatherapp.network.WeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*


class RequestManager(context: Context, binding: ActivityMainBinding) {
    private val mContext: Context = context
    private var mProgressDialog: Dialog? = null
    private var mWeatherList: WeatherResponse? = null
    private val mBinding: ActivityMainBinding = binding

    private fun hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }

    private fun showCustomProgressDialog() {
        mProgressDialog = Dialog(mContext)

        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)

        mProgressDialog!!.show()
    }

    fun getWeatherDetails(longitude: Double, latitude: Double): WeatherResponse? {

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: WeatherService = retrofit
            .create(WeatherService::class.java)

        val listCall: Call<WeatherResponse> = service
            .getWeather(latitude, longitude, ApiConfig.APP_ID, METRIC_UNIT)

        showCustomProgressDialog()

        listCall.enqueue(object : Callback<WeatherResponse> {

            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    hideProgressDialog()
                    mWeatherList = response.body()
                    setupUI()
                    Log.i("Response Result", "$mWeatherList")
                } else {
                    when (response.code()) {
                        400 -> {
                            Log.e("Error 400", "Bad connection")
                        }
                        404 -> {
                            Log.e("Error 400", "Resource not found")
                        }
                        else -> {
                            Log.e("Error", "Generic error")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("Error", t.message.toString())
                hideProgressDialog()
            }
        })
        return mWeatherList
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        val weatherList = mWeatherList!!
        for (i in weatherList.weather.indices) {
            Log.i("Weather Name", weatherList.weather.toString())
            mBinding.apply {
                tvMain.text = weatherList.weather[i].main
                tvMainDescription.text = weatherList.weather[i].description
                tvTemp.text = weatherList.main.temp.toString() + getUnit(weatherList.sys.country)
                // tvCountry.text = weatherList.sys.country +", ${weatherList.name}"

                tvHumidity.text = weatherList.main.humidity.toString() + " per cent"
                tvMin.text = weatherList.main.temp_min.toString() + " min"
                tvMax.text = weatherList.main.temp_max.toString() + " max"
                tvSpeed.text = weatherList.wind.speed.toString()
                tvCountry.text = getCountryName(weatherList.sys.country)
                tvName.text = weatherList.name
                tvSunriseTime.text = unixTime(weatherList.sys.sunrise.toLong())
                tvSunsetTime.text = unixTime(weatherList.sys.sunset.toLong())

                // Here we update the main icon
                when (weatherList.weather[i].icon) {
                    "01d" -> ivMain.setImageResource(R.drawable.sunny)
                    "02d" -> ivMain.setImageResource(R.drawable.cloud)
                    "03d" -> ivMain.setImageResource(R.drawable.cloud)
                    "04d" -> ivMain.setImageResource(R.drawable.cloud)
                    "04n" -> ivMain.setImageResource(R.drawable.cloud)
                    "10d" -> ivMain.setImageResource(R.drawable.rain)
                    "11d" -> ivMain.setImageResource(R.drawable.storm)
                    "13d" -> ivMain.setImageResource(R.drawable.snowflake)
                    "01n" -> ivMain.setImageResource(R.drawable.cloud)
                    "02n" -> ivMain.setImageResource(R.drawable.cloud)
                    "03n" -> ivMain.setImageResource(R.drawable.cloud)
                    "10n" -> ivMain.setImageResource(R.drawable.cloud)
                    "11n" -> ivMain.setImageResource(R.drawable.rain)
                    "13n" -> ivMain.setImageResource(R.drawable.snowflake)
                }

            }
        }
    }

    private fun getUnit(value: String): String {
        if ( US_SYMBOL == value || LR_SYMBOL == value || MM_SYMBOL == value) {
            return FAHRENHEIT_SYMBOL
        }
        return CELSIUS_SYMBOL

    }

    private fun unixTime(timex: Long): String? {
        val date = Date(timex * DATE_ALLOCATION)
        @SuppressLint("SimpleDateFormat") val sdf =
            SimpleDateFormat("HH:mm:ss")
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }


    private fun getCountryName(abbr:String):String{
        when(abbr){
            "AF"->return "Afghanistan"
            "AX"->return "Aland Islands"
            "AL"->return "Albania"
            "DZ"->return "Algeria"
            "AS"->return "American Samoa"
            "AD"->return "Andorra"
            "AO"->return "Angola"
            "AI"->return "Anguilla"
            "AQ"->return "Antarctica"
            "AG"->return "Antigua and Barbuda"
            "AR"->return "Argentina"
            "AM"->return "Armenia"
            "AW"->return "Aruba"
            "AU"->return "Australia"
            "AT"->return "Austria"
            "AZ"->return "Azerbaijan"
            "BS"->return "Bahamas"
            "BH"->return "Bahrain"
            "BD"->return "Bangladesh"
            "BB"->return "Barbados"
            "BY"->return "Belarus"
            "BE"->return "Belgium"
            "BZ"->return "Belize"
            "BJ"->return "Benin"
            "BM"->return "Bermuda"
            "BT"->return "Bhutan"
            "BO"->return "Bolivia"
            "BQ"->return "Bonaire, Saint Eustatius and Saba"
            "BA"->return "Bosnia and Herzegovina"
            "BW"->return "Botswana"
            "BV"->return "Bouvet Island"
            "BR"->return "Brazil"
            "IO"->return "British Indian Ocean Territory"
            "VG"->return "British Virgin Islands"
            "BN"->return "Brunei"
            "BG"->return "Bulgaria"
            "BF"->return "Burkina Faso"
            "BI"->return "Burundi"
            "KH"->return "Cambodia"
            "CM"->return "Cameroon"
            "CA"->return "Canada"
            "CV"->return "Cape Verde"
            "KY"->return "Cayman Islands"
            "CF"->return "Central African Republic"
            "TD"->return "Chad"
            "CL"->return "Chile"
            "CN"->return "China"
            "CX"->return "Christmas Island"
            "CC"->return "Cocos Islands"
            "CO"->return "Colombia"
            "KM"->return "Comoros"
            "CK"->return "Cook Islands"
            "CR"->return "Costa Rica"
            "HR"->return "Croatia"
            "CU"->return "Cuba"
            "CW"->return "Curacao"
            "CY"->return "Cyprus"
            "CZ"->return "Czech Republic"
            "CD"->return "Democratic Republic of the Congo"
            "DK"->return "Denmark"
            "DJ"->return "Djibouti"
            "DM"->return "Dominica"
            "DO"->return "Dominican Republic"
            "TL"->return "East Timor"
            "EC"->return "Ecuador"
            "EG"->return "Egypt"
            "SV"->return "El Salvador"
            "GQ"->return "Equatorial Guinea"
            "ER"->return "Eritrea"
            "EE"->return "Estonia"
            "ET"->return "Ethiopia"
            "FK"->return "Falkland Islands"
            "FO"->return "Faroe Islands"
            "FJ"->return "Fiji"
            "FI"->return "Finland"
            "FR"->return "France"
            "GF"->return "French Guiana"
            "PF"->return "French Polynesia"
            "TF"->return "French Southern Territories"
            "GA"->return "Gabon"
            "GM"->return "Gambia"
            "GE"->return "Georgia"
            "DE"->return "Germany"
            "GH"->return "Ghana"
            "GI"->return "Gibraltar"
            "GR"->return "Greece"
            "GL"->return "Greenland"
            "GD"->return "Grenada"
            "GP"->return "Guadeloupe"
            "GU"->return "Guam"
            "GT"->return "Guatemala"
            "GG"->return "Guernsey"
            "GN"->return "Guinea"
            "GW"->return "Guinea-Bissau"
            "GY"->return "Guyana"
            "HT"->return "Haiti"
            "HM"->return "Heard Island and McDonald Islands"
            "HN"->return "Honduras"
            "HK"->return "Hong Kong"
            "HU"->return "Hungary"
            "IS"->return "Iceland"
            "IN"->return "India"
            "ID"->return "Indonesia"
            "IR"->return "Iran"
            "IQ"->return "Iraq"
            "IE"->return "Ireland"
            "IM"->return "Isle of Man"
            "IL"->return "Israel"
            "IT"->return "Italy"
            "CI"->return "Ivory Coast"
            "JM"->return "Jamaica"
            "JP"->return "Japan"
            "JE"->return "Jersey"
            "JO"->return "Jordan"
            "KZ"->return "Kazakhstan"
            "KE"->return "Kenya"
            "KI"->return "Kiribati"
            "XK"->return "Kosovo"
            "KW"->return "Kuwait"
            "KG"->return "Kyrgyzstan"
            "LA"->return "Laos"
            "LV"->return "Latvia"
            "LB"->return "Lebanon"
            "LS"->return "Lesotho"
            "LR"->return "Liberia"
            "LY"->return "Libya"
            "LI"->return "Liechtenstein"
            "LT"->return "Lithuania"
            "LU"->return "Luxembourg"
            "MO"->return "Macao"
            "MK"->return "Macedonia"
            "MG"->return "Madagascar"
            "MW"->return "Malawi"
            "MY"->return "Malaysia"
            "MV"->return "Maldives"
            "ML"->return "Mali"
            "MT"->return "Malta"
            "MH"->return "Marshall Islands"
            "MQ"->return "Martinique"
            "MR"->return "Mauritania"
            "MU"->return "Mauritius"
            "YT"->return "Mayotte"
            "MX"->return "Mexico"
            "FM"->return "Micronesia"
            "MD"->return "Moldova"
            "MC"->return "Monaco"
            "MN"->return "Mongolia"
            "ME"->return "Montenegro"
            "MS"->return "Montserrat"
            "MA"->return "Morocco"
            "MZ"->return "Mozambique"
            "MM"->return "Myanmar"
            "NA"->return "Namibia"
            "NR"->return "Nauru"
            "NP"->return "Nepal"
            "NL"->return "Netherlands"
            "AN"->return "Netherlands Antilles"
            "NC"->return "New Caledonia"
            "NZ"->return "New Zealand"
            "NI"->return "Nicaragua"
            "NE"->return "Niger"
            "NG"->return "Nigeria"
            "NU"->return "Niue"
            "NF"->return "Norfolk Island"
            "KP"->return "North Korea"
            "MP"->return "Northern Mariana Islands"
            "NO"->return "Norway"
            "OM"->return "Oman"
            "PK"->return "Pakistan"
            "PW"->return "Palau"
            "PS"->return "Palestinian Territory"
            "PA"->return "Panama"
            "PG"->return "Papua New Guinea"
            "PY"->return "Paraguay"
            "PE"->return "Peru"
            "PH"->return "Philippines"
            "PN"->return "Pitcairn"
            "PL"->return "Poland"
            "PT"->return "Portugal"
            "PR"->return "Puerto Rico"
            "QA"->return "Qatar"
            "CG"->return "Republic of the Congo"
            "RE"->return "Reunion"
            "RO"->return "Romania"
            "RU"->return "Russia"
            "RW"->return "Rwanda"
            "BL"->return "Saint Barthelemy"
            "SH"->return "Saint Helena"
            "KN"->return "Saint Kitts and Nevis"
            "LC"->return "Saint Lucia"
            "MF"->return "Saint Martin"
            "PM"->return "Saint Pierre and Miquelon"
            "VC"->return "Saint Vincent and the Grenadines"
            "WS"->return "Samoa"
            "SM"->return "San Marino"
            "ST"->return "Sao Tome and Principe"
            "SA"->return "Saudi Arabia"
            "SN"->return "Senegal"
            "RS"->return "Serbia"
            "CS"->return "Serbia and Montenegro"
            "SC"->return "Seychelles"
            "SL"->return "Sierra Leone"
            "SG"->return "Singapore"
            "SX"->return "Sint Maarten"
            "SK"->return "Slovakia"
            "SI"->return "Slovenia"
            "SB"->return "Solomon Islands"
            "SO"->return "Somalia"
            "ZA"->return "South Africa"
            "GS"->return "South Georgia and the South Sandwich Islands"
            "KR"->return "South Korea"
            "SS"->return "South Sudan"
            "ES"->return "Spain"
            "LK"->return "Sri Lanka"
            "SD"->return "Sudan"
            "SR"->return "Suriname"
            "SJ"->return "Svalbard and Jan Mayen"
            "SZ"->return "Swaziland"
            "SE"->return "Sweden"
            "CH"->return "Switzerland"
            "SY"->return "Syria"
            "TW"->return "Taiwan"
            "TJ"->return "Tajikistan"
            "TZ"->return "Tanzania"
            "TH"->return "Thailand"
            "TG"->return "Togo"
            "TK"->return "Tokelau"
            "TO"->return "Tonga"
            "TT"->return "Trinidad and Tobago"
            "TN"->return "Tunisia"
            "TR"->return "Turkey"
            "TM"->return "Turkmenistan"
            "TC"->return "Turks and Caicos Islands"
            "TV"->return "Tuvalu"
            "VI"->return "U.S. Virgin Islands"
            "UG"->return "Uganda"
            "UA"->return "Ukraine"
            "AE"->return "United Arab Emirates"
            "GB"->return "United Kingdom"
            "US"->return "United States"
            "UM"->return "United States Minor Outlying Islands"
            "UY"->return "Uruguay"
            "UZ"->return "Uzbekistan"
            "VU"->return "Vanuatu"
            "VA"->return "Vatican"
            "VE"->return "Venezuela"
            "VN"->return "Vietnam"
            "WF"->return "Wallis and Futuna"
            "EH"->return "Western Sahara"
            "YE"->return "Yemen"
            "ZM"->return "Zambia"
            "ZW"->return "Zimbabwe"
        }
        return "default"
    }
}