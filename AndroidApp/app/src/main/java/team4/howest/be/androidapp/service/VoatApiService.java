package team4.howest.be.androidapp.service;


import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedString;
import rx.Observable;
import team4.howest.be.androidapp.model.ApiResponse;
import team4.howest.be.androidapp.model.Authentication;
import team4.howest.be.androidapp.model.CommentPostRequest;
import team4.howest.be.androidapp.model.Comments;
import team4.howest.be.androidapp.model.DefaultSubverseResponse;
import team4.howest.be.androidapp.model.ReplyResponse;
import team4.howest.be.androidapp.model.Submission;
import team4.howest.be.androidapp.model.SubmissionResponse;
import team4.howest.be.androidapp.model.SubmissionsForSubverse;
import team4.howest.be.androidapp.model.SubverseInfoResponse;
import team4.howest.be.androidapp.model.User;
import team4.howest.be.androidapp.model.UserSubmission;
import team4.howest.be.androidapp.model.UserSubmissionResponse;
import team4.howest.be.androidapp.model.UserSubscriptionsResponse;
import team4.howest.be.androidapp.model.VoteResponse;


/**
 * Created by anthony on 15.18.10.
 */

/*
TODO
Alle responses volgen het formaat {success: true, data: { ... }}
We kunnen dus een requestinterceptor maken
*/

public interface VoatApiService {
    @GET("/v1/v/{subverse}/{submissionID}/comments")
    Observable<Comments> getCommentsForSubmission(
            @Path("subverse") String subverse,
            @Path("submissionID") String submissionID
    );

    @GET("/v1/u/{user}/comments")
    Observable<Comments> getCommentsForUser(
            @Path("user") String user
    );

    @GET("/v1/v/{subverse}/{submissionID}")
    void getSingleSubmission(
            @Path("subverse") String subverse,
            @Path("submissionID") String submissionID,
            Callback<SubmissionResponse> callback
    );

    @GET("/v1/u/{user}/info")
     Observable<User.UserAPIResponse> getUserInfo(
            @Path("user") String user
    );

    @GET("/v1/vote/{type}/{ID}/{vote}")
    Observable<User.UserAPIResponse> setVote(
            @Path("type") String type,
            @Path("ID") String ID,
            @Path("vote") String vote
    );

    @POST("/v1/vote/{type}/{ID}/{vote}")
    Observable<VoteResponse> postVote(
            @Path("type") String type,
            @Path("ID") String ID,
            @Path("vote") String vote
    );

    @GET("/v1/v/{subverse}/info")
    Observable<SubverseInfoResponse> getSubverseInfo(
            @Path("subverse") String subverse
    );

    @GET("/v1/v/{subverse}")
    void getSubmissionsForSubverse(
            @Path("subverse") String subverse,
            @Query("sort") String sort,
            @Query("page") Integer page,
            Callback<SubmissionsForSubverse> callback

    );

    @GET("/v1/u/{user}/submissions")
    void getSubmissionsForUser(
            @Path("user") String user,
            Callback<SubmissionsForSubverse> callback
    );

    @GET("/v1/v/{subverse}")
    void getSubmissionsForSearch(
            @Path("subverse") String subverse,
            @Query("search") String search,
            Callback<SubmissionsForSubverse> callback
    );

    @GET("/path/of/interest")
    Comments getDummieContent();

    @POST("/v1/v/{subverse}/{submissionID}/comment/{commentID}")
    Observable<ReplyResponse> postReplyToComment(
        @Path("subverse") String subverse,
        @Path("submissionID") String submissionID,
        @Path("commentID") String commentID,
        @Body CommentPostRequest value
    );

    @POST("/v1/v/{subverse}/{submissionID}/comment")
    Observable<ReplyResponse> postReplyToSubmission(
            @Path("subverse") String subverse,
            @Path("submissionID") String submissionID,
            @Body CommentPostRequest value
    );


    @Headers({
            "Content-Type: application/x-www-form-urlencoded",
    })
    @POST("/token")
    void authenticate(
            @Body TypedString body,
            Callback<Authentication> callback
    );

    @GET("/defaultsubverses")
    void getDefaultSubverses(Callback<DefaultSubverseResponse> callback);

    @POST("/v1/v/{subverse}")
    void postSubmission(@Path("subverse") String subverse,
                          @Body UserSubmission userSubmission,
                          Callback<UserSubmissionResponse> cb);

    @GET("/v1/u/{user}/subscriptions")
    void getUserSubscriptions(
            @Path("user") String user,
            Callback<UserSubscriptionsResponse> callback
    );

    @POST("/v1/submissions/{submissionID}/save")
    void saveSubmission(
            @Path("submissionID") Integer submissionID,
            Callback<ApiResponse> callback
    );

}
