package csell.com.vn.csell.apis;

import com.crashlytics.android.Crashlytics;

import java.util.concurrent.TimeUnit;

import csell.com.vn.csell.constants.Constants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static Retrofit retrofit = null;
    public static Retrofit retrofitLogin = null;
    public static Retrofit retrofitUploadImage = null;

    public static <S> S createServiceLogin(Class<S> serviceClass) {
        try {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client;

            client = new OkHttpClient.Builder()
                    .addNetworkInterceptor(chain -> {
                        Request.Builder requestBuilder = chain.request().newBuilder()
                                .header("Content-type", "application/json");

                        return chain.proceed(requestBuilder.build());
                    })
                    .addInterceptor(interceptor)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();

            if (retrofitLogin == null) {
                retrofitLogin = new Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL_V2)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return retrofitLogin.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, String authToken) {
        try {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client;

            client = new OkHttpClient.Builder()
                    .addNetworkInterceptor(chain -> {
                        Request.Builder requestBuilder = chain.request().newBuilder()
                                .header("Content-type", "application/json")
                                .header("Authorization", "Bearer " + authToken);
                        return chain.proceed(requestBuilder.build());
                    })
                    .addInterceptor(interceptor)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();

            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL_V2)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return retrofit.create(serviceClass);
    }

    public static <S> S createServiceUploadImage(Class<S> serviceClass, String authToken) {
        try {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client;

            client = new OkHttpClient.Builder()
                    .addNetworkInterceptor(chain -> {
                        Request.Builder requestBuilder = chain.request().newBuilder()
                                .header("Authorization", "Bearer " + authToken);
                        return chain.proceed(requestBuilder.build());
                    })
                    .addInterceptor(interceptor)
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();

            if (retrofitUploadImage == null) {
                retrofitUploadImage = new Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL_V2)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return retrofitUploadImage.create(serviceClass);
    }
}
