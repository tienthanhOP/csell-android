package csell.com.vn.csell.views.product.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.crashlytics.android.Crashlytics;
import java.util.ArrayList;
import java.util.List;
import csell.com.vn.csell.R;
import csell.com.vn.csell.models.NoteV1;
import csell.com.vn.csell.views.note.adapter.TimeLineAdapter;
import csell.com.vn.csell.constants.Constants;
import csell.com.vn.csell.constants.Utilities;
import csell.com.vn.csell.models.Note;
import io.fabric.sdk.android.Fabric;

public class TimeLineProductFragment extends Fragment {

    private RecyclerView rvHistoryNote;
    public static ArrayList<NoteV1> listProductNote;
    @SuppressLint("StaticFieldLeak")
    public static TimeLineAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getContext(), new Crashlytics());
        listProductNote = new ArrayList<>();

        reloadView();
    }

    public void reloadView() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            listProductNote.clear();
            listProductNote.addAll((ArrayList<NoteV1>) getArguments().getSerializable(Constants.KEY_NOTE_PRODUCT));
        }
//        initView(getView());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_timeline_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {

        try {
            rvHistoryNote = view.findViewById(R.id.rv_history_product_note);
            rvHistoryNote.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            rvHistoryNote.setLayoutManager(layoutManager);
            adapter = new TimeLineAdapter(getActivity(), listProductNote, true, 2);
            rvHistoryNote.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.ADD_NOTE_ACTIVITY_RESULT:
                if (data != null) {
                    NoteV1 note = (NoteV1) data.getSerializableExtra(Constants.KEY_PASSINGDATA_NOTE_OBJ);
                    if (note != null) {
                        listProductNote.add(note);
                        Utilities.sortListNoteV1(listProductNote);
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

//    public void requestPermission() {
//        if (getActivity() != null) {
//            int checkReadCal = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR);
//            int checkWriteCal = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR);
//            int permissionGranted = PackageManager.PERMISSION_GRANTED;
//
//            String[] permissions = new String[]{
//                    Manifest.permission.READ_CALENDAR,
//                    Manifest.permission.WRITE_CALENDAR};
//            //check Android 6+
//            if (Build.VERSION.SDK_INT >= 23) {
//                //check permission granted
//                if (checkReadCal != permissionGranted || checkWriteCal != permissionGranted) {
//                    //request Permissions
//                    ActivityCompat.requestPermissions(getActivity(), permissions, 1);
//                } else {
//                    Intent note = new Intent(getActivity(), AddNoteProductActivity.class);
//                    note.putExtra("NOTE_IN_PRODUCT", true);
//                    note.putExtra(Constants.KEY_PRODUCT_ID, fileGet.getProductIdCurrentSelect());
//                    note.putExtra(Constants.KEY_PRODUCT_NAME, fileGet.getProductNameCurrentSelect());
//                    startActivityForResult(note, Constants.ADD_NOTE_ACTIVITY_RESULT);
//                }
//            } else {
//                Intent note = new Intent(getActivity(), AddNoteProductActivity.class);
//                note.putExtra("NOTE_IN_PRODUCT", true);
//                note.putExtra(Constants.KEY_PRODUCT_ID, fileGet.getProductIdCurrentSelect());
//                note.putExtra(Constants.KEY_PRODUCT_NAME, fileGet.getProductNameCurrentSelect());
//                startActivityForResult(note, Constants.ADD_NOTE_ACTIVITY_RESULT);
//            }
//        }
//    }

    public void updateView(List<NoteV1> lsNotes) {
        listProductNote.clear();
        listProductNote.addAll(lsNotes);

        adapter = new TimeLineAdapter(getActivity(), listProductNote, true, 2);
        rvHistoryNote.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
