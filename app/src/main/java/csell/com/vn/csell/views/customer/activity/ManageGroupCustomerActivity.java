package csell.com.vn.csell.views.customer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
//import com.google.firebase.firestore.DocumentChange;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import csell.com.vn.csell.R;
import csell.com.vn.csell.models.GroupCustomerRetroV1;
import csell.com.vn.csell.views.BaseActivityTransition;
import csell.com.vn.csell.views.customer.adapter.ManageGroupFirebaseAdapter;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.interfaces.OnStartDragListener;
import csell.com.vn.csell.commons.SimpleItemTouchHelperCallback;
import csell.com.vn.csell.views.customer.fragment.CustomersFragment;
import csell.com.vn.csell.models.GroupCustomerRetro;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;

public class ManageGroupCustomerActivity extends AppCompatActivity implements OnStartDragListener {

    private LinearLayout btnCreate;
    private FancyButton btnBack, btnSave;
    private ManageGroupFirebaseAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private ArrayList<GroupCustomerRetroV1> listGroup;
    public static List<Integer> list = Arrays.asList(0, 1, 2);

    private TextView tvTitle;
    private int mPosition = -1;
    private BaseActivityTransition baseActivityTransition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_group);
        Fabric.with(this, new Crashlytics());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_UPDATE_CUSTOMER);
        registerReceiver(broadcastReceiver, intentFilter);
        initView();
        setupWindowAnimations();
        addEvent();
        getGroups();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fade);
        getWindow().setEnterTransition(transition);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void getGroups() {

        if (CustomersFragment.listGroup.size() != 0) {
            listGroup.addAll(CustomersFragment.listGroup);
        }

    }

    private void initView() {
        baseActivityTransition = new BaseActivityTransition(this);

        btnCreate = findViewById(R.id.btn_create_group);
        btnBack = findViewById(R.id.btn_back_navigation);
        btnBack.setText(getString(R.string.title_back_vn));
        btnSave = findViewById(R.id.btn_save_navigation);
        RecyclerView rvGroup = findViewById(R.id.rv_list_group);
        rvGroup.setLayoutManager(new LinearLayoutManager(this));

        listGroup = new ArrayList<>();
        mAdapter = new ManageGroupFirebaseAdapter(this, listGroup, this);
        rvGroup.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(this, mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(rvGroup);
        tvTitle = findViewById(R.id.custom_TitleToolbar);
        tvTitle.setText(getString(R.string.title_manage_group));
    }

    private void addEvent() {
        btnCreate.setOnClickListener(v -> {
            baseActivityTransition.transitionTo(new Intent(this, AddGroupCustomerActivity.class), 0);
            finishAfterTransition();
        });

        btnBack.setOnClickListener(v -> onBackPressed());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder != null) {
            mItemTouchHelper.startDrag(viewHolder);
        }
        mPosition = position;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mPosition != -1) {
                listGroup.remove(mPosition);
                mAdapter.notifyDataSetChanged();
            }
        }
    };
}
