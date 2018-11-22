package be.belga.reporter.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import be.belga.reporter.type.GenderTypeEnum;
import be.belga.reporter.type.RoleTypeEnum;
import be.belga.reporter.type.StatusTypeEnum;

/**
 * The persistent class for the users database table.
 */
@Entity
@Table(name = "users")
@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String username;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;

    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Enumerated(EnumType.STRING)
    private GenderTypeEnum gender;

    private String language;

    @Column(name = "last_name")
    private String lastName;

    @JsonIgnore
    private String password;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private RoleTypeEnum role;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private StatusTypeEnum status;

    // bi-directional many-to-one association to Post
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;

    // bi-directional one-to-one association to UserMetadata
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = false)
    @PrimaryKeyJoinColumn
    private UserMetadata userMetadata;

    public User() {
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public GenderTypeEnum getGender() {
        return this.gender;
    }

    public void setGender(GenderTypeEnum gender) {
        this.gender = gender;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleTypeEnum getRole() {
        return this.role;
    }

    public void setRole(RoleTypeEnum role) {
        this.role = role;
    }

    public StatusTypeEnum getStatus() {
        return this.status;
    }

    public void setStatus(StatusTypeEnum status) {
        this.status = status;
    }

    public List<Post> getPosts() {
        return this.posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public Post addPost(Post post) {
        getPosts().add(post);
        post.setUser(this);

        return post;
    }

    public Post removePost(Post post) {
        getPosts().remove(post);
        post.setUser(null);

        return post;
    }

    public UserMetadata getUserMetadata() {
        return this.userMetadata;
    }

    public void setUserMetadata(UserMetadata userMetadata) {
        this.userMetadata = userMetadata;
    }

}