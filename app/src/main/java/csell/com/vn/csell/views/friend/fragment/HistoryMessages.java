package csell.com.vn.csell.views.friend.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.List;

import csell.com.vn.csell.GlideApp;
import csell.com.vn.csell.R;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import csell.com.vn.csell.views.friend.activity.PendingMessageActivity;
import csell.com.vn.csell.views.friend.adapter.HistoryMessageAdapter;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.LastMessage;
import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;

public class HistoryMessages extends Fragment {

    private RecyclerView rvHistoryMessage;
    private DatabaseReference dbReference;
    private HistoryMessageAdapter historyMessageAdapter;
    private CoordinatorLayout layoutHistoryMessage;
    @SuppressLint("StaticFieldLeak")
    public static TextView txtPendingMessage;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout layoutPendingMess;
    private SwipeRefreshLayout loading_refreshing;
    private static CircleImageView imgAvatarPending1, imgAvatarPending2, imgAvatarPending3;
    private BaseActivityTransition baseActivityTransition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getContext(), new Crashlytics());
        dbReference = FirebaseDBUtil.getDatebase().getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_message, container, false);

        layoutPendingMess = rootView.findViewById(R.id.layout_pending_message);
        imgAvatarPending1 = rootView.findViewById(R.id.img_persion_1);
        imgAvatarPending2 = rootView.findViewById(R.id.img_persion_2);
        imgAvatarPending3 = rootView.findViewById(R.id.img_persion_3);
        txtPendingMessage = rootView.findViewById(R.id.txtPindingMessage);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        addEvent();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView(View view) {
//        imgAvatarPending1 = view.findViewById(R.id.img_persion_1);
//        imgAvatarPending2 = view.findViewById(R.id.img_persion_2);
//        imgAvatarPending3 = view.findViewById(R.id.img_persion_3);
        rvHistoryMessage = view.findViewById(R.id.rv_history_message);
        layoutHistoryMessage = view.findViewById(R.id.layout_history_message);
//        txtPendingMessage = view.findViewById(R.id.txtPindingMessage);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
//        layoutPendingMess = view.findViewById(R.id.layout_pending_message);
        rvHistoryMessage.setLayoutManager(linearLayoutManager);
        rvHistoryMessage.setHasFixedSize(true);
        loading_refreshing = view.findViewById(R.id.loading_refreshing);

        FileSave fileSave = new FileSave(getActivity(), Constants.GET);

        dbReference = FirebaseDBUtil.getDatebase().getReference();

        SnapshotParser<LastMessage> parser = snapshot -> snapshot.getValue(LastMessage.class);

        ((MainActivity) getActivity()).updateCountPendingMessage(layoutPendingMess, txtPendingMessage);

        Query query = dbReference.child(EntityFirebase.TableLastMessages).child(fileSave.getUserId())
                .orderByChild(EntityFirebase.FieldDateCreated);
        fileSave.getUserId();
        FirebaseRecyclerOptions<LastMessage> options = new FirebaseRecyclerOptions.Builder<LastMessage>()
                .setQuery(query, parser)
                .build();

        historyMessageAdapter = new HistoryMessageAdapter(getActivity(), options);
        rvHistoryMessage.setAdapter(historyMessageAdapter);
        historyMessageAdapter.startListening();
        loading_refreshing = view.findViewById(R.id.loading_refreshing);

    }

    private void addEvent() {
        loading_refreshing.setOnRefreshListener(() -> {
            if (Utilities.isNetworkConnected(MainActivity.mainContext)) {
                if (Utilities.getInetAddressByName() != null) {
                    historyMessageAdapter.notifyDataSetChanged();
                    loading_refreshing.setRefreshing(false);
                } else {
                    Snackbar.make(layoutHistoryMessage, getResources().getString(R.string.Please_check_your_network_connection),
                            Snackbar.LENGTH_LONG).show();
                    loading_refreshing.setRefreshing(false);
                }
            } else {
                Snackbar.make(layoutHistoryMessage, getResources().getString(R.string.Please_check_your_network_connection),
                        Snackbar.LENGTH_LONG).show();
                loading_refreshing.setRefreshing(false);
            }
        });
        layoutPendingMess.setOnClickListener(view -> {
            Intent mess = new Intent(getActivity(), PendingMessageActivity.class);
            startActivity(mess);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        historyMessageAdapter.stopListening();
    }


    @SuppressLint("SetTextI18n")
    public static void updateCountPendingMessage(Context context, int countPendingMessage, List<String> lstAvatar) {
        try {

            if (countPendingMessage == 0) {
                layoutPendingMess.setVisibility(View.GONE);
                txtPendingMessage.setText("");
            } else {
                layoutPendingMess.setVisibility(View.VISIBLE);
                txtPendingMessage.setText(context.getString(R.string.text_you_have) + " " + countPendingMessage + " " + context.getString(R.string.text_count_message_pending));

                if (lstAvatar.size() == 0) {
                    imgAvatarPending1.setVisibility(View.GONE);
                    imgAvatarPending2.setVisibility(View.GONE);
                    imgAvatarPending3.setVisibility(View.GONE);
                }

                if (lstAvatar.size() > 0) {
                    imgAvatarPending3.setVisibility(View.VISIBLE);
                    GlideApp.with(context)
                            .load(lstAvatar.get(0) == null ? R.drawable.ic_logo : lstAvatar.get(0))
                            .error(R.drawable.ic_logo)
                            .placeholder(R.drawable.ic_logo)
                            .into(imgAvatarPending3);
                } else {
                    imgAvatarPending3.setVisibility(View.GONE);
                }

                if (lstAvatar.size() > 1) {
                    imgAvatarPending2.setVisibility(View.VISIBLE);
                    GlideApp.with(context)
                            .load(lstAvatar.get(1) == null ? R.drawable.ic_logo : lstAvatar.get(1))
                            .error(R.drawable.ic_logo)
                            .placeholder(R.drawable.ic_logo)
                            .into(imgAvatarPending2);
                } else {
                    imgAvatarPending2.setVisibility(View.GONE);
                }

                if (lstAvatar.size() > 2) {
                    imgAvatarPending1.setVisibility(View.VISIBLE);
                    GlideApp.with(context)
                            .load(lstAvatar.get(2) == null ? R.drawable.ic_logo : lstAvatar.get(2))
                            .error(R.drawable.ic_logo)
                            .placeholder(R.drawable.ic_logo)
                            .into(imgAvatarPending1);
                } else {
                    imgAvatarPending1.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            Log.e("test_", e.toString());
        }

    }

}
