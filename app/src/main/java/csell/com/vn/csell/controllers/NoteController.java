package csell.com.vn.csell.controllers;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.PostAPI;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.Note;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteController {
    private Context mContext;
    private FileSave fileGet;

    public NoteController(Context context) {
        this.mContext = context;
        fileGet = new FileSave(context, Constants.GET);
    }

    public void getNoteToday(int skip, int limit, String dateStart, String dateEnd, OnGetNoteTodayListener listener) {
        try {
            GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
            Call<JSONResponse<List<Note>>> getNoteToday = getAPI.getNoteToday(skip, limit, dateStart, dateEnd);

            getNoteToday.enqueue(new Callback<JSONResponse<List<Note>>>() {
                @Override
                public void onResponse(Call<JSONResponse<List<Note>>> call, Response<JSONResponse<List<Note>>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body().getSuccess() != null) {
                                listener.onGetNoteTodaySuccess(response);
                            }
                        } else {
                            listener.onGetNoteTodayFailure(response);
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                        Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                    }
                }

                @Override
                public void onFailure(Call<JSONResponse<List<Note>>> call, Throwable t) {
                    listener.onConnectGetNoteTodayFailure();
                    if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
                    Crashlytics.logException(t);
                }
            });
        } catch (Exception ignored) {

        }
    }

    public void updateNote(HashMap<String, Object> note, String id, OnUpdateNoteListener listener) {
        try {
            PostAPI api = RetrofitClient.createService(PostAPI.class, fileGet.getToken());
            Call<HashMap<String, Object>> updateNote = api.updateNote(note, id);
            updateNote.enqueue(new Callback<HashMap<String, Object>>() {
                @Override
                public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                    try {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                listener.onUpdateNoteSuccess();
                            } else {
                                listener.onErrorUpdateNote();
                            }
                        } else {
                            listener.onUpdateNoteFailure();
                            if (response.errorBody() != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    String msg = (String) jsonObject.get(Constants.ERROR);
                                    if (!TextUtils.isEmpty(msg)) {
                                        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException | IOException ignored) {

                                }
                            }
                        }
                        updateNote.cancel();
                    } catch (Exception e) {
                        listener.onConnectUpdateNoteFailure();
                        if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                        Crashlytics.logException(new Exception(Utilities.LOG_EXCEPTION + e.toString()));
                    }
                }

                @Override
                public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                    updateNote.cancel();
                    Crashlytics.logException(t);
                    if (BuildConfig.DEBUG) Log.e(getClass().getName(), t.toString());
                }
            });
        } catch (Exception ignored) {

        }
    }

    public interface OnGetNoteTodayListener {
        void onGetNoteTodaySuccess(Response<JSONResponse<List<Note>>> response);

        void onGetNoteTodayFailure(Response<JSONResponse<List<Note>>> response);

        void onConnectGetNoteTodayFailure();
    }

    public interface OnUpdateNoteListener {
        void onUpdateNoteSuccess();

        void onErrorUpdateNote();

        void onUpdateNoteFailure();

        void onConnectUpdateNoteFailure();
    }
}
