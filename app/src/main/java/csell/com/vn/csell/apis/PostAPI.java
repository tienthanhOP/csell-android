package csell.com.vn.csell.apis;

import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.models.CustomerRetro;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.models.ProductResponseModel;
import csell.com.vn.csell.models.ProductResponseV1;
import csell.com.vn.csell.models.Project;
import csell.com.vn.csell.models.ResCustomers;
import csell.com.vn.csell.models.ResDelete;
import csell.com.vn.csell.models.ResLogin;
import csell.com.vn.csell.models.UserRetro;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostAPI {

    /*Image*/
    @Multipart
    @POST("images/upload-multiple")
    Call<ImageResponse> uploadImageInGroup(@Part List<MultipartBody.Part> files, @Query("group") String group);

    /*
     * API User
     * */

    @POST("auth/login")
    Call<ResLogin> login(@Body HashMap<String, Object> user);

    @POST("auth/login-token/web")
    Call<ResLogin> loginSocial(@Body HashMap<String, Object> user);

    @POST("auth/logout")
    Call<ResLogin> logout();

    @POST("auth/register")
    Call<ResLogin> register(@Body HashMap<String, Object> user);

    @POST("user")
    Call<JSONResponse<Object>> updateInfo(@Body UserRetro user);

    @POST("user/sendEmail")
    Call<JSONResponse<Object>> sendVerifyEmail(@Body HashMap<String, Object> params);

    @POST("auth/request-active-account")
    Call<HashMap<String, Object>> sendVerifyPhone(@Body HashMap<String, Object> params);

    @POST("user/active")
    Call<HashMap<String, Object>> activeAccount(@Body HashMap<String, Object> params);

    @POST("user")
    Call<JSONResponse<Object>> updatePassword(@Body HashMap<String, Object> params);

    @POST("users/reset-password")
    Call<HashMap<String, Object>> resetPassword(@Body HashMap<String, Object> params);

    @POST("user")
    Call<JSONResponse<Object>> updateAvatar(@Body HashMap<String, Object> params);

    @POST("user")
    Call<JSONResponse<Object>> updateCover(@Body HashMap<String, Object> params);

    @POST("user/code")
    Call<JSONResponse<Object>> sendEmailCode(@Body HashMap<String, Object> params);





    /*
     * API Customer
     * */

    @POST("customers")
    Call<ResCustomers> addCustomer(@Body HashMap<String, Object> map);

    @POST("customer/addMultiple")
    Call<JSONResponse<List<CustomerRetro>>> addListCustomer(@Body HashMap<String, Object> map);

    @DELETE("customers/{custid}")
    Call<ResDelete> removeCustomer(@Path("custid") String customerId);

    @PUT("customers/{custid}")
    Call<ResCustomers> updateCustomer(@Path("custid") String customerId, @Body HashMap<String, Object> map);

    @PUT("customer/group")
    Call<JSONResponse<Object>> addGroupCustomer(@Body HashMap<String, Object> map);

    @POST("customer/group")
    Call<JSONResponse<Object>> updateGroup(@Body HashMap<String, Object> map);

    @POST("customer/group/{groupId}")
    Call<JSONResponse<Object>> removeGroup(@Path("groupId") String groupId);









    /*
     * API Notes
     * */

    @POST("notes")
    Call<HashMap<String, Object>> addNote(@Body HashMap<String, Object> map);

    @PUT("notes/{noteId}")
    Call<HashMap<String, Object>> updateNote(@Body HashMap<String, Object> map, @Path("noteId") String noteId);

    @DELETE("notes/{noteId}")
    Call<HashMap<String, Object>> removeNote(@Path("noteId") String noteId);






    /*
     * API Products
     * */

    @POST("products")
    Call<JSONProductV1<ProductResponseV1>> addProduct(@Body HashMap body);

    @PUT("products/{productId}")
    Call<JSONProductV1<ProductResponseV1>> updateProduct(@Path("productId") String pid, @Body HashMap body);

    @POST("item/reup")
    Call<JSONResponse<ProductResponseModel>> reupProduct(@Body HashMap body);

    @POST("item")
    Call<JSONResponse<List<ProductResponseModel>>> updatePrivateNote(@Body HashMap body);

    @POST("item")
    Call<JSONResponse<List<ProductResponseModel>>> updateImages(@Body HashMap body);

    @POST("item")
    Call<JSONResponse<List<ProductResponseModel>>> updateOwnerInfo(@Body HashMap body);

    @POST("item")
    Call<JSONResponse<Object>> updatePostType(@Body HashMap body);

    @DELETE("products/{pid}")
    Call<HashMap<String, Object>> removeProduct(@Path("pid") String pid);








    /*
     * API Friends
     * */

    @POST("friend/request")
    Call<JSONResponse<Object>> sendRequest(@Body HashMap body);

    @PUT("friend")
    Call<JSONResponse<Object>> acceptFriend(@Body HashMap body);

    @POST("friend/remove")
    Call<JSONResponse<Object>> unFriend(@Body HashMap body);

    @POST("friend/follow")
    Call<JSONResponse<Object>> followFriend(@Body HashMap body);

    @PUT("friend/follow")
    Call<JSONResponse<Object>> unfollowFriend(@Body HashMap body);






    /*API Social*/

    @PUT("feed")
    Call<JSONResponse<List<Product>>> postEmpty(@Body HashMap body);

    @POST("item/follow")
    Call<JSONResponse<Object>> followPost(@Body HashMap body);

    @POST("item/follow/{itemid}")
    Call<JSONResponse<Object>> unfollowPost(@Path("itemid") String itemid, @Body HashMap body);

    /*
     * API Noti
     * */
    @POST("user/notification")
    Call<JSONResponse<Object>> sendNoti(@Body HashMap body);

    @PUT("user/devices")
    Call<JSONResponse<Object>> removeDevice(@Body HashMap body);

    @POST("user/devices")
    Call<JSONResponse<Object>> addDevice(@Body HashMap body);


    /*
     * API Projects
     **/
    @POST("item/projects")
    Call<JSONResponse<Project>> addProject(@Body Project body);


    /*UploadImage*/
    @POST("/images")
    Call<JSONResponse<List<String>>> uploadImages(@Body HashMap body);

}
