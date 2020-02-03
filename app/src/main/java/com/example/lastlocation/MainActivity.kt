package com.example.lastlocation

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.amap.api.location.AMapLocationClientOption
import kotlinx.android.synthetic.main.activity_main.*


import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationListener

import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.MyLocationStyle


class MainActivity : AppCompatActivity() {

    val loacationListener = AMapLocationListener { amapLocation ->

        if (amapLocation != null && amapLocation.errorCode === 0) {

        } else {
            val errText =
                "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo()
            Log.e("AmapErr", errText)
        }

    }


    var mlocationClient: AMapLocationClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        map_view.onCreate(savedInstanceState)
        val myLocationStyle = MyLocationStyle()
        myLocationStyle?.interval(2000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle?.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW)
        myLocationStyle?.strokeWidth(5f)
        map_view.map.getUiSettings().setMyLocationButtonEnabled(true)//设置默认定位按钮是否显示，非必需设置。
        map_view.map.setMyLocationEnabled(true)// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        val uiSetting = map_view.map.uiSettings
        uiSetting.isCompassEnabled = true//指南针
        uiSetting.isZoomControlsEnabled = true// 缩放
        uiSetting.setScaleControlsEnabled(true)//比例尺

        map_view.map.moveCamera(CameraUpdateFactory.zoomTo(17f))

        mlocationClient = AMapLocationClient(this)
        mlocationClient?.setLocationListener(loacationListener)




        btn_set.setOnClickListener {
            val intent = Intent()
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivity(intent)
        }



    }

    override fun onResume() {
        super.onResume()
        map_view.onResume()
        mlocationClient?.startLocation()
    }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
        if (null != mlocationClient) {
            mlocationClient?.onDestroy()
        }
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
