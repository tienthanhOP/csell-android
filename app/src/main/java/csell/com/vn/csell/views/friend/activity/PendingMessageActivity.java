package csell.com.vn.csell.views.friend.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import csell.com.vn.csell.R;
import csell.com.vn.csell.views.friend.adapter.PendingMessageAdapter;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.EntityFirebase;
import csell.com.vn.csell.constants.FileSave;
import csell.com.vn.csell.constants.FirebaseDBUtil;
import csell.com.vn.csell.models.LastMessage;
import csell.com.vn.csell.views.csell.activity.MainActivity;
import io.fabric.sdk.android.Fabric;
import mehdi.sakout.fancybuttons.FancyButton;

public class PendingMessageActivity extends AppCompatActivity {

    private FancyButton btnBack;
    private PendingMessageAdapter mAdapter;
    public Query query;
    public FirebaseRecyclerOptions<LastMessage> options;
    public SnapshotParser<LastMessage> parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_message);
        Fabric.with(this, new Crashlytics());

        initView();
        addEvent();
    }

    private void initView() {
        DatabaseReference mFirebaseRef = FirebaseDBUtil.getDatebase().getReference();
        btnBack = findViewById(R.id.btn_back_navigation);
        btnBack.setText(getString(R.string.title_back_vn));
        TextView tvTitleToolbar = findViewById(R.id.custom_TitleToolbar);
        tvTitleToolbar.setText(MainActivity.mainContext.getResources().getString(R.string.title_pending_message));
        FileSave fileGet = new FileSave(this, Constants.GET);
        RecyclerView rvList = findViewById(R.id.rv_pending_message);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        query = mFirebaseRef.child(EntityFirebase.TableLastMessages).child(fileGet.getUserId())
                .orderByChild(EntityFirebase.FieldIsPending).equalTo(true);

        parser = snapshot -> snapshot.getValue(LastMessage.class);

        options = new FirebaseRecyclerOptions.Builder<LastMessage>()
                .setQuery(query, parser)
                .build();

        mAdapter = new PendingMessageAdapter(this, options);
        mAdapter.startListening();
        rvList.setAdapter(mAdapter);

    }

    private void addEvent() {
        btnBack.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.stopListening();
    }
}
