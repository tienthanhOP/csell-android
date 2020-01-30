package csell.com.vn.csell.views.customer.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import csell.com.vn.csell.R;
import csell.com.vn.csell.views.customer.activity.ContactCustomerDetailActivity;
import csell.com.vn.csell.views.note.adapter.TimeLineAdapter;

//import com.google.firebase.firestore.DocumentChange;
//import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by chuc.nq on 3/16/2018.
 */

public class TimeLineCustomerFragment extends Fragment {

    public static LinearLayout layoutAddNote;
    public static RecyclerView rvHistoryNote;
    public TimeLineAdapter adapter;
    private View root;
    private ProgressBar progressBar;
    private NestedScrollView layoutContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_time_line_customer, container, false);
        initView(rootView);
        setupWindowAnimations();
        addEvent();
        return rootView;
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(getActivity()).inflateTransition(R.transition.fade);
        getActivity().getWindow().setEnterTransition(transition);
    }

    private void addEvent() {
        layoutAddNote.setOnClickListener(v -> {
            ((ContactCustomerDetailActivity) getActivity()).createNote();
        });
    }

    private void hideProgressBar(boolean b) {
        if (b) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                progressBar.setVisibility(View.GONE);
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                layoutContent.setAnimation(animation);
                layoutContent.setVisibility(View.VISIBLE);
            }, 500);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            layoutContent.setVisibility(View.GONE);
        }
    }

    private void initView(View rootView) {
        progressBar = rootView.findViewById(R.id.progress_bar);
        layoutContent = rootView.findViewById(R.id.layout_content);
        hideProgressBar(false);

        layoutAddNote = rootView.findViewById(R.id.layout_add_note);
//        layoutAddNote.setVisibility(View.GONE);
        rvHistoryNote = rootView.findViewById(R.id.rv_history_customer_note);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvHistoryNote.setLayoutManager(layoutManager);
        rvHistoryNote.setHasFixedSize(true);
        adapter = new TimeLineAdapter(getActivity(), ContactCustomerDetailActivity.dataNotes, false, 3);
        rvHistoryNote.setAdapter(adapter);
    }

//    public void scrollToDateNow() {
//        if (listProductNote == null) return;
//        for (int i = 0; i < listProductNote.size(); i++) {
//            Long time = System.currentTimeMillis();
//            if (time - listProductNote.get(i).DateReminder <= 0) {
//                rvHistoryNote.getLayoutManager().scrollToPosition(i - 1);
//                break;
//            }
//        }
//    }

    public void updateViewTimeLine() {
        try {
            if (adapter == null || rvHistoryNote == null) {
                rvHistoryNote = root.findViewById(R.id.rv_history_customer_note);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                rvHistoryNote.setLayoutManager(layoutManager);
                rvHistoryNote.setHasFixedSize(true);
                adapter = new TimeLineAdapter(getActivity(), ContactCustomerDetailActivity.dataNotes, false, 3);
                rvHistoryNote.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
            hideProgressBar(true);
        } catch (Exception ignored) {
            ignored.getMessage();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (ContactCustomerDetailActivity.dataNotes.size() == 0) {
//            layoutAddNote.setVisibility(View.VISIBLE);
//            ContactCustomerDetailActivity.fab.hide();
//            rvHistoryNote.setVisibility(View.GONE);
//        } else {
//            layoutAddNote.setVisibility(View.GONE);
//            ContactCustomerDetailActivity.fab.show();
//            rvHistoryNote.setVisibility(View.VISIBLE);
//        }
    }
}
