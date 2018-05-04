package bcs.com.polymap.inter;

import bcs.com.polymap.model.Alert;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("json/mapres.json")
    Call<Alert> GetMapData();
}
