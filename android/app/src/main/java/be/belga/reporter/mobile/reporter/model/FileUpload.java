package be.belga.reporter.mobile.reporter.model;

import android.support.annotation.Keep;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by vinh.bui on 7/3/2018.
 */

@Keep
public class FileUpload implements Serializable {

    private long id;
    private String strId;
    private String generatedName;
    private String generatedUrl;
    private String mimetype;
    private long size;

    public static List<FileUpload> parseFromJSON(String json) {
        FileUpload[] files = new Gson().fromJson(json, FileUpload[].class);
        return new LinkedList<>(Arrays.asList(files));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStrId() {
        return strId;
    }

    public void setStrId(String strId) {
        this.strId = strId;
    }

    public String getGeneratedName() {
        return generatedName;
    }

    public void setGeneratedName(String generatedName) {
        this.generatedName = generatedName;
    }

    public String getGeneratedUrl() {
        return generatedUrl;
    }

    public void setGeneratedUrl(String generatedUrl) {
        this.generatedUrl = generatedUrl;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileUpload)) return false;
        FileUpload that = (FileUpload) o;
        return Objects.equals(strId, that.strId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(strId);
    }

    @Override
    public String toString() {
        return "FileUpload{" +
                "strId='" + strId + '\'' +
                ", generatedName='" + generatedName + '\'' +
                ", generatedUrl='" + generatedUrl + '\'' +
                ", mimetype='" + mimetype + '\'' +
                ", size=" + size +
                '}';
    }
}
