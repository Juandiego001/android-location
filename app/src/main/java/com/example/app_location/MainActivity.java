package com.example.app_location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    EditText editTextLatitud;
    EditText editTextLongitud;
    TextView textViewAddress;
    LocationManager mLocManager;
    MyLocationListener mLocListener;
    int PERMISO_FINE = 0;
    int PERMISO_COARSE = 0;
    int providerFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextLatitud = (EditText) findViewById(R.id.editTextLatitud);
        editTextLongitud = (EditText) findViewById(R.id.editTextLongitud);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocListener = new MyLocationListener();
        mLocListener.setMainActivity(this);

        askCurrentLocationAndUpdates();
    }

    public void askCurrentLocationAndUpdates() {
        try {
            providerFound = 0;
            int PERMISO_FINE = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int PERMISO_COARSE = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (PERMISO_FINE != PackageManager.PERMISSION_GRANTED || PERMISO_COARSE != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            } else {
                mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                        (LocationListener) mLocListener);

                final FutureTask<Boolean> ft = new FutureTask<Boolean>(() -> {}, true);

                CancellationSignal cs = new CancellationSignal();

                Executor ex = new Executor() {
                    @Override
                    public void execute(Runnable runnable) {
                        Log.d("Callback", "Callback ejecutado");
                        runnable.run();
                    }
                };

                Consumer c = new Consumer() {
                    @Override
                    public void accept(Object o) {
                        try {
                            if (o != null) {
                                providerFound++;
                                Location loc = (Location) o;
                                setCurrentLocation(loc);
                            } else {
                                Log.d("Callback consumer", "Null");
                            }
                            ft.run();
                        } catch (Exception e){
                            Log.d("Error on customer", e.toString());
                        }
                    }
                };


                for (String theProvider : mLocManager.getAllProviders()) {
                    Log.d("Provider", theProvider);
                    mLocManager.getCurrentLocation(theProvider, cs, ex, c);
                    ft.get();
                    Log.d("providerFound", Integer.toString(providerFound));
                    if (providerFound > 0) break;
                }
            }
        } catch (Exception e) {
            Log.d("Error.", e.toString());
        }
    }

    public void setLocation(double latitude, double longitude) {
        try {
            Log.d("Latitude", Double.toString(latitude));
            Log.d("Longitude", Double.toString(longitude));
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> direcciones = geocoder.getFromLocation(latitude, longitude, 1);
            Log.d("Direcciones", direcciones.toString());

            if (!direcciones.isEmpty()) {
                Address direccion = direcciones.get(0);
                String theDireccion = direccion.getAddressLine(0);
                Log.d("Direccion", theDireccion);
                textViewAddress.setText("La dirección es " +
                        direccion.getAddressLine(0));
            } else {
                Log.d("Direcciones", "Esta vacio");
                Log.d("Direcciones", Boolean.toString(direcciones.isEmpty()));
            }
        } catch (Exception e) {
            Log.d("Error en el método setLocation", e.toString());
        }
    }

    public void setCurrentLocation(Location loc) {
        double theLatitud = loc.getLatitude();
        double theLongitude = loc.getLongitude();

        try {
            if (theLatitud != 0 && theLongitude != 0) {
                setLocation(theLatitud, theLongitude);
            } else {
                Log.d("Latitud esta vacio o longitud esta vacio", Double.toString(theLatitud));
                Log.d("Latitud esta vacio o longitud esta vacio", Double.toString(theLongitude));
            }
        } catch (Exception e) {
            Log.d("Error on setLocation", e.toString());
        }
    }

    public void obtenerDireccion(View v) {
        Log.d("Impresion", "Se presionó el botón");
        double theLatitude = Double.parseDouble(editTextLatitud.getText().toString());
        double theLongitude = Double.parseDouble(editTextLongitud.getText().toString());
        setLocation(theLatitude, theLongitude);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    askCurrentLocationAndUpdates();
                } else {
                    Toast.makeText(this, "Sin los permisos no se podrá mostrar" +
                            " su localización actual", Toast.LENGTH_LONG).show();
                }
                break;

            default:
                Toast.makeText(this, "Sin los permisos no se podrá mostrar" +
                        " su localización actual", Toast.LENGTH_LONG).show();
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}