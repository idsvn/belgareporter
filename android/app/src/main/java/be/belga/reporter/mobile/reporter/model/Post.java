package be.belga.reporter.mobile.reporter.model;

import android.support.annotation.Keep;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import belga.be.belgareporter.R;

/**
 * Created by vinh.bui on 6/12/2018.
 */

@Keep
public class Post implements Serializable {

    private String id;
    private String topic;
    private String title;
    private String caption;
    private String lead;
    private String body;
    private PostWorkflowStatus workflowStatus = PostWorkflowStatus.NEW;
    private Long createDate = new Date().getTime();
    private String type = PostType.SHORT.getStatus();
    private Metadata metadata = new Metadata();
    private User user;
    private FileUpload fileUpload;

    public static List<Post> parseAllFromJSON(String json) {
        Post[] posts = new Gson().fromJson(json, Post[].class);
        return posts != null && posts.length > 0 ? new LinkedList<Post>(Arrays.asList(posts)) : new ArrayList<Post>();
    }

    public static Post parseFromJSON(String json) {
        Post post = new Gson().fromJson(json, Post.class);
        return post != null ? post : null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getLead() {
        return lead;
    }

    public void setLead(String lead) {
        this.lead = lead;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public PostWorkflowStatus getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(PostWorkflowStatus workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public PostType getType() {
        return PostType.valueOf(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public FileUpload getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", topic='" + topic + '\'' +
                ", title='" + title + '\'' +
                ", caption='" + caption + '\'' +
                ", lead='" + lead + '\'' +
                ", body='" + body + '\'' +
                ", workflowStatus=" + workflowStatus +
                ", createDate=" + createDate +
                ", type='" + type + '\'' +
                '}';
    }

    @Keep
    public enum PostWorkflowStatus {
        NEW("NEW", "New", R.mipmap.status_new_icon, R.color.chinook),
        IN_PROGRESS("IN PROGRESS", "In Progress", R.mipmap.status_uploading_icon, R.color.golden_poppy),
        PUBLISHED("PUBLISHED", "Published", R.mipmap.status_published_icon, R.color.summer_sky),
        FAILED("FAILED", "Failed", R.mipmap.status_failed_icon, R.color.dark_orange);

        private String status;
        private String fullStatus;
        private int iconResource;
        private int colorResource;

        PostWorkflowStatus(String status, String fullStatus, int iconResource, int colorResource) {
            this.status = status;
            this.fullStatus = fullStatus;
            this.iconResource = iconResource;
            this.colorResource = colorResource;
        }

        public String getStatus() {
            return status;
        }

        public String getFullStatus() {
            return fullStatus;
        }

        public int getIconResource() {
            return iconResource;
        }

        public int getColorResource() {
            return colorResource;
        }
    }

    @Keep
    public enum PostType {
        PICTURE("PICTURE", "Picture"),
        SHORT("SHORT", "Short"),
        ALERT("ALERT", "Alert"),
        VIDEO("VIDEO", "Video"),
        AUDIO("AUDIO", "Audio");

        private String status;
        private String fullStatus;

        PostType(String status, String fullStatus) {
            this.status = status;
            this.fullStatus = fullStatus;
        }

        public String getStatus() {
            return status;
        }

        public String getFullStatus() {
            return fullStatus;
        }

    }
}
