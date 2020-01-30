package csell.com.vn.csell.apis;

import java.util.List;

import csell.com.vn.csell.models.DataCustomerV1;
import csell.com.vn.csell.models.DataGroupCustomers;
import csell.com.vn.csell.models.FriendResponse;
import csell.com.vn.csell.models.GroupCustomerRetro;
import csell.com.vn.csell.models.Note;
import csell.com.vn.csell.models.NoteV1;
import csell.com.vn.csell.models.Product;
import csell.com.vn.csell.models.ProductCountResponse;
import csell.com.vn.csell.models.ProductResponseV1;
import csell.com.vn.csell.models.Project;
import csell.com.vn.csell.models.ResCustomers;
import csell.com.vn.csell.models.UserRetro;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetAPI {

    /* Customer */
    @GET("user")
    Call<JSONResponse<UserRetro>> getDetail();

    @GET("user/verify")
    Call<JSONResponse<Object>> verifyAccount(@Query("account") String userName);

    @GET("customers")
    Call<DataCustomerV1> getCustomers(@Query("skip") int skip,
                                      @Query("limit") int limit,
                                      @Query("keyword") String keyword,
                                      @Query("phones") String phones,
                                      @Query("emails") String emails);

    @GET("customers/{custId}")
    Call<ResCustomers> getDetailCustomer(@Path("custId") String customerId);

    @GET("customer-group")
    Call<DataGroupCustomers> getListGroup(@Query("keyword") String keyword);

    @GET("customer/group/{groupId}")
    Call<JSONResponse<List<GroupCustomerRetro>>> getDetailGroup(@Path("groupId") String groupId);


    /* Note */
    @GET("notes/{noteId}")
    Call<NoteV1> getDetailNote(@Path("noteId") String noteId);

    @GET("note")
    Call<JSONResponse<List<Note>>> getNoteToday(@Query("skip") int skip, @Query("limit") int limit,
                                                @Query("start") String dateStart, @Query("end") String dateEnd);

    @GET("notes")
    Call<JSONNote<List<NoteV1>>> getListNote(@Query("productId") String productId, @Query("customerId") String customerId);







    /*GET Friends*/

    @GET("friend/search")
    Call<JSONResponse<List<UserRetro>>> searchFriend(@Query("keyword") String search,
                                                     @Query("skip") int skip,
                                                     @Query("limit") int limit);

    @GET("friend")
    Call<JSONResponse<List<FriendResponse>>> getFriends(@Query("skip") int skip,
                                                        @Query("limit") int limit);

    @GET("friend/request")
    Call<JSONResponse<List<FriendResponse>>> getFriendRequest(@Query("skip") int skip,
                                                              @Query("limit") int limit);

    @GET("user/{userId}")
    Call<JSONResponse<List<UserRetro>>> getFriendDetail(@Path("userId") String userId);

    @GET("user/{userId}/item")
    Call<JSONResponse<List<Product>>> getFriendNewFeed(@Path("userId") String userId,
                                                       @Query("skip") int skip,
                                                       @Query("limit") int limit);

    @GET("user/{userId}/item")
    Call<JSONResponse<List<ProductCountResponse>>> getFriendGroupProduct(@Path("userId") String userId,
                                                                         @Query("level") int level,
                                                                         @Query("catid") String categoryId,
                                                                         @Query("projectid") String project);


    @GET("user/{userId}/item")
    Call<JSONResponse<List<Product>>> getFriendListProduct(@Path("userId") String userId,
                                                           @Query("catid") String categoryId,
                                                           @Query("skip") int skip,
                                                           @Query("limit") int limit);


    /*Get Product*/
    @GET("products")
    Call<ProductResponse> getListProducts(@Query("provinceId") String provinceId,
                                          @Query("districtId") String districtId,
                                          @Query("wardId") String wardId,
                                          @Query("projectId") String projectId,
                                          @Query("minPrice") String minPrice,
                                          @Query("skip") int skip,
                                          @Query("limit") int limit,
                                          @Query("categoryId") String categoryId);

    @GET("products/{itemId}")
    Call<ProductResponseV1> getDetailProduct(@Path("itemId") String productId);

    @GET("item")
    Call<JSONResponse<List<ProductCountResponse>>> getGroupProducts(@Query("type") int type,
                                                                    @Query("level") int level,
                                                                    @Query("catid") String cat,
                                                                    @Query("projectid") String project);

    @GET("item")
    Call<JSONResponse<List<Product>>> getListProducts(@Query("type") int type,
                                                      @Query("catid") String cat,
                                                      @Query("projectid") String project,
                                                      @Query("skip") int skip,
                                                      @Query("limit") int limit);

    @GET("item")
    Call<JSONResponse<List<Product>>> getAllProduct(@Query("type") int type,
                                                    @Query("skip") int skip,
                                                    @Query("limit") int limit);


    @GET("item")
    Call<JSONResponse<List<Product>>> getProductByProject(@Query("projectid") String project,
                                                          @Query("type") int type,
                                                          @Query("catid") String catId,
                                                          @Query("skip") int skip,
                                                          @Query("limit") int limit);

    @GET("item/follow")
    Call<JSONResponse<List<Product>>> getProductFollows(@Query("skip") int skip,
                                                        @Query("limit") int limit);












    /*Social */

    @GET("feed")
    Call<JSONResponse<List<Product>>> getNewfeeds(@Query("catid") String cat,
                                                  @Query("skip") int skip,
                                                  @Query("limit") int limit);

    @GET("user/{uid}/item/{itemid}")
    Call<JSONResponse<List<Product>>> getNewfeedDetail(@Path("uid") String uid,
                                                       @Path("itemid") String pid);


    @GET("feed/search")
    Call<JSONResponse<List<Product>>> filterNewfeed(@Query("skip") int skip,
                                                    @Query("limit") int limit,
                                                    @Query("root_cat") String rootName,
                                                    @Query("sub_cat") String subName,
                                                    @Query("catid") String catName,
                                                    @Query("from") String priceMin,
                                                    @Query("to") String priceMax,
                                                    @Query("city") String city,
                                                    @Query("district") String district);

    @GET("item/{pid}/info")
    Call<JSONResponse<List<UserRetro>>> getUsersSocial(@Path("pid") String pid,
                                                       @Query("users") String uids);


    /*Project*/
    @GET("item/projects")
    Call<JSONResponse<List<Project>>> searchProject(@Query("skip") int skip,
                                                    @Query("limit") int limit,
                                                    @Query("name") String key,
                                                    @Query("city") String city,
                                                    @Query("district") String district);

    @GET("item/projects/{pid}")
    Call<JSONResponse<List<Project>>> getDetailProject(@Path("pid") String pid);

}
