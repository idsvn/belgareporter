package be.belga.reporter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import be.belga.reporter.entity.Metadata;

public interface MetadataRepository extends JpaRepository<Metadata, Integer> {

    public Metadata findByPostId(int postId);
}
