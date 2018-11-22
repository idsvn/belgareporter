package be.belga.reporter.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The persistent class for the file_uploads database table.
 */
@Entity
@Table(name = "file_uploads")
@NamedQuery(name = "FileUpload.findAll", query = "SELECT f FROM FileUpload f")
public class FileUpload implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "generated_name")
    private String generatedName;

    @Column(name = "generated_url")
    private String generatedUrl;

    private String mimetype;

    @Column(name = "original_name")
    private String originalName;

    private int size;

    // bi-directional many-to-one association to Post
    @JsonIgnore
    @OneToMany(mappedBy = "fileUpload")
    private List<Post> posts;

    public FileUpload() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGeneratedName() {
        return this.generatedName;
    }

    public void setGeneratedName(String generatedName) {
        this.generatedName = generatedName;
    }

    public String getGeneratedUrl() {
        return this.generatedUrl;
    }

    public void setGeneratedUrl(String generatedUrl) {
        this.generatedUrl = generatedUrl;
    }

    public String getMimetype() {
        return this.mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getOriginalName() {
        return this.originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Post> getPosts() {
        return this.posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public Post addPost(Post post) {
        getPosts().add(post);
        post.setFileUpload(this);

        return post;
    }

    public Post removePost(Post post) {
        getPosts().remove(post);
        post.setFileUpload(null);

        return post;
    }

    public FileUpload(int id) {
        super();
        this.id = id;
    }

}