package com.example.lastlocation

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle

import com.example.lastlocation.maputil.LngLat
import com.example.lastlocation.maputil.MapUtil
import com.example.lastlocation.maputil.TransUtil
import kotlinx.android.synthetic.main.layout_map.*

class MapDialog(context: Context?, var location: Location) : AlertDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_map)
        btn_gaode.setOnClickListener {
            dismiss()
            MapUtil.getIntance(context).myLocation2WhereByGaode(location.latitude!!.toFloat(),location.longitude!!.toFloat(),"上次位置")
        }
        btn_tenxun.setOnClickListener {
            dismiss()
            MapUtil.getIntance(context).myLocation2WhereByTengxun(location.latitude!!.toFloat(),location.longitude!!.toFloat(),"上次位置")
        }
        btn_baidu.setOnClickListener {
            dismiss()
            val lngLat = TransUtil.gaodeorTentxun2Baidu(LngLat(location.longitude!!,
                location.latitude!!
            ))
            MapUtil.getIntance(context).myLocation2WhereByBaidu(lngLat.lantitude.toFloat(),lngLat.longitude.toFloat(),"上次位置")
        }
    }
}