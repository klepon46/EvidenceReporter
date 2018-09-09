package com.anatech.evidencereporter.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.anatech.evidencereporter.BuildConfig;
import com.anatech.evidencereporter.Model.ReportItem;
import com.anatech.evidencereporter.Model.ReportLab;
import com.anatech.evidencereporter.R;
import com.anatech.evidencereporter.utils.PictureUtils;

import java.io.File;
import java.util.Date;
import java.util.UUID;


public class EvidenceFragment extends Fragment {
    private static final String ARG_EVIDENCE_ID = "evidence_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;

    private ReportItem mReportItem;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;

    public EvidenceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID uuid = (UUID) getArguments().getSerializable(ARG_EVIDENCE_ID);
        mReportItem = ReportLab.getInstance(getActivity()).getReport(uuid);
        mPhotoFile = ReportLab.getInstance(getActivity()).getPhotoFile(mReportItem);
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

        mTitleField = v.findViewById(R.id.evidence_title);
        mDateButton = v.findViewById(R.id.report_date);
        mSolvedCheckBox = v.findViewById(R.id.report_solved);

        mTitleField.setText(mReportItem.getTitle());
        mSolvedCheckBox.setChecked(mReportItem.isSolved());

        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mReportItem.getDate());
                dialog.setTargetFragment(EvidenceFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });


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

        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mReportItem.setSolved(b);
            }
        });

        mReportButton = v.findViewById(R.id.btn_evidence_send_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getEvidenceReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.evidence_report_suspect));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });


        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = v.findViewById(R.id.btn_evidence_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mReportItem.getSuspect() != null) {
            mSuspectButton.setText(mReportItem.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                packageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = v.findViewById(R.id.evidence_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });


        mPhotoView = v.findViewById(R.id.evidence_photo);
        updatePhotoView();

        return v;
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
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();

            String[] queryFields = {ContactsContract.Contacts.DISPLAY_NAME};

            Cursor cursor = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            try {
                if (cursor.getCount() == 0)
                    return;

                cursor.moveToFirst();
                String suspect = cursor.getString(0);
                mReportItem.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                cursor.close();
            }

        } else if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
        }
    }

    public static EvidenceFragment newInstance(UUID uuid) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_EVIDENCE_ID, uuid);

        EvidenceFragment evidenceFragment = new EvidenceFragment();
        evidenceFragment.setArguments(bundle);
        return evidenceFragment;
    }

    private void updateDate() {
        mDateButton.setText(mReportItem.getDate().toString());
    }

    private String getEvidenceReport() {
        String solvedString = null;
        if (mReportItem.isSolved()) {
            solvedString = getString(R.string.evidence_report_solved);
        } else {
            solvedString = getString(R.string.evidence_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mReportItem.getDate()).toString();

        String suspect = mReportItem.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.evidence_report_no_suspect);
        } else {
            suspect = getString(R.string.evidence_report_suspect);
        }

        String report = getString(R.string.evidence_report,
                mReportItem.getTitle(), dateString, solvedString, suspect);

        return report;

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

}
