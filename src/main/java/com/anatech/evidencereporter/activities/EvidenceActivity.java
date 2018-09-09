package com.anatech.evidencereporter.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.anatech.evidencereporter.R;
import com.anatech.evidencereporter.fragment.EvidenceFragment;

import java.util.UUID;

public class EvidenceActivity extends SingleFragmentActivity {

    public static final String EXTRA_EVIDENCE_ID = "evidence_id";

    @Override
    protected Fragment createFragment() {
        //return new EvidenceFragment();

        UUID crimeID = (UUID) getIntent().getSerializableExtra(EXTRA_EVIDENCE_ID);
        return EvidenceFragment.newInstance(crimeID);
    }

    public static Intent newIntent(Context packageContext, UUID uuid){
        Intent intent = new Intent(packageContext,EvidenceActivity.class);
        intent.putExtra(EXTRA_EVIDENCE_ID, uuid);

        return intent;
    }
}
