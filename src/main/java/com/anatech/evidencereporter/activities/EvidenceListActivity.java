package com.anatech.evidencereporter.activities;

import android.support.v4.app.Fragment;

import com.anatech.evidencereporter.fragment.EvidenceListFragment;

/**
 * Created by garya on 15/08/2018.
 */

public class EvidenceListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new EvidenceListFragment();
    }
}
