package com.example.wehealed.medical_lens;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitService {

    // 서버 URL
    public static final String requestBaseURL = "https://wehealedapi.run.goorm.io/api/";

    @GET("test/")
    Call<TextItem> getIndex(
            @Query("text") String text
    );

    //@FormUrlEncoded
    @POST("request_translate_test/")
    Call<MachineTranslationResponseJSON> getJSON(@Body MachineTranslationRequestJSON machineTranslationRequestJSON);

    //@FormUrlEncoded
    @POST("request_translate_test/")
    Call<MachineTranslationResponseJSON> getJSON_Test(@Body MachineTranslationRequestJSON machineTranslationRequestJSON);

    //@FormUrlEncoded
    @POST("request_translate_v2/")
    Call<MachineTranslationResponseJSON> getJSON_V2(@Body MachineTranslationRequestJSON machineTranslationRequestJSON);

    //@FormUrlEncoded
    @POST("request_translate_v4/")
    Call<MachineTranslationResponseJSON> getJSON_V4(@Body MachineTranslationRequestJSON machineTranslationRequestJSON);

    /*
    @FormUrlEncoded
    @POST("test2/")
    Call<TextItem> getPostIndex(
            @Query("text") String text
    );*/

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
