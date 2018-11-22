package be.belga.reporter.mobile.reporter.model;

import android.support.annotation.Keep;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by vinh.bui on 6/18/2018.
 */

@Keep
public class Metadata implements Serializable {

    private String id;
    private String postId;
    private String author;
    private String city;
    private String country;
    private String credit;
    private String distribition;
    private String editorial;
    private String info;
    private String iptc;
    private String keywords;
    private String label;
    private String language;
    private String package_;
    private String source;
    private String status;
    private String urgency;
    private String comment;

    public static List<Metadata> parseAllFromJSON(String json) {
        Metadata[] metadata = new Gson().fromJson(json, Metadata[].class);
        return new LinkedList<>(Arrays.asList(metadata));
    }

    public static Metadata parseFromJSON(String json) {
        Metadata metadata = new Gson().fromJson(json, Metadata.class);
        return metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getDistribition() {
        return distribition;
    }

    public void setDistribition(String distribition) {
        this.distribition = distribition;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getIptc() {
        return iptc;
    }

    public void setIptc(String iptc) {
        this.iptc = iptc;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPackage_() {
        return package_;
    }

    public void setPackage_(String package_) {
        this.package_ = package_;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Metadata)) return false;
        Metadata metadata = (Metadata) o;
        return Objects.equals(id, metadata.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "id='" + id + '\'' +
                ", postId='" + postId + '\'' +
                ", author='" + author + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", credit='" + credit + '\'' +
                ", distribition='" + distribition + '\'' +
                ", editorial='" + editorial + '\'' +
                ", info='" + info + '\'' +
                ", iptc='" + iptc + '\'' +
                ", keywords='" + keywords + '\'' +
                ", label='" + label + '\'' +
                ", language='" + language + '\'' +
                ", package_='" + package_ + '\'' +
                ", source='" + source + '\'' +
                ", status='" + status + '\'' +
                ", urgency='" + urgency + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
