package csell.com.vn.csell.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import csell.com.vn.csell.BuildConfig;
import csell.com.vn.csell.apis.JSONResponse;
import csell.com.vn.csell.apis.RetrofitClient;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.apis.GetAPI;
import csell.com.vn.csell.models.Project;
import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectsController {
    private Context context;
    private FileSave fileGet;
    private Project project;

    public ProjectsController(Context context) {
        this.context = context;
        fileGet = new FileSave(context, Constants.GET);
        Fabric.with(context, new Crashlytics());
        project = new Project();
    }

    public interface OnGetProjectListener {
        void onGetProjectSuccess(Project project);

        void onGetProjectFailure();

        void onConnectProjectFailure();
    }

    public interface OnGetListProjectsListener {
        void onGetListProjectsSuccess(List<Project> projects, int skip, boolean isFilter);

        void onGetListProjectsFailure();

        void onConnectListProjectsFailure();
    }

    @SuppressLint("StaticFieldLeak")
    public void getProjects(String projectId, OnGetProjectListener listener) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
                if (getAPI != null) {
                    Call<JSONResponse<List<Project>>> getDetailProject = getAPI.getDetailProject(projectId);
                    getDetailProject.enqueue(new Callback<JSONResponse<List<Project>>>() {
                        @Override
                        public void onResponse(Call<JSONResponse<List<Project>>> call, Response<JSONResponse<List<Project>>> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    JSONResponse<List<Project>> result = response.body();
                                    if (result.getSuccess() != null) {
                                        if (result.getSuccess()) {
                                            if (project != null && result.getData().size() > 0) {
                                                project = response.body().getData().get(0);
                                                listener.onGetProjectSuccess(project);
                                            }
                                        }
                                    }
                                }
                            } else {
                                listener.onGetProjectFailure();
                            }
                            getDetailProject.cancel();
                        }

                        @Override
                        public void onFailure(Call<JSONResponse<List<Project>>> call, Throwable t) {
                            listener.onConnectProjectFailure();
                            getDetailProject.cancel();
                            Crashlytics.logException(t);
                        }
                    });
                }
                return null;
            }
        }.execute();
    }

    public void getListProject(int skip, String search, String city, String district, boolean isFilter, OnGetListProjectsListener listener) {
        GetAPI getAPI = RetrofitClient.createService(GetAPI.class, fileGet.getToken());
        Call<JSONResponse<List<Project>>> getProjects = getAPI.searchProject(skip, 15, search, city, district);
        getProjects.enqueue(new Callback<JSONResponse<List<Project>>>() {
            @Override
            public void onResponse(Call<JSONResponse<List<Project>>> call, Response<JSONResponse<List<Project>>> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getSuccess() != null) {
                                if (response.body().getSuccess()) {
                                    if (response.body().getData() != null) {
                                        listener.onGetListProjectsSuccess(response.body().getData(), skip, isFilter);
                                    }
                                }
                            }
                            String msg = response.body().getError();
                            if (!TextUtils.isEmpty(msg)) {
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        listener.onGetListProjectsFailure();
                        if (response.errorBody() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                String msg = (String) jsonObject.get(Constants.ERROR);
                                if (!TextUtils.isEmpty(msg)) {
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    getProjects.cancel();
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    if (BuildConfig.DEBUG) Log.e(getClass().getName(), e.toString());
                }
            }

            @Override
            public void onFailure(Call<JSONResponse<List<Project>>> call, Throwable t) {
                listener.onConnectListProjectsFailure();
                getProjects.cancel();
            }
        });
    }
}

