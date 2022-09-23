package ca.bntec.itineraireplusplus

import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ca.bntec.itineraireplusplus.databinding.ActivityMapsBinding
import classes.map.MapData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var mapFragment: SupportMapFragment
    lateinit var progressDialog: ProgressDialog
    var origin = LatLng(45.5419056, -73.4924797)
    var dest = LatLng(25.8102247,-80.2101818)
    //var dest = LatLng(45.4779697, -75.5184535)
    lateinit var context: Context
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        context = this
        drawPolylines()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.addMarker(
            MarkerOptions()
                .position(origin)
                .title("LinkedIn")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
        googleMap.addMarker(
            MarkerOptions()
                .position(dest)
        )
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 15f))
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {

        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude

        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude

        // Sensor enabled
        val sensor = "sensor=false"
        val mode = "mode=driving"

        val app = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        val bundle = app.metaData


        var metaKey= bundle.getString("com.google.android.geo.API_KEY")

        val key ="key=$metaKey"
           // "key=AIzaSyBP4KpmtFwjJ4LkdJQzUWlVpeyH3V6cDnM"
        // Building the parameters to the web service
        val parameters =
            "$str_origin&$str_dest&$sensor&$mode&$key"
        //"$str_origin&$str_dest&$sensor&$mode&key=AIzaSyBP4KpmtFwjJ4LkdJQzUWlVpeyH3V6cDnM"

        // Output format
        val output = "json"

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

    private fun drawPolylines() {
        progressDialog = ProgressDialog(this@MapsActivity)
        progressDialog.setMessage("Please Wait, Polyline between two locations is building.")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Checks, whether start and end locations are captured
        // Getting URL to the Google Directions API
        val url: String = getDirectionsUrl(origin, dest)
        Log.d("url", url + "")
        //val downloadTask = DownloadTask()
        // Start downloading json data from Google Directions API
        //downloadTask.execute(url)
        getData(url)
    }

    private fun getData(url: String) {
        var data = ""
        var jObject: JSONObject
        var routes = ArrayList<ArrayList<HashMap<String, String>>>()

        MainScope().launch(Dispatchers.IO) {
            data = async { downloadUrl(url) }.await()
            jObject = JSONObject(data)
            val parser = MapData()
            routes = parser.parse(jObject)

            Log.d("result", routes.toString())
            var points = ArrayList<LatLng>()
            var lineOptions: PolylineOptions? = null

            for (i in routes.indices) {

                lineOptions = PolylineOptions()
                val path: List<java.util.HashMap<String, String>> = routes.get(i)
                for (j in path.indices) {
                    val point = path[j]
                    val lat = point["lat"]!!.toDouble()
                    val lng = point["lng"]!!.toDouble()
                    val position = LatLng(lat, lng)
                    points.add(position)
                }
                lineOptions.addAll(points)
                lineOptions.width(12f)
                lineOptions.color(Color.RED)
                lineOptions.geodesic(true)
            }

// Drawing polyline in the Google Map for the i-th route

// Drawing polyline in the Google Map for the i-th route
            this@MapsActivity.runOnUiThread(java.lang.Runnable {
                mMap.addPolyline(lineOptions!!)
            })
            progressDialog.dismiss()
        }
    }


    suspend fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection!!.connect()
            iStream = urlConnection!!.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
            Log.d("data", data)
        } catch (e: Exception) {
            Log.d("Exception", e.toString())
        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data!!
    }

}