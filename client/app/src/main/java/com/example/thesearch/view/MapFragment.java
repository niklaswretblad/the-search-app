package com.example.thesearch.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.thesearch.model.Spot;
import com.example.thesearch.repository.UserRepository;
import com.example.thesearch.viewmodel.SpotViewModel;
import com.example.thesearch.viewmodel.ViewModelFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import com.example.thesearch.R;
import com.example.thesearch.databinding.FragmentMapBinding;
import com.example.thesearch.viewmodel.MapViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapFragment"; // Define the TAG constant
    private static final LatLng DEFAULT_LOCATION = new LatLng(59.3293, 18.0686); // Stockholm, Sweden
    private static final float DEFAULT_ZOOM = 7.0f;

    private final int FINE_PERMISSION_CODE = 1;
    private FragmentMapBinding binding;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private MapViewModel mapViewModel;
    private SpotViewModel spotViewModel;
    private Map<Marker, Spot> currentUserMarkerSpotMap = new HashMap<>();
    private Map<Marker, Spot> followedUsersMarkerSpotMap = new HashMap<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        UserRepository userRepository = new UserRepository();
        ViewModelFactory factory = new ViewModelFactory(userRepository);
        mapViewModel = new ViewModelProvider(requireActivity(), factory).get(MapViewModel.class);

        spotViewModel = new ViewModelProvider(requireActivity(), factory).get(SpotViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Initialize SupportMapFragment and request the map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Observe the surf spots LiveData, to account for changes and updates
        spotViewModel.getSpotsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Spot>>() {
            @Override
            public void onChanged(List<Spot> surfSpots) {
               updateCurrentUserMapMarkers(surfSpots);
            }
        });

        // Observe the surf spots LiveData, to account for changes and updates
        spotViewModel.getFollowedSpotsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Spot>>() {
            @Override
            public void onChanged(List<Spot> surfSpots) {
                updateFollowedUsersMapMarkers(surfSpots);
            }
        });

        return root;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Set a long click listener on the map
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // Show the AddSurfSpotFragment when the map is long-clicked
                // TODO: This should be done so that the clicked area is centered in the little square still visible above the bottomsheet

                Log.d(TAG, String.format("Map long-clicked at: (%f, %f)", latLng.latitude, latLng.longitude));
                AddSurfSpotFragment addSurfSpotFragment = AddSurfSpotFragment.newInstance(latLng.latitude, latLng.longitude);
                addSurfSpotFragment.show(getChildFragmentManager(), "AddSurfSpotFragment");
            }
        });

        // Set a marker click listener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d(TAG, String.format("Marker clicked entered"));
                Spot surfSpot = currentUserMarkerSpotMap.get(marker);
                if (surfSpot != null) {
                    Log.d(TAG, String.format("Marker clicked: %s at (%f, %f)", surfSpot.getName(), surfSpot.getLatitude(), surfSpot.getLongitude()));
                    ViewSurfSpotFragment viewSurfSpotDialogFragment = ViewSurfSpotFragment.newInstance(surfSpot, true);
                    viewSurfSpotDialogFragment.show(getChildFragmentManager(), "ViewSurfSpotFragment");
                    return true;
                } else {
                    surfSpot = followedUsersMarkerSpotMap.get(marker);
                    if (surfSpot != null) {
                        Log.d(TAG, String.format("Marker clicked: %s at (%f, %f)", surfSpot.getName(), surfSpot.getLatitude(), surfSpot.getLongitude()));
                        ViewSurfSpotFragment viewSurfSpotDialogFragment = ViewSurfSpotFragment.newInstance(surfSpot, false);
                        viewSurfSpotDialogFragment.show(getChildFragmentManager(), "ViewSurfSpotFragment");
                        return true;
                    }
                }
                Log.d(TAG, String.format("Marker clicked FAILED. Did not find spot marker."));
                return false;
            }
        });

        getCurrentLocation();

        spotViewModel.loadSpots();
        spotViewModel.loadFollowedSpots();
    }

    public void updateCurrentUserMapMarkers(List<Spot> spots) {
        if (mMap != null) {
            // First clear the map from the old current user markers
            for (Map.Entry<Marker, Spot> entry : currentUserMarkerSpotMap.entrySet()) { {
                Marker marker = entry.getKey();
                marker.remove();
            }}

            // Then clear the hashmap storing markers + spots
            currentUserMarkerSpotMap.clear();

            // Now add the new stuff!
            for (Spot spot : spots) {
                LatLng position = new LatLng(spot.getLatitude(), spot.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(spot.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                currentUserMarkerSpotMap.put(marker, spot);
                Log.d(TAG, String.format(
                        "updateCurrentUserMapMarkers SurfSpot marker created at Lat: %f, Lon: %f",
                        position.latitude, position.longitude));
            }
        }
    }

    public void updateFollowedUsersMapMarkers(List<Spot> spots) {
        if (mMap != null) {
            // First clear the map from the old current user markers
            for (Map.Entry<Marker, Spot> entry : followedUsersMarkerSpotMap.entrySet()) { {
                Marker marker = entry.getKey();
                marker.remove();
            }}

            // Then clear the hashmap storing markers + spots
            followedUsersMarkerSpotMap.clear();

            // Now add the new stuff
            for (Spot spot : spots) {
                LatLng position = new LatLng(spot.getLatitude(), spot.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(spot.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                followedUsersMarkerSpotMap.put(marker, spot);
                Log.d(TAG, String.format(
                        "updateFollowedUsersMapMarkers SurfSpot marker created at Lat: %f, Lon: %f",
                        position.latitude, position.longitude));
            }
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            Log.d(TAG, "getCurrentLocation() - Does not have location privileges");
            return;
        }

        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d(TAG, "getCurrentLocation() - onSuccess");
                LatLng currentLatLng;
                if (location != null) {
                    Log.d(TAG, "getCurrentLocation() - Changing location to current location");
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                } else {
                    Log.d(TAG, "getCurrentLocation() - Location is null, using default location");
                    currentLatLng = DEFAULT_LOCATION;
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "getCurrentLocation() - Failed to get location, using default location", e);
                LatLng currentLatLng = DEFAULT_LOCATION;
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}