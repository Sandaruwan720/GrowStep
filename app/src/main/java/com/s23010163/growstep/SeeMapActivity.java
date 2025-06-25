package com.s23010163.growstep;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SeeMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private static final int LOCATION_REQUEST_CODE = 100;
    private Marker startMarker, endMarker;
    private Polyline routeLine;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private LinearLayout infoPanel;
    private TextView tvDistance, tvSpeed, tvNavigationStatus;
    private TextToSpeech tts;
    private int tapCount = 0;
    private LatLng startLatLng, endLatLng;
    private boolean isNavigating = false;
    
    // Google Directions API key - Replace with your actual API key
    private static final String DIRECTIONS_API_KEY = "AIzaSyDYBJNepivTh_el0hCPhoEk8637bdBHhS8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_map);

        // Enhanced floating info panel for distance, speed, and navigation status
        infoPanel = new LinearLayout(this);
        infoPanel.setOrientation(LinearLayout.VERTICAL);
        infoPanel.setBackgroundColor(0xE6FFFFFF); // More opaque white background
        infoPanel.setPadding(32, 32, 32, 32);
        
        tvNavigationStatus = new TextView(this);
        tvNavigationStatus.setTextSize(18f);
        tvNavigationStatus.setTextColor(0xFF1976D2); // Blue color
        tvNavigationStatus.setTypeface(null, android.graphics.Typeface.BOLD);
        
        tvDistance = new TextView(this);
        tvSpeed = new TextView(this);
        tvDistance.setTextSize(16f);
        tvSpeed.setTextSize(16f);
        tvDistance.setTextColor(0xFF333333);
        tvSpeed.setTextColor(0xFF333333);
        
        infoPanel.addView(tvNavigationStatus);
        infoPanel.addView(tvDistance);
        infoPanel.addView(tvSpeed);
        
        addContentView(infoPanel, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        infoPanel.setY(200); // Place below search bar
        infoPanel.setX(0);
        infoPanel.setZ(10);
        infoPanel.setAlpha(0.98f);
        infoPanel.setVisibility(LinearLayout.GONE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Enhanced TextToSpeech initialization
        tts = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.getDefault());
                tts.setSpeechRate(0.9f); // Slightly slower for better clarity
                tts.setPitch(1.0f);
            }
        });

        // Set up clear route button
        findViewById(R.id.clear_route_button).setOnClickListener(v -> {
            clearRoute();
            findViewById(R.id.clear_route_button).setVisibility(android.view.View.GONE);
            tts.speak("Route cleared", TextToSpeech.QUEUE_FLUSH, null, null);
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            initMap();
        }

        findViewById(R.id.nav_home).setOnClickListener(v ->
                startActivity(new android.content.Intent(this, HomeActivity.class)));
        findViewById(R.id.nav_groups).setOnClickListener(v ->
                startActivity(new android.content.Intent(this, GroupsActivity.class)));
    }

    private void initMap() {
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        Toast.makeText(this, "Map is ready!", Toast.LENGTH_SHORT).show();
        
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }
        mMap.setMyLocationEnabled(true);
        
        // Enhanced map styling for better visibility
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLocation = location;
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                Toast.makeText(this, "Current location set", Toast.LENGTH_SHORT).show();
            }
        });
        
        mMap.setOnMapClickListener(latLng -> {
            tapCount++;
            Toast.makeText(this, "Tap " + tapCount + " at: " + latLng.latitude + ", " + latLng.longitude, Toast.LENGTH_SHORT).show();
            
            if (tapCount == 1) {
                // Clear previous route
                clearRoute();
                startLatLng = latLng;
                startMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Start Point")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .snippet("Tap again to set destination"));
                endLatLng = null;
                infoPanel.setVisibility(LinearLayout.GONE);
                isNavigating = false;
                
                // Voice feedback for first tap
                tts.speak("Start point set. Tap again to set your destination.", TextToSpeech.QUEUE_FLUSH, null, null);
                
            } else if (tapCount == 2) {
                endLatLng = latLng;
                if (endMarker != null) endMarker.remove();
                endMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .snippet("Route will be displayed"));
                
                // Draw enhanced blue route
                drawRoute();
                
                // Show navigation info and start voice guidance
                showNavigationInfo();
                startVoiceGuidance();
                
                tapCount = 0; // Reset for next use
            }
        });
        
        Toast.makeText(this, "Map click listener set up", Toast.LENGTH_SHORT).show();
        startLocationUpdates();
    }

    private void clearRoute() {
        if (startMarker != null) startMarker.remove();
        if (endMarker != null) endMarker.remove();
        if (routeLine != null) routeLine.remove();
        infoPanel.setVisibility(LinearLayout.GONE);
        findViewById(R.id.clear_route_button).setVisibility(android.view.View.GONE);
        isNavigating = false;
        startLatLng = null;
        endLatLng = null;
    }

    private void drawRoute() {
        if (startLatLng != null && endLatLng != null) {
            Toast.makeText(this, "Fetching route from Google Directions API...", Toast.LENGTH_SHORT).show();
            
            // Fetch the actual route from Google Directions API
            new FetchRouteTask(startLatLng, endLatLng).execute();
        } else {
            Toast.makeText(this, "Cannot draw route - missing coordinates", Toast.LENGTH_SHORT).show();
        }
    }

    private void drawRouteWithPoints(List<LatLng> routePoints) {
        if (routeLine != null) routeLine.remove();
        
        // Create polyline options with all route points
        PolylineOptions polylineOptions = new PolylineOptions()
            .color(0xFF2196F3) // Material Design Blue
            .width(12f) // Thicker line
            .zIndex(1f); // Ensure it's above other elements
        
        // Add all route points
        for (LatLng point : routePoints) {
            polylineOptions.add(point);
        }
        
        // Draw the route
        routeLine = mMap.addPolyline(polylineOptions);
        
        // Update the distance display with actual route distance
        updateRouteDistance(routePoints);
        
        // Fit camera to show the entire route
        if (routePoints.size() > 0) {
            com.google.android.gms.maps.model.LatLngBounds.Builder builder = 
                new com.google.android.gms.maps.model.LatLngBounds.Builder();
            
            for (LatLng point : routePoints) {
                builder.include(point);
            }
            
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
        }
        
        Toast.makeText(this, "Route drawn following roads!", Toast.LENGTH_SHORT).show();
    }

    private void drawStraightLineRoute() {
        if (routeLine != null) routeLine.remove();
        
        // Create a straight line route as fallback
        routeLine = mMap.addPolyline(new PolylineOptions()
            .add(startLatLng, endLatLng)
            .color(0xFF2196F3) // Material Design Blue
            .width(12f) // Thicker line
            .zIndex(1f)); // Ensure it's above other elements
        
        // Fit camera to show the entire route
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
            new com.google.android.gms.maps.model.LatLngBounds.Builder()
                .include(startLatLng)
                .include(endLatLng)
                .build(), 100)); // 100dp padding
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(2000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                currentLocation = locationResult.getLastLocation();
                updateSpeed();
                if (isNavigating) {
                    updateNavigationGuidance();
                }
            }
        }, null);
    }

    private void updateSpeed() {
        if (currentLocation != null) {
            float speed = currentLocation.hasSpeed() ? currentLocation.getSpeed() : 0f;
            tvSpeed.setText(String.format(Locale.getDefault(), "Current Speed: %.1f km/h", speed * 3.6f));
        }
    }

    private void showNavigationInfo() {
        if (startLatLng != null && endLatLng != null) {
            float[] results = new float[1];
            Location.distanceBetween(startLatLng.latitude, startLatLng.longitude, 
                                   endLatLng.latitude, endLatLng.longitude, results);
            float distanceKm = results[0] / 1000f;
            
            tvNavigationStatus.setText("🚗 Navigation Active");
            tvDistance.setText(String.format(Locale.getDefault(), "Route Distance: %.2f km (straight line)", distanceKm));
            infoPanel.setVisibility(LinearLayout.VISIBLE);
            
            // Show clear route button
            findViewById(R.id.clear_route_button).setVisibility(android.view.View.VISIBLE);
            
            // Add animation to make the info panel more noticeable
            Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            infoPanel.startAnimation(fadeIn);
            
            isNavigating = true;
        }
    }

    private void updateRouteDistance(List<LatLng> routePoints) {
        if (routePoints != null && routePoints.size() > 1) {
            double totalDistance = 0;
            for (int i = 0; i < routePoints.size() - 1; i++) {
                float[] results = new float[1];
                Location.distanceBetween(
                    routePoints.get(i).latitude, routePoints.get(i).longitude,
                    routePoints.get(i + 1).latitude, routePoints.get(i + 1).longitude,
                    results
                );
                totalDistance += results[0];
            }
            
            float routeDistanceKm = (float) (totalDistance / 1000.0);
            tvDistance.setText(String.format(Locale.getDefault(), "Route Distance: %.2f km (via roads)", routeDistanceKm));
        }
    }

    private void startVoiceGuidance() {
        if (startLatLng != null && endLatLng != null) {
            float[] results = new float[1];
            Location.distanceBetween(startLatLng.latitude, startLatLng.longitude, 
                                   endLatLng.latitude, endLatLng.longitude, results);
            float distanceKm = results[0] / 1000f;
            
            String direction = getDirection(startLatLng, endLatLng);
            String speakText = String.format(Locale.getDefault(), 
                "Navigation started. Distance to destination: %.2f kilometers. Head %s. Follow the blue route along the roads.", 
                distanceKm, direction);
            
            tts.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void updateNavigationGuidance() {
        if (currentLocation != null && endLatLng != null) {
            float[] results = new float[1];
            Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), 
                                   endLatLng.latitude, endLatLng.longitude, results);
            float remainingDistance = results[0] / 1000f;
            
            // Update distance display with remaining distance
            tvDistance.setText(String.format(Locale.getDefault(), "Remaining: %.2f km", remainingDistance));
            
            // Provide voice guidance every 30 seconds (you can adjust this)
            if (System.currentTimeMillis() % 30000 < 2000) { // Every ~30 seconds
                String direction = getDirection(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), endLatLng);
                String guidanceText = String.format(Locale.getDefault(), 
                    "Continue %s. %.1f kilometers remaining.", direction, remainingDistance);
                tts.speak(guidanceText, TextToSpeech.QUEUE_ADD, null, null);
            }
        }
    }

    private String getDirection(LatLng start, LatLng end) {
        double dLon = Math.toRadians(end.longitude - start.longitude);
        double y = Math.sin(dLon) * Math.cos(Math.toRadians(end.latitude));
        double x = Math.cos(Math.toRadians(start.latitude)) * Math.sin(Math.toRadians(end.latitude)) -
                Math.sin(Math.toRadians(start.latitude)) * Math.cos(Math.toRadians(end.latitude)) * Math.cos(dLon);
        double brng = Math.toDegrees(Math.atan2(y, x));
        brng = (brng + 360) % 360;
        if (brng >= 45 && brng < 135) return "east";
        if (brng >= 135 && brng < 225) return "south";
        if (brng >= 225 && brng < 315) return "west";
        return "north";
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initMap();
        } else {
            Toast.makeText(this, "Location permission is required to show the map", Toast.LENGTH_LONG).show();
        }
    }

    // AsyncTask to fetch route from Google Directions API
    private class FetchRouteTask extends AsyncTask<Void, Void, List<LatLng>> {
        private LatLng origin;
        private LatLng destination;

        public FetchRouteTask(LatLng origin, LatLng destination) {
            this.origin = origin;
            this.destination = destination;
        }

        @Override
        protected List<LatLng> doInBackground(Void... voids) {
            try {
                String url = String.format(
                    "https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&key=%s",
                    origin.latitude, origin.longitude,
                    destination.latitude, destination.longitude,
                    DIRECTIONS_API_KEY
                );

                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                inputStream.close();
                connection.disconnect();

                return parseDirectionsResponse(response.toString());

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<LatLng> routePoints) {
            if (routePoints != null && !routePoints.isEmpty()) {
                drawRouteWithPoints(routePoints);
            } else {
                // Fallback to straight line if API fails
                Toast.makeText(SeeMapActivity.this, "Using straight line route (API failed)", Toast.LENGTH_SHORT).show();
                drawStraightLineRoute();
            }
        }

        private List<LatLng> parseDirectionsResponse(String response) {
            List<LatLng> routePoints = new ArrayList<>();
            try {
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray routes = jsonResponse.getJSONArray("routes");
                
                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONObject polyline = route.getJSONObject("overview_polyline");
                    String encodedPolyline = polyline.getString("points");
                    
                    // Decode the polyline
                    routePoints = PolyUtil.decode(encodedPolyline);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routePoints;
        }
    }
}
