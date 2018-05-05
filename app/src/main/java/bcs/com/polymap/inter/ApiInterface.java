package bcs.com.polymap.inter;

import bcs.com.polymap.model.Model;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("json/mapres.json")
    Call<Model> GetMapData();
}
