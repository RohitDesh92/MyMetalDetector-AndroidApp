package com.example.rohit.mymetaldetect;

import android.graphics.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private Camera camera;
    private SensorManager sensorManager;
    private Sensor mySensor;
    private boolean sFlashlightOn;
    private long mLastUpdate;
    static final int UPDATE_THESHOLD=500;
    private TextView x_view, y_view,z_view, E_ut, O_ut,O_detect;
    static  float Earth_mag_strenght=0;

    //Graph
    private LineGraphSeries series;
    private LineGraphSeries seriesLight; //Light Series
    private static Random RANDOM = new Random();
    private int lastX=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x_view = (TextView) findViewById(R.id.Xaxis);
        y_view = (TextView) findViewById(R.id.Yaxis);
        z_view = (TextView) findViewById(R.id.Zaxis);
        E_ut = (TextView) findViewById(R.id.Earth_ut);
        O_ut =(TextView) findViewById(R.id.object_ut);
        O_detect = (TextView) findViewById(R.id.Object);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (null == (mySensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD))) {
            finish();
        }
       //Graph
        GraphView graph= (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        seriesLight = new LineGraphSeries<DataPoint>(); //Light Series
        graph.addSeries(seriesLight);                   //Light Series

        Viewport viewport =graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(500);
        viewport.setScalable(true);


    }

    protected void onResume()
    {
        float x =0,y=0,z=0;
        long w=0;
        super.onResume();
        sensorManager.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_UI);
        mLastUpdate =System.currentTimeMillis();
        GeoClass r1 = new GeoClass(x,y,z,w);
        Earth_mag_strenght= r1.getFieldStrength();

    }
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            long actualTime = System.currentTimeMillis();
            if(actualTime - mLastUpdate > UPDATE_THESHOLD) {
                mLastUpdate = actualTime;
                float x = event.values[0], y = event.values[1], z = event.values[2];
                //Logic For Manipulation

                x_view.setText(String.valueOf(x));
                y_view.setText(String.valueOf(y));
                z_view.setText(String.valueOf(z));


                int M_strenght = Math.round(Earth_mag_strenght);
                M_strenght = M_strenght % 1000;
                E_ut.setText(String.valueOf(M_strenght));
                O_ut.setText(String.valueOf(Math.round(Math.sqrt(x * x + y * y + z * z))));
                final float O_Strenght = Math.round(Math.sqrt(x * x + y * y + z * z));
                //O_Strenght > 1.4*M_strenght || O_Strenght>0.6*M_strenght
                if (O_Strenght > 50) {
                    O_detect.setText("Metal Detected");
                } else {
                    O_detect.setText("NO Metal Around");
                }



                //Graph Code Start
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for(int i=0;i<100;i++)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    series.appendData(new DataPoint(lastX++, O_Strenght), true, 10);
                                    //Light Series
                                    seriesLight.appendData(new DataPoint(lastX++,O_Strenght*2),true,10);

                                }
                            });
                            try {
                                Thread.sleep(1200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                //Graph Code End
            }


    }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
