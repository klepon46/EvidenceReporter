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
        long date = getLong(getColumnIndex(EvidenceTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(EvidenceTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(EvidenceTable.Cols.SUSPECT));

        ReportItem reportItem = new ReportItem(UUID.fromString(uuidString));
        reportItem.setTitle(title);
        reportItem.setDate(new Date(date));
        reportItem.setSolved(isSolved != 0);
        reportItem.setSuspect(suspect);

        return reportItem;
    }
}
