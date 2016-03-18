package com.hhtv.eventqa_admin.api;

import com.hhtv.eventqa_admin.models.event.Event;
import com.hhtv.eventqa_admin.models.event.Result;
import com.hhtv.eventqa_admin.models.question.Question;
import com.hhtv.eventqa_admin.models.question.Vote;
import com.hhtv.eventqa_admin.models.user.GetUserResponse;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by nienb on 10/3/16.
 */
public interface APIEndpoint {


    @FormUrlEncoded
    @POST("/api/event/getUser")
    Call<GetUserResponse> getUser(@Field("username") String username, @Field("password") String password);

    @GET("/api/event/getEventDetail")
    Call<Result> getEvent(@Query("id") int eventid);


    @GET("/api/event/getQuestions")
    Call<Question> getAllQuestions(@Query("event_id") int eventId,@Query("user_id") int userId,@Query("device_id") String deviceid);
    @GET("/api/event/getUpdatedQuestions")
    Call<Vote> updateQuestionsList(@Query("device_id") String lastCheckstr,
                                   @Query("event_id") int eventid,
                                   @Query("user_id") int creatorid);
    @GET("/api/event/getHighestVoteQuestions")
    Call<Question> getHighestVoteQuestion(@Query("event_id") int eventId,@Query("user_id")
    int userId,@Query("device_id") String deviceid);


    //TODO remove fake method
    @GET("/getEvent")
    Call<Event> getFakeEvent(@Query("userid") String userId);

}
