package com.example.lastlocation

import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps2d.LocationSource
import kotlinx.android.synthetic.main.activity_main.*
import com.amap.api.maps2d.model.MyLocationStyle
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationListener





class MainActivity : AppCompatActivity(), AMapLocationListener {
    override fun onLocationChanged(amapLocation: AMapLocation?) {
        if (listener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() === 0) {
                listener?.onLocationChanged(amapLocation)// 显示系统小蓝点
            } else {
                val errText =
                    "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo()
                Log.e("AmapErr", errText)
            }
        }
    }


    var myLocationStyle: MyLocationStyle? = null

    var mLocationOption: AMapLocationClientOption? = null
    var listener: LocationSource.OnLocationChangedListener?=null

    var  mlocationClient:AMapLocationClient?=null

    val locationSource: LocationSource = object : LocationSource {
        override fun deactivate() {
            listener = null
            if (mlocationClient != null) {
                mlocationClient?.stopLocation()
                mlocationClient?.onDestroy()
            }
            mlocationClient = null
        }

        override fun activate(p0: LocationSource.OnLocationChangedListener?) {
            listener = p0
            if (listener == null) {
                //初始化定位
                mlocationClient = AMapLocationClient(this@MainActivity)
                //初始化定位参数
                mLocationOption = AMapLocationClientOption()
                //设置定位回调监听
                mlocationClient?.setLocationListener(this@MainActivity )
                //设置为高精度定位模式
                mLocationOption?.setLocationMode(AMapLocationMode.Hight_Accuracy)
                //设置定位参数
                mlocationClient?.setLocationOption(mLocationOption)
                // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
                // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
                // 在定位结束后，在合适的生命周期调用onDestroy()方法
                // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
                mlocationClient?.startLocation()//启动定位
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        map_view.onCreate(savedInstanceState)
        myLocationStyle = MyLocationStyle()
        //myLocationStyle?.interval(2000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle?.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW)//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
        myLocationStyle?.strokeColor(Color.GREEN)
        myLocationStyle?.strokeWidth(5f)

        map_view.map.setLocationSource(locationSource)

    }

    override fun onResume() {
        super.onResume()
        map_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        map_view.onDestroy()
        if(null != mlocationClient){
            mlocationClient?.onDestroy()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_view.onSaveInstanceState(outState)
    }
}
