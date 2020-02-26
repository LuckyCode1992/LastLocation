package com.example.lastlocation

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.alibaba.fastjson.JSON

import kotlinx.android.synthetic.main.activity_main.*


import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationListener

import com.amap.api.maps2d.CameraUpdateFactory

import android.graphics.BitmapFactory
import android.text.TextUtils
import android.widget.Toast
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.model.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    var locationSp by PropertyBySharedPref<String>(default = "")

    lateinit var location: Location
    lateinit var lastLocation:Location
    val loacationListener = AMapLocationListener { amapLocation ->

        if (amapLocation != null && amapLocation.errorCode === 0) {
            location = JSON.parseObject(JSON.toJSONString(amapLocation), Location::class.java)
            Log.d("location_", "location:$location")
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
            location.time = format
            Log.d("location_", "format$format")
        }

    }


    var mlocationClient: AMapLocationClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        map_view.onCreate(savedInstanceState)
        val myLocationStyle = MyLocationStyle()

        myLocationStyle?.interval(20000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle?.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW)
        map_view.map.setMyLocationStyle(myLocationStyle)
        map_view.map.getUiSettings().setMyLocationButtonEnabled(true)//设置默认定位按钮是否显示，非必需设置。
        map_view.map.setMyLocationEnabled(true)// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        val uiSetting = map_view.map.uiSettings
        uiSetting.isCompassEnabled = true//指南针
        uiSetting.isZoomControlsEnabled = true// 缩放
        uiSetting.setScaleControlsEnabled(true)//比例尺

        map_view.map.moveCamera(CameraUpdateFactory.zoomTo(17f))






        btn_set.setOnClickListener {
            val intent = Intent()
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null))
            startActivity(intent)
        }
        btn_addmark.setOnClickListener {
            addMark2tag()
        }
        btn_record.setOnClickListener {
            recordMark()
        }
        btn_go.setOnClickListener {
            if (TextUtils.isEmpty(locationSp)){
                Toast.makeText(this,"没有上次位置，不能导航",Toast.LENGTH_LONG).show()
            }else{
                MapDialog(this,lastLocation).show()
            }
        }


    }

    fun addMark2tag() {
        clearMark()
        val markerOption = MarkerOptions()
        markerOption.position(
            LatLng(
                location.latitude ?: 0.0 + 0.002,
                location.longitude ?: 0.0 + 0.002
            )
        )
//        markerOption.title(location.time).snippet(location.address)
        markerOption.draggable(true)//设置Marker是否可拖动
        markerOption.icon(
            BitmapDescriptorFactory.fromBitmap(
                BitmapFactory
                    .decodeResource(resources, R.drawable.mark_current)
            )
        )
        map_view.map.setOnMarkerDragListener(object : AMap.OnMarkerDragListener {
            override fun onMarkerDragStart(p0: Marker?) {

            }

            override fun onMarkerDrag(p0: Marker?) {

            }

            override fun onMarkerDragEnd(marker: Marker?) {
                Log.d("mark_","改变后lat:"+marker?.position?.latitude)
                updateLocation(marker)

            }

        })
        val marker = map_view.map.addMarker(markerOption)
        Log.d("mark_","改变前lat:"+marker?.position?.latitude)
        updateLocation(marker)


    }


    fun recordMark() {
        clearMark()
        Log.d("mark_","recordMark lat:"+lastLocation?.latitude)
        val markerOption = MarkerOptions()
        markerOption.position(
            LatLng(
                lastLocation?.latitude ?: 0.0,
                lastLocation?.longitude ?: 0.0
            )

        )
        markerOption.draggable(true)//设置Marker是否可拖动
        markerOption.icon(
            BitmapDescriptorFactory.fromBitmap(
                BitmapFactory
                    .decodeResource(resources, R.drawable.mark_last)
            )
        )
        map_view.map.addMarker(markerOption)
        //保存当前location
        locationSp = JSON.toJSONString(lastLocation)

    }
    fun addMarkerForLast(location: Location){
        clearMark()
        val markerOption = MarkerOptions()
        markerOption.position(
            LatLng(
                location.latitude ?: 0.0,
                location.longitude ?: 0.0
            )

        )
        markerOption.draggable(true)//设置Marker是否可拖动
        markerOption.icon(
            BitmapDescriptorFactory.fromBitmap(
                BitmapFactory
                    .decodeResource(resources, R.drawable.mark_last)
            )
        )
       val marker =  map_view.map.addMarker(markerOption)
        map_view.map.setOnMapClickListener {
            MapDialog(this,lastLocation).show()
        }

    }

    private fun clearMark() {
        val mapScreenMarkers = map_view.map.mapScreenMarkers
        mapScreenMarkers.forEach {
            it?.remove()
        }
    }

    private fun updateLocation(marker: Marker?) {
        Log.d("mark_","updateLocation lat:"+marker?.position?.latitude)
        lastLocation.latitude = marker?.position?.latitude
        lastLocation.longitude = marker?.position?.longitude
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        lastLocation.time = format
        Log.d("mark_","updateLocation location: lat:"+marker?.position?.latitude)
    }

    override fun onResume() {
        super.onResume()
        map_view.onResume()
        mlocationClient = AMapLocationClient(this)
        mlocationClient?.setLocationListener(loacationListener)
        mlocationClient?.startLocation()

        if (!TextUtils.isEmpty(locationSp)&& locationSp.startsWith("{")||locationSp.startsWith("[")) {
            lastLocation = JSON.parseObject(locationSp, Location::class.java)
            addMarkerForLast(lastLocation)
        }
    }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
        mlocationClient?.unRegisterLocationListener(loacationListener)
        mlocationClient?.onDestroy()

    }

    override fun onDestroy() {
        super.onDestroy()
        map_view.onDestroy()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_view.onSaveInstanceState(outState)
    }
}
