package be.belga.reporter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import be.belga.reporter.entity.FileUpload;

public interface FileUploadRepository extends JpaRepository<FileUpload, Integer> {

}
