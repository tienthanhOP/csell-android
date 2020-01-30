package csell.com.vn.csell.controllers;

import android.content.Context;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.models.UploadImageRetro;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadImageController {
    private Context mContext;
    private FileSave fileGet;

    public UploadImageController(Context mContext) {
        this.mContext = mContext;
        fileGet = new FileSave(mContext, Constants.GET);
    }

    public interface OnUploadImagesListener {
        void onUploadImagesSuccess(List<String> data);

        void onErrorUploadImages();

        void onUploadImagesFailure();

        void onConnectUploadImagesFailure();
    }

    public void uploadImages(UploadImageRetro imageRetro, OnUploadImagesListener listener) {
//        HashMap<String, Object> imagesMap = imageRetro.toMap();
//
//        PostAPI postAPI = RetrofitClient.createServiceForImageUpload(PostAPI.class, fileGet.getToken());
//        Call<JSONResponse<List<String>>> call = postAPI.uploadImages(imagesMap);
//        call.enqueue(new Callback<JSONResponse<List<String>>>() {
//            @Override
//            public void onResponse(Call<JSONResponse<List<String>>> call, Response<JSONResponse<List<String>>> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getSuccess() != null) {
//                        if (response.body().getSuccess()) {
//                            listener.onUploadImagesSuccess(response.body().getData());
//                            Toast.makeText(mContext, R.string.upload_success, Toast.LENGTH_LONG).show();
//                        } else {
//                            listener.onErrorUploadImages();
//                        }
//                    } else {
//                        listener.onUploadImagesFailure();
//                    }
//                } else {
//                    listener.onUploadImagesFailure();
//                }
//                call.cancel();
//            }
//
//            @Override
//            public void onFailure(Call<JSONResponse<List<String>>> call, Throwable t) {
//                listener.onConnectUploadImagesFailure();
//                Crashlytics.logException(t);
//            }
//        });
    }
}
