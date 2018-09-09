package com.anatech.evidencereporter.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.anatech.evidencereporter.Model.ReportItem;
import com.anatech.evidencereporter.Model.ReportLab;
import com.anatech.evidencereporter.R;
import com.anatech.evidencereporter.activities.EvidenceActivity;
import com.anatech.evidencereporter.activities.EvidencePagerActivity;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by garya on 15/08/2018.
 */

public class EvidenceListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private EvidenceAdapter mEvidenceAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evidence_list, container, false);
        mRecyclerView = view.findViewById(R.id.evidence_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_report_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_report:
                ReportItem reportItem = new ReportItem();
                ReportLab.getInstance(getActivity()).addReport(reportItem);
                Intent intent = EvidencePagerActivity.newIntent(getActivity(), reportItem.getId());
                startActivity(intent);
                return true;
            case R.id.menu_item_show_subtitle:
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void updateSubtitle() {
        ReportLab reportLab = ReportLab.getInstance(getActivity());
        int reportCount = reportLab.getReports().size();
        String subtitle = getString(R.string.subtitle_format, reportCount);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        ReportLab reportLab = ReportLab.getInstance(getActivity());
        List<ReportItem> reportItems = reportLab.getReports();

        if (mEvidenceAdapter == null) {
            mEvidenceAdapter = new EvidenceAdapter(reportLab.getReports());
            mRecyclerView.setAdapter(mEvidenceAdapter);
        } else {
            mEvidenceAdapter.setReports(reportItems);
            mEvidenceAdapter.notifyDataSetChanged();
        }

    }

    private class EvidenceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;
        private ReportItem mReportItem;


        public EvidenceHolder(View itemView) {
            super(itemView);

            mTitleTextView = itemView.findViewById(R.id.list_item_evidence_title_text_view);
            mDateTextView = itemView.findViewById(R.id.list_item_evidence_date_text_view);
            mSolvedCheckBox = itemView.findViewById(R.id.list_item_evidence_solved_check_box);
            itemView.setOnClickListener(this);
        }

        public void bindEvidence(ReportItem item) {
            mReportItem = item;
            mTitleTextView.setText(item.getTitle());
            mDateTextView.setText(item.getDate().toString());
            mSolvedCheckBox.setChecked(item.isSolved());
        }

        @Override
        public void onClick(View view) {
            Intent intent = EvidencePagerActivity.newIntent(getActivity(), mReportItem.getId());
            startActivity(intent);
        }
    }

    private class EvidenceAdapter extends RecyclerView.Adapter<EvidenceHolder> {

        private List<ReportItem> mReports;

        public EvidenceAdapter(List<ReportItem> reports) {
            mReports = reports;
        }

        @Override
        public EvidenceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_evidence, parent, false);


            return new EvidenceHolder(view);
        }

        @Override
        public void onBindViewHolder(EvidenceHolder holder, int position) {
            ReportItem item = mReports.get(position);
            holder.bindEvidence(item);

        }

        @Override
        public int getItemCount() {
            return mReports.size();
        }

        public void setReports(List<ReportItem> reports) {
            mReports = reports;
        }

    }
}
