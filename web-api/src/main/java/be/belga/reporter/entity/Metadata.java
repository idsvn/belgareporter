package be.belga.reporter.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The persistent class for the metadata database table.
 */
@Entity
@NamedQuery(name = "Metadata.findAll", query = "SELECT m FROM Metadata m")
public class Metadata implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

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

	@Column(name = "package")
	private String package_;

	private String source;

	private String urgency;

	private String comment;

	private String status;

	@JsonIgnore
	// @MapsId
	// @OneToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "id")
	@OneToOne(mappedBy = "metadata", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
	private Post post;

	public Metadata() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCredit() {
		return this.credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public String getDistribition() {
		return this.distribition;
	}

	public void setDistribition(String distribition) {
		this.distribition = distribition;
	}

	public String getEditorial() {
		return this.editorial;
	}

	public void setEditorial(String editorial) {
		this.editorial = editorial;
	}

	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getIptc() {
		return this.iptc;
	}

	public void setIptc(String iptc) {
		this.iptc = iptc;
	}

	public String getKeywords() {
		return this.keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPackage_() {
		return this.package_;
	}

	public void setPackage_(String package_) {
		this.package_ = package_;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getUrgency() {
		return this.urgency;
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}

	public Post getPost() {
		return this.post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}