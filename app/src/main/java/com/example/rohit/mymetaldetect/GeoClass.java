package com.example.rohit.mymetaldetect;

import android.hardware.GeomagneticField;

/**
 * Created by Rohit on 4/1/2016.
 */
public class GeoClass extends GeomagneticField {
    public GeoClass(float gdLatitudeDeg, float gdLongitudeDeg, float altitudeMeters, long timeMillis) {

        super(gdLatitudeDeg, gdLongitudeDeg, altitudeMeters, timeMillis);
    }

    @Override
    public float getFieldStrength() {
        return super.getFieldStrength();
    }

}
