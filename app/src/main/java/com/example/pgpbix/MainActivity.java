package com.example.pgpbix;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private LocationComponent locationComponent;
    private boolean isLocationTracking = false;
    private FloatingActionButton locationButton;
    private ImageView ivDirection;
    private static final int PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init maplibre
        Mapbox.getInstance(this);
        setContentView(R.layout.activity_main);

        // init mapview
        mapView = findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(mapboxMap -> {
            // Set the map style to use TMS tiles
            String tmsUrl = "https://tile.openstreetmap.org/{z}/{x}/{y}.png";

            String styleJson = "{\n" +
                    "  \"version\": 8,\n" +
                    "  \"sources\": {\n" +
                    "    \"tms-tiles\": {\n" +
                    "      \"type\": \"raster\",\n" +
                    "      \"tiles\": [\"" + tmsUrl + "\"],\n" +
                    "      \"tileSize\": 256\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"layers\": [\n" +
                    "    {\n" +
                    "      \"id\": \"tms-tiles\",\n" +
                    "      \"type\": \"raster\",\n" +
                    "      \"source\": \"tms-tiles\",\n" +
                    "      \"minzoom\": 0,\n" +
                    "      \"maxzoom\": 22\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
            mapboxMap.setStyle(new Style.Builder().fromJson(styleJson), style -> {

                // Tambahkan ikon untuk kedua marker
                style.addImage("marker-icon-id", BitmapFactory.decodeResource(getResources(), R.drawable.marker_icon));
                style.addImage("marker-icon-2", BitmapFactory.decodeResource(getResources(), R.drawable.marker_icon_2));
                style.addImage("marker-icon-3", BitmapFactory.decodeResource(getResources(), R.drawable.marker_icon_3));

                // Lokasi pertama
                LatLng location1 = new LatLng(-7.542011834740503, 110.44544683618048);
                Feature feature1 = Feature.fromGeometry(Point.fromLngLat(location1.getLongitude(), location1.getLatitude()));
                feature1.addStringProperty("id", "marker1");

                // Lokasi kedua
                LatLng location2 = new LatLng(-8.023234326384785, 110.32533015931338);
                Feature feature2 = Feature.fromGeometry(Point.fromLngLat(location2.getLongitude(), location2.getLatitude()));
                feature2.addStringProperty("id", "marker2");

                // Lokasi ketiga
                LatLng location3 = new LatLng(-7.752014991808766, 110.491527503816);
                Feature feature3 = Feature.fromGeometry(Point.fromLngLat(location3.getLongitude(), location3.getLatitude()));
                feature3.addStringProperty("id", "marker3");

                // Buat koleksi fitur yang mencakup kedua lokasi
                List<Feature> features = new ArrayList<>();
                features.add(feature1);
                features.add(feature2);
                features.add(feature3);

                FeatureCollection featureCollection = FeatureCollection.fromFeatures(features);
                GeoJsonSource geoJsonSource = new GeoJsonSource("marker-source", featureCollection);
                style.addSource(geoJsonSource);

                // Tambahkan SymbolLayer untuk marker pertama dengan filter berdasarkan ID
                SymbolLayer symbolLayer1 = new SymbolLayer("marker-layer-1", "marker-source")
                        .withProperties(
                                PropertyFactory.iconImage("marker-icon-id"),
                                PropertyFactory.iconAllowOverlap(true),
                                PropertyFactory.iconIgnorePlacement(true),
                                PropertyFactory.iconSize(0.09f)
                        )
                        .withFilter(eq(get("id"), "marker1"));
                style.addLayer(symbolLayer1);

                // Tambahkan SymbolLayer untuk marker kedua dengan filter berdasarkan ID
                SymbolLayer symbolLayer2 = new SymbolLayer("marker-layer-2", "marker-source")
                        .withProperties(
                                PropertyFactory.iconImage("marker-icon-2"),
                                PropertyFactory.iconAllowOverlap(true),
                                PropertyFactory.iconIgnorePlacement(true),
                                PropertyFactory.iconSize(0.14f)
                        )
                        .withFilter(eq(get("id"), "marker2"));
                style.addLayer(symbolLayer2);

                // Tambahkan SymbolLayer untuk marker ketiga dengan filter berdasarkan ID
                SymbolLayer symbolLayer3 = new SymbolLayer("marker-layer-3", "marker-source")
                        .withProperties(
                                PropertyFactory.iconImage("marker-icon-3"),
                                PropertyFactory.iconAllowOverlap(true),
                                PropertyFactory.iconIgnorePlacement(true),
                                PropertyFactory.iconSize(0.14f)
                        )
                        .withFilter(eq(get("id"), "marker3"));
                style.addLayer(symbolLayer3);

                // Set kamera
                mapboxMap.setCameraPosition(new CameraPosition.Builder().target(location1).zoom(10.0).build());

                // Zoom In dan Zoom Out
                FloatingActionButton zoomInButton = findViewById(R.id.btn_zoom_in);
                FloatingActionButton zoomOutButton = findViewById(R.id.btn_zoom_out);

                zoomInButton.setOnClickListener(v -> {
                    CameraPosition position = new CameraPosition.Builder()
                            .target(mapboxMap.getCameraPosition().target)
                            .zoom(mapboxMap.getCameraPosition().zoom + 1)
                            .build();
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 500);
                });

                zoomOutButton.setOnClickListener(v -> {
                    CameraPosition position = new CameraPosition.Builder()
                            .target(mapboxMap.getCameraPosition().target)
                            .zoom(mapboxMap.getCameraPosition().zoom - 1)
                            .build();
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 500);
                });

                // Enable location component and add location puck
                checkLocationPermission(style, mapboxMap);

                // Tombol lokasi
                locationButton = findViewById(R.id.btn_location);
                locationButton.setOnClickListener(v -> {
                    if (locationComponent != null && locationComponent.getLastKnownLocation() != null) {
                        isLocationTracking = !isLocationTracking;
                        if (isLocationTracking) {
                            // Get Location
                            LatLng userLocation = new LatLng(
                                    locationComponent.getLastKnownLocation().getLatitude(),
                                    locationComponent.getLastKnownLocation().getLongitude());

                            CameraPosition position = new CameraPosition.Builder()
                                    .target(userLocation)
                                    .zoom(13)
                                    .build();

                            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000);

                            // Mengubah ikon tombol lokasi
                            locationButton.setImageResource(R.drawable.location_on);
                        } else {
                            locationComponent.setCameraMode(CameraMode.NONE);
                            locationButton.setImageResource(R.drawable.location_off);
                        }
                    } else {
                        Toast.makeText(this, "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }

    // Mengecek izin lokasi
    private void checkLocationPermission(Style style, MapboxMap mapboxMap) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableLocationComponent(style, mapboxMap);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        }
    }

    // Aktifkan LocationComponent jika izin sudah diberikan
    private void enableLocationComponent(Style style, MapboxMap mapboxMap) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            if (mapboxMap != null) {
                locationComponent = mapboxMap.getLocationComponent();
                LocationComponentActivationOptions locationComponentActivationOptions =
                        LocationComponentActivationOptions.builder(this, style).build();

                locationComponent.activateLocationComponent(locationComponentActivationOptions);
                locationComponent.setLocationComponentEnabled(true);
                locationComponent.setCameraMode(CameraMode.TRACKING);
                locationComponent.setRenderMode(RenderMode.COMPASS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mapView.getMapAsync(mapboxMap -> mapboxMap.getStyle(this::enableLocationComponent));
            } else {
                Toast.makeText(this, "Izin lokasi ditolak.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableLocationComponent(Style style) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}