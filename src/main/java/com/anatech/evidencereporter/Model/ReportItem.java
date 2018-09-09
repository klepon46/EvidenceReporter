package com.anatech.evidencereporter.Model;

import java.util.Date;
import java.util.UUID;

/**
 * Created by garya on 23/04/2018.
 */

public class ReportItem {

    private UUID id;
    private String title;
    private String description;
    private Date date;
    private boolean solved;
    private String mSuspect;

    public ReportItem() {

        this(UUID.randomUUID());
    }

    public ReportItem(UUID id) {
        this.id = id;
        date = new Date();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        if (date == null)
            date = new Date();
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String mSuspect) {
        this.mSuspect = mSuspect;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
