package be.belga.reporter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import be.belga.reporter.entity.UserMetadata;

public interface UserMetadataRepository extends JpaRepository<UserMetadata, Integer> {

	public UserMetadata findByUser_Username(String username);

}
