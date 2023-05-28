package com.example.upbmaps;

public class ClassInfo {
    private String subject;
    private String className;
    private String places;
    private String hourFrom;
    private String hourTo;
    private String date;
    private String pdfUrl;
    private String classId;


    public ClassInfo() {
    }

    public ClassInfo(String subject, String className, String places,String hourFrom, String hourTo, String date, String pdfUrl) {
        this.subject = subject;
        this.className = className;
        this.places = places;
        this.hourFrom = hourFrom;
        this.hourTo = hourTo;
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassPlaces() {
        return places;
    }

    public void setClassPlaces(String places) {
        this.places = places;
    }

    public String getHourFrom() {
        return hourFrom;
    }

    public void setHourFrom(String hourFrom) {
        this.hourFrom = hourFrom;
    }

    public String getHourTo() {
        return hourTo;
    }

    public void setHourTo(String hourTo) {
        this.hourTo = hourTo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }
    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }
}
