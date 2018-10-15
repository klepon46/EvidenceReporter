package com.anatech.evidencereporter.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.CircularProgressDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.anatech.evidencereporter.BuildConfig;
import com.anatech.evidencereporter.Model.ReportItem;
import com.anatech.evidencereporter.Model.ReportLab;
import com.anatech.evidencereporter.R;
import com.anatech.evidencereporter.utils.PictureUtils;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mapbox.api.staticmap.v1.MapboxStaticMap;
import com.mapbox.api.staticmap.v1.StaticMapCriteria;
import com.mapbox.api.staticmap.v1.models.StaticMarkerAnnotation;
import com.mapbox.geojson.Point;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class EvidenceFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_EVIDENCE_ID = "evidence_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String URL_STORAGE_REFERENCE = "gs://evidencereporter-5a4ef.appspot.com";
    private static final String FOLDER_STORAGE_IMG = "images";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PLACE = 1;
    private static final int REQUEST_PHOTO = 2;

    private ReportItem mReportItem;
    private EditText mTitleField;
    private EditText mDescField;
    private Button mDateButton;
    private Button mPickPlaceButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private ImageView mLocationView;
    private File mPhotoFile;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public EvidenceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        firebaseStorage = FirebaseStorage.getInstance();

        UUID uuid = (UUID) getArguments().getSerializable(ARG_EVIDENCE_ID);
        mReportItem = ReportLab.getInstance(getActivity()).getReport(uuid);
        mPhotoFile = ReportLab.getInstance(getActivity()).getPhotoFile(mReportItem);
        storageReference = firebaseStorage.getReferenceFromUrl(URL_STORAGE_REFERENCE)
                .child(FOLDER_STORAGE_IMG);
    }

    @Override
    public void onPause() {
        super.onPause();
        ReportLab.getInstance(getActivity()).updateReport(mReportItem);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_evidence, container, false);
        initializeViewElement(v);

        mTitleField.setText(mReportItem.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mReportItem.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDescField.setText(mReportItem.getDescription());
        mDescField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mReportItem.setDescription(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        updateDate();
        updatePhotoView();
        setStaticImageMap();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_menu_evidence, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_send_report:
                sendReportToServer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.report_date:
                showDatePickerDialog();
                break;
            case R.id.btn_pick_place:
                pickPlace();
                break;
            case R.id.evidence_camera:
                captureImage();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mReportItem.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
        } else if (requestCode == REQUEST_PLACE) {
            Place place = PlacePicker.getPlace(getActivity(), data);
            LatLng latLng = place.getLatLng();

            mReportItem.setLatitude(latLng.latitude);
            mReportItem.setLongitude(latLng.longitude);

            setStaticImageMap();
        }
    }

    public static EvidenceFragment newInstance(UUID uuid) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_EVIDENCE_ID, uuid);

        EvidenceFragment evidenceFragment = new EvidenceFragment();
        evidenceFragment.setArguments(bundle);
        return evidenceFragment;
    }

    private void showDatePickerDialog() {
        FragmentManager fm = getFragmentManager();
        DatePickerFragment dialog = DatePickerFragment.newInstance(mReportItem.getDate());
        dialog.setTargetFragment(EvidenceFragment.this, REQUEST_DATE);
        dialog.show(fm, DIALOG_DATE);
    }

    private void pickPlace() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(getActivity()), REQUEST_PLACE);
        } catch (GooglePlayServicesRepairableException |
                GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void captureImage() {
        Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = FileProvider.getUriForFile(getActivity(),
                BuildConfig.APPLICATION_ID + ".provider",
                mPhotoFile);
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(captureImage, REQUEST_PHOTO);
    }

    private void sendReportToServer() {
        File compr = PictureUtils.saveBitmapToFile(mPhotoFile);

        Uri uri = FileProvider.getUriForFile(getActivity(),
                BuildConfig.APPLICATION_ID + ".provider",
                compr);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),
                getActivity());
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask task = storageReference.child(mPhotoFile.getName()).putFile(uri);
        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "Data Terkirim", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeViewElement(View v) {
        mTitleField = v.findViewById(R.id.evidence_title);
        mDateButton = v.findViewById(R.id.report_date);
        mPickPlaceButton = v.findViewById(R.id.btn_pick_place);
        mPhotoButton = v.findViewById(R.id.evidence_camera);
        mPhotoView = v.findViewById(R.id.evidence_photo);
        mLocationView = v.findViewById(R.id.evidence_location_imageview);
        mDescField = v.findViewById(R.id.evidence_description);

        mDateButton.setOnClickListener(this);
        mPickPlaceButton.setOnClickListener(this);
        mPhotoButton.setOnClickListener(this);
    }

    private void updateDate() {
        mDateButton.setText(mReportItem.getDate().toString());
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),
                    getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private void setStaticImageMap() {

        if (mReportItem.getLongitude() == 0 && mReportItem.getLatitude() == 0) {
            mLocationView.setVisibility(View.GONE);
            return;
        }

        double longitude = mReportItem.getLongitude();
        double latitude = mReportItem.getLatitude();

        StaticMarkerAnnotation marker = StaticMarkerAnnotation.builder()
                .color(0, 255, 0)
                .name(StaticMapCriteria.SMALL_PIN)
                .lnglat(Point.fromLngLat(longitude, latitude))
                .build();
        List<StaticMarkerAnnotation> markers = new ArrayList<>();
        markers.add(marker);

        MapboxStaticMap staticMap = MapboxStaticMap.builder()
                .accessToken(getString(R.string.mapbox_api_key))
                .styleId(StaticMapCriteria.LIGHT_STYLE)
                .cameraPoint(Point.fromLngLat(longitude, latitude))
                .cameraZoom(11)
                .width(400)
                .height(200)
                .retina(true)
                .staticMarkerAnnotations(markers)
                .build();

        CircularProgressDrawable progress = new CircularProgressDrawable(getActivity());
        progress.setStrokeWidth(8f);
        progress.setCenterRadius(24f);
        progress.start();

        Glide.with(getActivity())
                .load(staticMap.url().toString())
                .placeholder(progress)
                .fitCenter().into(mLocationView);
    }
}
