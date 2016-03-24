package com.hhtv.eventqa_admin.api;

import com.hhtv.eventqa_admin.models.event.Event;
import com.hhtv.eventqa_admin.models.event.Result2;
import com.hhtv.eventqa_admin.models.question.MarkQuestionResponse;
import com.hhtv.eventqa_admin.models.question.Question;
import com.hhtv.eventqa_admin.models.question.Vote;
import com.hhtv.eventqa_admin.models.user.GetUserResponse;
import com.squareup.okhttp.RequestBody;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;

/**
 * Created by nienb on 10/3/16.
 */
public interface APIEndpoint {


    @FormUrlEncoded
    @POST("/api/event/getUser")
    Call<GetUserResponse> getUser(@Field("username") String username, @Field("password") String password);

    @Multipart
    @FormUrlEncoded
    @POST("/api/event/editEvent")
    Call<String> updateEvent(@Part("event_name") String event_name,
                             @Part("description") String description,
                             @Part("start_date") String start_date,
                             @Part("end_date") String end_date,
                             @Part("image")RequestBody image);

    @GET("/api/event/getEventDetail")
    Call<Result2> getEvent(@Query("id") int eventid);


    @GET("/api/event/getQuestions")
    Call<Question> getAllQuestions(@Query("event_id") int eventId,@Query("user_id") int userId,@Query("device_id") String deviceid);
    @GET("/api/event/getUpdatedQuestions")
    Call<Vote> updateQuestionsList(@Query("device_id") String lastCheckstr,
                                   @Query("event_id") int eventid,
                                   @Query("user_id") int creatorid);
    @GET("/api/event/getHighestVoteQuestions")
    Call<Question> getHighestVoteQuestion(@Query("event_id") int eventId,@Query("user_id")
    int userId,@Query("device_id") String deviceid);


    @GET("/api/event/markQuestionAnswered")
    Call<MarkQuestionResponse> markQuestionAnswered(@Query("user_id") int user_id, @Query("question_id") int question_id);
    @GET("/api/event/markQuestionDeleted")
    Call<MarkQuestionResponse> markQuestionDeleted(@Query("user_id") int user_id, @Query("question_id") int question_id);
    @GET("/api/event/markQuestionDuplicated")
    Call<MarkQuestionResponse> markQuestionDuplicated(@Query("user_id") int user_id, @Query("question_id") int question_id);
    @GET("/api/event/getEvents")
    Call<Event> getEvents(@Query("user_id") int user_id);



    /*//TODO remove fake method
    @GET("/getEvent")
    Call<Event> getFakeEvent(@Query("userid") String userId);*/

}
