package be.belga.reporter.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import be.belga.reporter.entity.Post;

public interface PostRepository extends JpaRepository<Post, Integer> {

	Optional<Post> findByMetadata_Id(Integer metaId);
}
