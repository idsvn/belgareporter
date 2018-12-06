package be.belga.reporter.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import be.belga.reporter.type.PostTypeEnum;
import be.belga.reporter.type.StatusTypeEnum;

/**
 * The persistent class for the posts database table.
 */
@Entity
@Table(name = "posts")
@NamedQuery(name = "Post.findAll", query = "SELECT p FROM Post p")
public class Post implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String caption;

	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date")
	private Date createDate;

	@JsonIgnore
	@Enumerated(EnumType.STRING)
	private StatusTypeEnum status;

	private String title;

	private String topic;

	private String lead;

	@Transient
	private Integer fileId;

	@Transient
	private Integer metaId;

	@Transient
	private String size;

	@Enumerated(EnumType.STRING)
	private PostTypeEnum type;

	private String body;

	// @OneToOne(mappedBy = "post", cascade = CascadeType.ALL)
	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "meta_id")
	private Metadata metadata;

	@ManyToOne
	@JoinColumn(name = "file_id")
	private FileUpload fileUpload;

	@ManyToOne
	@JoinColumn(name = "username")
	private User user;

	public Post() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer postId) {
		this.id = postId;
	}

	public String getCaption() {
		return this.caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public StatusTypeEnum getStatus() {
		return this.status;
	}

	public void setStatus(StatusTypeEnum status) {
		this.status = status;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTopic() {
		return this.topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public PostTypeEnum getType() {
		return this.type;
	}

	public void setType(PostTypeEnum type) {
		this.type = type;
	}

	public Metadata getMetadata() {
		return this.metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
		this.metadata.setPost(this);
	}

	public FileUpload getFileUpload() {
		return this.fileUpload;
	}

	public void setFileUpload(FileUpload fileUpload) {
		this.fileUpload = fileUpload;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getLead() {
		return lead;
	}

	public void setLead(String lead) {
		this.lead = lead;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Integer getFileId() {
		return fileId;
	}

	public void setFileId(Integer fileId) {
		this.fileId = fileId;
	}

	public Integer getMetaId() {
		return metaId;
	}

	public void setMetaId(Integer metaId) {
		this.metaId = metaId;
	}

}