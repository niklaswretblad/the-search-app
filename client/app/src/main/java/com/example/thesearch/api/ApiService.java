package com.example.thesearch.api;

import com.example.thesearch.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("user/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    @POST("user/logout")
    Call<Void> logout();

    @GET("user/{userId}/spots")
    Call<List<Spot>> getUserSpots(@Path("userId") String userId);

    @GET("search_users")
    Call<List<User>> searchUsers(@Query("query") String query);

    @POST("spots")
    Call<SpotResponse> createSpot(@Body SpotRequest request);

    @GET("spots/{spotId}")
    Call<Spot> getSpot(@Path("spotId") int spotId);

    @PUT("spots/{spotId}")
    Call<Void> updateSpot(@Path("spotId") int spotId, @Body SpotRequest request);

    @DELETE("spots/{spotId}")
    Call<Void> removeSpot(@Path("spotId") int spotId);

    @GET("user/{userId}/spots_and_followed_spots")
    Call<List<Spot>> getUserAndFollowedSpots(@Path("userId") String userId);

    @POST("likes")
    Call<LikeResponse> likeSpot(@Body LikeRequest request);

    @HTTP(method = "DELETE", path = "likes", hasBody = true)
    Call<Void> unlikeSpot(@Body UnlikeRequest request);

    @GET("likes/{spotId}")
    Call<LikeCountResponse> getNumberOfLikes(@Path("spotId") int spotId);

    @POST("comments/{spotId}")
    Call<CommentResponse> commentSpot(@Path("spotId") int spotId, @Body CommentRequest request);

    @DELETE("comments/{commentId}")
    Call<Void> removeComment(@Path("commentId") int commentId);

    @GET("comments/{spotId}")
    Call<List<Comment>> getSpotComments(@Path("spotId") int spotId);

    @GET("follows/check/{userId}/{targetId}")
    Call<FollowCheckResponse> checkIfFollowing(@Path("userId") String userId, @Path("targetId") String targetId);

    @POST("follows")
    Call<Void> followUser(@Body FollowRequest request);

    @DELETE("follows/{userId}/{targetId}")
    Call<Void> unfollowUser(@Path("userId") String userId, @Path("targetId") String targetId);

    @GET("user/{userId}/followed_spots")
    Call<List<Spot>> getFollowedSpots(@Path("userId") String userId);
}
