package com.example.esmidth.gpsservice;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class GpsActivity extends Activity {
    private Double homeLat=26.0673834d;
    private Double homeLon=119.3119936d;
    private EditText editText = null;
    private MyReceiver receiver=null;
    private final static String TAG=GpsActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText=(EditText)findViewById(R.id.editText);

        Log.i(TAG, UtilTool.isGpsEnabled((LocationManager)getSystemService(Context.LOCATION_SERVICE))+"");
        if(!UtilTool.isGpsEnabled((LocationManager)getSystemService(Context.LOCATION_SERVICE))){
            Toast.makeText(this, "GSP当前无法使用", Toast.LENGTH_LONG).show();
            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(callGPSSettingIntent);
            return;
        }

        startService(new Intent(this, GpsService.class));

        receiver=new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.ljq.activity.GpsService");
        registerReceiver(receiver, filter);
    }

    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras();
            String lon=bundle.getString("lon");
            String lat=bundle.getString("lat");
            if(lon!=null&&!"".equals(lon)&&lat!=null&&!"".equals(lat)){
                double distance=getDistance(Double.parseDouble(lat),
                        Double.parseDouble(lon), homeLat, homeLon);
                editText.setText("目前经纬度\n经度："+lon+"\n纬度："+lat+"\n目标纬度："+homeLat.toString()+"\n目标经度："+homeLon.toString()+"\n与目标距离："+java.lang.Math.abs(distance));
            }else{
                editText.setText("目前经纬度\n经度："+lon+"\n纬度："+lat);
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        stopService(new Intent(this, GpsService.class));
        super.onDestroy();
    }

    /**
     * 把经纬度换算成距离
     *
     * @param lat1 开始纬度
     * @param lon1 开始经度
     * @param lat2 结束纬度
     * @param lon2 结束经度
     * @return
     */
    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }
}