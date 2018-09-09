package com.anatech.evidencereporter.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.anatech.evidencereporter.Model.ReportItem;
import com.anatech.evidencereporter.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by garya on 27/03/2018.
 */

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    static final int REQUEST_IMG_CAPTURE = 1;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRoot = database.getReference().getRoot();

    private Button mSubmitButton, mTakePhotButton;
    private EditText mEtTitle, mEtDesc;
    private ImageView mPicPreview;

    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initializeView();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_submit:

                ReportItem reportItem = constructReportMessage();
                dbRoot.push().setValue(reportItem);

                break;

            case R.id.btn_take_photo:
                dispatchTakePictureIntent();
                break;
        }

    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //startActivityForResult(takePictureIntent, REQUEST_IMG_CAPTURE);
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException e) {

            }

            if (photoFile != null) {
                Uri photoUri = FileProvider
                        .getUriForFile(this, "com.example.android.fileprovider",
                                photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMG_CAPTURE);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMG_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mPicPreview.setImageBitmap(imageBitmap);
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "EVIDENCE_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        image.getAbsolutePath();

        mCurrentPhotoPath = image.getAbsolutePath();

        return image;

    }

    private ReportItem constructReportMessage() {

        ReportItem reportItem = new ReportItem();
        reportItem.setTitle(mEtTitle.getText().toString());
        reportItem.setDescription(mEtDesc.getText().toString());

        return reportItem;
    }


    private void initializeView() {
        mSubmitButton = findViewById(R.id.btn_submit);
        mTakePhotButton = findViewById(R.id.btn_take_photo);
        mEtTitle = findViewById(R.id.et_title);
        mEtDesc = findViewById(R.id.et_desc);
        mPicPreview = findViewById(R.id.iv_pic_preview);

        mSubmitButton.setOnClickListener(this);
        mTakePhotButton.setOnClickListener(this);
    }
}
