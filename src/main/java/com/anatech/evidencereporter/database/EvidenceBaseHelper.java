package com.anatech.evidencereporter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.anatech.evidencereporter.database.EvidenceDbSchema.EvidenceTable;

/**
 * Created by garya on 06/09/2018.
 */

public class EvidenceBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "evidenceBase.db";

    public EvidenceBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + EvidenceTable.NAME + "(" +
                "id integer primary key autoincrement, " +
                EvidenceTable.Cols.UUID + ", " +
                EvidenceTable.Cols.TITLE + ", " +
                EvidenceTable.Cols.DESCRIPTION + ", " +
                EvidenceTable.Cols.DATE + ", " +
                EvidenceTable.Cols.SOLVED + ", " +
                EvidenceTable.Cols.LATITUDE + ", " +
                EvidenceTable.Cols.LONGITUDE + ", " +
                EvidenceTable.Cols.SUSPECT + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
