package com.anatech.evidencereporter.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.anatech.evidencereporter.Model.ReportItem;
import com.anatech.evidencereporter.database.EvidenceDbSchema.EvidenceTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by garya on 06/09/2018.
 */

public class EvidenceCursorWrapper extends CursorWrapper {
    public EvidenceCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public ReportItem getReportITem() {
        String uuidString = getString(getColumnIndex(EvidenceTable.Cols.UUID));
        String title = getString(getColumnIndex(EvidenceTable.Cols.TITLE));
        String description = getString(getColumnIndex(EvidenceTable.Cols.DESCRIPTION));
        long date = getLong(getColumnIndex(EvidenceTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(EvidenceTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(EvidenceTable.Cols.SUSPECT));
        double longitude = getDouble(getColumnIndex(EvidenceTable.Cols.LONGITUDE));
        double latitude = getDouble(getColumnIndex(EvidenceTable.Cols.LATITUDE));

        ReportItem reportItem = new ReportItem(UUID.fromString(uuidString));
        reportItem.setTitle(title);
        reportItem.setDescription(description);
        reportItem.setDate(new Date(date));
        reportItem.setSolved(isSolved != 0);
        reportItem.setSuspect(suspect);
        reportItem.setLongitude(longitude);
        reportItem.setLatitude(latitude);

        return reportItem;
    }
}
