package com.example.my2;

import com.example.my2.model.Channel;
import com.example.my2.model.ChannelFeed;
import com.example.my2.model.Feed;
import com.example.my2.model.PublicChannels;
import com.example.my2.model.StatusUpdates;

import java.util.List;
import java.util.Map;


import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/***
 * Java interface representation of the ThingSpeak API using the Retrofit library.
 *
 * @author Macro Yau
 */
public interface ThingSpeakService {

    @GET("/channels/{id}/feeds.json")
    void getChannelFeed(@Path("id") Long channelId, @QueryMap Map<String, String> params, Callback<ChannelFeed> callback);

    @GET("/channels/{id}/feeds/last.json")
    void getLastEntryInChannelFeed(@Path("id") Long channelId, @QueryMap Map<String, String> params, Callback<Feed> callback);

    @GET("/channels/{id}/feeds/{entry}.json")
    void getSpecificEntryInChannelFeed(@Path("id") Long channelId, @Path("entry") Long entryId, @QueryMap Map<String, String> params, Callback<Feed> callback);

    @GET("/channels/{id}/fields/{field}.json")
    void getChannelFieldFeed(@Path("id") Long channelId, @Path("field") Integer fieldId, @QueryMap Map<String, String> params, Callback<ChannelFeed> callback);

    @GET("/channels/{id}/status.json")
    void getStatusUpdates(@Path("id") Long channelId, @QueryMap Map<String, String> params, Callback<StatusUpdates> callback);

    @GET("/channels/public.json")
    void listPublicChannels(@Query("page") Integer page, @Query("tag") String tag, @Query("username") String username,
                            @Query("latitude") Float latitude, @Query("longitude") Float longitude, @Query("distance") Float distance,
                            Callback<PublicChannels> callback);

    @GET("/channels.json")
    void listMyChannels(@Query("api_key") String apiKey, Callback<List<Channel>> callback);

}
