package com.example.wehealed.medical_lens;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {
    @GET("token/")
    Call<TextItem> getIndex(
            @Query("text") String text
    );

    /*
    @GET("test.php")
    Call<RetrofitRepo> getItem(
            @QueryMap Map<String, String> option
    );

    @FormUrlEncoded
    @POST("post.php")
    Call<RetrofitRepo> getPost(
            @Field("name") String name
    );
*/

}
