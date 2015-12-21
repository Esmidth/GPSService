package com.example.esmidth.gpsservice;

/**
 * Created by Great Great Esmidth on 2015/10/7.
 * Stay Foolish
 */
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class GpsService extends Service {
    ArrayList<CellInfo> cellIds = null;
    private Gps gps=null;
    private boolean threadDisable=false;
    private final static String TAG=GpsService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        gps=new Gps(GpsService.this);
        cellIds=UtilTool.init(GpsService.this);

        new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (!threadDisable) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(gps!=null){
                        Location location=gps.getLocation();
                        if(location==null){
                            Log.v(TAG, "gps location null");
                            try {
                                location = UtilTool.callGear(GpsService.this, cellIds);
                            } catch (Exception e) {
                                location=null;
                                e.printStackTrace();
                            }
                            if(location==null){
                                Log.v(TAG, "cell location null");
                            }
                        }

                        Intent intent=new Intent();
                        intent.putExtra("lat", location==null?"":location.getLatitude()+"");
                        intent.putExtra("lon", location==null?"":location.getLongitude()+"");
                        intent.setAction("com.ljq.activity.GpsService");
                        sendBroadcast(intent);
                    }

                }
            }
        }).start();

    }

    @Override
    public void onDestroy() {
        threadDisable=true;
        if(cellIds!=null&&cellIds.size()>0){
            cellIds=null;
        }
        if(gps!=null){
            gps.closeLocation();
            gps=null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


}
