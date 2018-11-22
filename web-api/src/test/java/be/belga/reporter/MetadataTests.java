package be.belga.reporter;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import be.belga.reporter.entity.Metadata;
import be.belga.reporter.repository.MetadataRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MetadataTests {

    @Autowired
    private MetadataRepository metadataRepository;

    @Test
    public void findAll() {
        List<Metadata> lst = metadataRepository.findAll();

        System.out.println(lst.size());
    }

}
