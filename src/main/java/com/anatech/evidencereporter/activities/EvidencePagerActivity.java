package com.anatech.evidencereporter.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.anatech.evidencereporter.Model.ReportItem;
import com.anatech.evidencereporter.Model.ReportLab;
import com.anatech.evidencereporter.R;
import com.anatech.evidencereporter.fragment.EvidenceFragment;

import java.util.List;
import java.util.UUID;

/**
 * Created by garya on 22/08/2018.
 */

public class EvidencePagerActivity extends AppCompatActivity {
    public static final String EXTRA_EVIDENCE_ID = "evidence_id";
    private ViewPager mViewPager;
    private List<ReportItem> mReports;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evidence_pager);

        UUID crimeID = (UUID) getIntent().getSerializableExtra(EXTRA_EVIDENCE_ID);

        mReports = ReportLab.getInstance(this).getReports();
        mViewPager = findViewById(R.id.activity_evidence_view_pager);

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                ReportItem reportItem = mReports.get(position);
                return EvidenceFragment.newInstance(reportItem.getId());
            }

            @Override
            public int getCount() {
                return mReports.size();
            }
        });

        for (int i = 0; i < mReports.size(); i++) {
            if (mReports.get(i).getId().equals(crimeID)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    public static Intent newIntent(Context packageContext, UUID uuid) {
        Intent intent = new Intent(packageContext, EvidencePagerActivity.class);
        intent.putExtra(EXTRA_EVIDENCE_ID, uuid);

        return intent;
    }
}
