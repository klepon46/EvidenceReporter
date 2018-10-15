package com.anatech.evidencereporter.database;

/**
 * Created by garya on 06/09/2018.
 */

public class EvidenceDbSchema {

    public static final class EvidenceTable{
        public static final String NAME = "evidences";


        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DESCRIPTION = "DESCRIPTION";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
            public static final String LONGITUDE = "longitude";
            public static final String LATITUDE = "latitude";

        }

    }

}
