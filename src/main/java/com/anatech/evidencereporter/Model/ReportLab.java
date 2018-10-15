package com.anatech.evidencereporter.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.anatech.evidencereporter.database.EvidenceBaseHelper;
import com.anatech.evidencereporter.database.EvidenceCursorWrapper;
import com.anatech.evidencereporter.database.EvidenceDbSchema;
import com.anatech.evidencereporter.database.EvidenceDbSchema.EvidenceTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by garya on 14/08/2018.
 */

public class ReportLab {

    private static ReportLab reportLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static ReportLab getInstance(Context context) {
        if (reportLab == null) {
            reportLab = new ReportLab(context);
        }

        return reportLab;
    }

    private ReportLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new EvidenceBaseHelper(mContext).getWritableDatabase();

    }

    public void addReport(ReportItem reportItem) {
        ContentValues values = getContentValues(reportItem);
        mDatabase.insert(EvidenceTable.NAME, null, values);
    }

    public List<ReportItem> getReports() {
        List<ReportItem> reports = new ArrayList<>();

        EvidenceCursorWrapper cursor = queryReports(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                reports.add(cursor.getReportITem());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return reports;
    }

    public ReportItem getReport(UUID id) {
        EvidenceCursorWrapper cursor = queryReports(
                EvidenceTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0)
                return null;

            cursor.moveToFirst();
            return cursor.getReportITem();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(ReportItem reportItem) {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, reportItem.getPhotoFilename());
    }

    public void updateReport(ReportItem reportItem) {
        String uuidString = reportItem.getId().toString();
        ContentValues values = getContentValues(reportItem);

        mDatabase.update(EvidenceTable.NAME,
                values,
                EvidenceTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    private static ContentValues getContentValues(ReportItem reportItem) {
        ContentValues values = new ContentValues();
        values.put(EvidenceTable.Cols.UUID, reportItem.getId().toString());
        values.put(EvidenceTable.Cols.TITLE, reportItem.getTitle());
        values.put(EvidenceTable.Cols.DESCRIPTION, reportItem.getDescription());
        values.put(EvidenceTable.Cols.DATE, reportItem.getDate().getTime());
        values.put(EvidenceTable.Cols.LONGITUDE, reportItem.getLongitude());
        values.put(EvidenceTable.Cols.LATITUDE, reportItem.getLatitude());
        values.put(EvidenceTable.Cols.SOLVED, reportItem.isSolved() ? 1 : 0);

        return values;
    }

    private EvidenceCursorWrapper queryReports(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                EvidenceTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new EvidenceCursorWrapper(cursor);
    }
}
