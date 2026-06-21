package com.cineai.app.ui.splash;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.cineai.app.R;
import com.cineai.app.ui.main.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.util.List;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private static final long SPLASH_DURATION = 2600;

    private FusedLocationProviderClient fusedLocationClient;
    private TextView tvLocation;
    private View loadingLineFill;
    private View loadingLineTrack;
    private boolean navigated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tvLocation      = findViewById(R.id.tv_location);
        loadingLineFill = findViewById(R.id.loading_line_fill);
        loadingLineTrack = findViewById(R.id.loading_line_track);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Animasi loading line
        startLineAnimation();

        // Navigate setelah duration
        new Handler().postDelayed(this::goToMain, SPLASH_DURATION);

        // GPS
        if (hasLocationPermission()) {
            fetchLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, LOCATION_PERMISSION_REQUEST);
        }
    }

    // ─── Line animation ───────────────────────────────────────

    private void startLineAnimation() {
        // Tunggu track selesai di-layout dulu
        loadingLineTrack.post(() -> {
            int trackWidth = loadingLineTrack.getWidth();
            if (trackWidth == 0) trackWidth = (int) (80 * getResources().getDisplayMetrics().density);

            final int finalTrackWidth = trackWidth;
            ValueAnimator animator = ValueAnimator.ofInt(0, finalTrackWidth);
            animator.setDuration(SPLASH_DURATION - 300);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.addUpdateListener(anim -> {
                int w = (int) anim.getAnimatedValue();
                android.view.ViewGroup.LayoutParams lp = loadingLineFill.getLayoutParams();
                lp.width = w;
                loadingLineFill.setLayoutParams(lp);
            });
            animator.start();
        });
    }

    // ─── Location ─────────────────────────────────────────────

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void fetchLocation() {
        if (!hasLocationPermission()) return;
        CancellationTokenSource cts = new CancellationTokenSource();
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        reverseGeocode(location);
                    } else {
                        fusedLocationClient.getLastLocation().addOnSuccessListener(last -> {
                            if (last != null) reverseGeocode(last);
                            else showDefaultLocation();
                        });
                    }
                })
                .addOnFailureListener(e -> showDefaultLocation());
    }

    private void reverseGeocode(Location location) {
        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(this, new Locale("id", "ID"));
                List<Address> addresses = geocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String area = address.getSubAdminArea() != null ? address.getSubAdminArea()
                            : address.getLocality() != null ? address.getLocality()
                            : address.getAdminArea() != null ? address.getAdminArea()
                            : "Indonesia";
                    String flag = getFlagEmoji(address.getCountryCode() != null
                            ? address.getCountryCode() : "ID");
                    String text = "📍 " + area + " " + flag;
                    runOnUiThread(() -> {
                        tvLocation.setText(text);
                        tvLocation.setVisibility(View.VISIBLE);
                    });
                } else showDefaultLocation();
            } catch (Exception e) { showDefaultLocation(); }
        }).start();
    }

    private void showDefaultLocation() {
        runOnUiThread(() -> {
            tvLocation.setText("📍 Indonesia 🇮🇩");
            tvLocation.setVisibility(View.VISIBLE);
        });
    }

    private String getFlagEmoji(String code) {
        if (code == null || code.length() != 2) return "🌍";
        int a = Character.codePointAt(code, 0) - 'A' + 0x1F1E6;
        int b = Character.codePointAt(code, 1) - 'A' + 0x1F1E6;
        return new String(Character.toChars(a)) + new String(Character.toChars(b));
    }

    @Override
    public void onRequestPermissionsResult(int reqCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(reqCode, permissions, grantResults);
        if (reqCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                fetchLocation();
            else showDefaultLocation();
        }
    }

    private void goToMain() {
        if (!navigated) {
            navigated = true;
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }
}