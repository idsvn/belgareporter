package be.belga.reporter;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import be.belga.reporter.entity.FileUpload;
import be.belga.reporter.repository.FileUploadRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileUploadTests {

    @Autowired
    private FileUploadRepository uploadRepository;

    @Test
    public void findAll() {
        List<FileUpload> lst = uploadRepository.findAll();

        System.out.println(lst.size());
    }

}
