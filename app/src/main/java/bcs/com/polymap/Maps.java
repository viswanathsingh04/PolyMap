package bcs.com.polymap;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bcs.com.polymap.inter.ApiInterface;
import bcs.com.polymap.model.Alert;
import bcs.com.polymap.model.Model;
import bcs.com.polymap.model.Parameter;
import bcs.com.polymap.model.Polygon;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Maps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<Alert> polygonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        polygonList = new ArrayList<>();
        GetData();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(new LatLng(-18.142, 178.431)).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(-18.142, 178.431), 2));
        // Polylines are useful for marking paths and routes on the map.
        mMap.addPolyline(new PolylineOptions().geodesic(true)
                        .add(new LatLng(17.1319086774805, 79.216953038765))
                        .add(new LatLng(16.6120656348321, 79.2521746471596))
                        .add(new LatLng(16.685249934407, 79.6126464318652))
                        .add(new LatLng(17.1776705218865, 79.4423545773502))
                        /*.add(new LatLng(17.0523483194474, 79.5306413275148))*/
                        /*.add(new LatLng(17.0632570218038, 79.5197326251584))*/
                        /*.add(new LatLng(17.0656033790209, 79.4997326251584))*/
                        /*.add(new LatLng(17.0523483194474, 79.4842327909574))*/
                        /*.add(new LatLng(17.0323483194474, 79.4954406354342))*/
                        .add(new LatLng(-33.866, 151.195))  // Sydney
                        .add(new LatLng(-18.142, 178.431))  // Fiji
               /* .add(new LatLng(21.291, -157.821))  // Hawaii
                .add(new LatLng(37.423, -122.091))  // Mountain View*/
        );
    }

    private void GetData() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://192.168.0.127/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        final ProgressDialog progressDoalog;
        progressDoalog = new ProgressDialog(this);
        progressDoalog.setMax(100);
        progressDoalog.setMessage("Its loading....");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
        Call<Model> call = apiInterface.GetMapData();
        Log.d("tag1", "message");
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(@NonNull Call<Model> call, @NonNull Response<Model> response) {
                Log.d("Map-Success", response.message());
                if (response.isSuccessful() && response.getClass() != null) {
                    Model sd = response.body();
                    //Log.d("Statusdata", String.valueOf(sd.getAlert()));
                    Log.e("Success", new Gson().toJson(response.body()));

                    try {
                        List<Alert> sampleData = fetchResults(response);
                        Log.d("Entered List", "proceeded");
                        if (sampleData != null && sampleData.size() > 0) {
                            for (Alert ghg : sampleData) {
                                polygonList.add(ghg);
                            }
                            //showData.notifyDataSetChanged();
                        }
                        System.out.println("ArraySize" + polygonList.size());
                        List<Polygon> data = new ArrayList<>();
                        for (int i = 0; i < polygonList.size(); i++) {
                            Alert pp = polygonList.get(i);
                            List<Polygon> polygon = pp.getPolygon();
                            polygon.get(i).getLongitude();
                            polygon.get(i).getLat();
                            //data.add(polygon);
                            Log.d("polygon.getLat()", String.valueOf(polygon.get(i).getLat()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                progressDoalog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<Model> call, Throwable t) {
                t.printStackTrace();
                progressDoalog.dismiss();
                Log.v("Map-Error", "No Response!");
            }
        });
    }

    /**
     * @param response extracts List<{@link Parameter>} from response
     * @return
     */
    public List<Alert> fetchResults(Response<Model> response) {
        Model alert = response.body();
        alert.getAlert().getClass().getName();
        return alert.getAlert();
    }
}
