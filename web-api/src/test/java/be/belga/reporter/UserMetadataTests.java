package be.belga.reporter;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import be.belga.reporter.entity.UserMetadata;
import be.belga.reporter.repository.UserMetadataRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMetadataTests {

	@Autowired
	private UserMetadataRepository userMetadataRepository;

	@Test
	@Rollback(false)
	public void findAll() {

		List<UserMetadata> lst = userMetadataRepository.findAll();

		System.out.println(lst.size());
	}

}
