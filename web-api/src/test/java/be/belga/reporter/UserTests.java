package be.belga.reporter;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import be.belga.reporter.entity.User;
import be.belga.reporter.entity.UserMetadata;
import be.belga.reporter.repository.UserRepository;
import be.belga.reporter.type.GenderTypeEnum;
import be.belga.reporter.type.RoleTypeEnum;
import be.belga.reporter.type.StatusTypeEnum;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class UserTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	// @Ignore
	@Rollback(false)
	public void login() {

		User user = userRepository.findByUsernameAndPassword("admin", "123456");

		System.out.println();
	}

	@Test
	@Ignore
	public void findAll() {
		List<User> lst = userRepository.findAll();

		System.out.println(lst.size());
	}

	@Test
	@Ignore
	public void createFullUser() {
		User user = getFullUser();
		User result = userRepository.save(user);
		assertNotNull(result);
		assertNotNull(result.getUserMetadata());
	}

	private User getFullUser() {
		User user = new User();
		user.setCreateDate(new Date());
		user.setEmail("minh.vo@idsolutions.com.vn");
		user.setFirstName("Minh");
		user.setLastName("Vo");
		user.setLanguage("FR");
		user.setPassword("123456");
		user.setRole(RoleTypeEnum.ADMIN);
		user.setStatus(StatusTypeEnum.A);
		user.setUsername("admin");
		user.setGender(GenderTypeEnum.MALE);

		UserMetadata metadata = new UserMetadata();
		metadata.setAuthor("Author");
		metadata.setCity("HCM");
		metadata.setCountry("Vietnam");
		metadata.setCredit("BELGA");
		metadata.setDistribition("Distribution");
		metadata.setEditorial("Editorial");
		metadata.setInfo("General Info");
		metadata.setIptc("04234300");
		metadata.setKeywords("Keyword");
		metadata.setLabel("SHORT Lable");
		metadata.setLanguage("FR");
		metadata.setPackage_("FOOD");
		metadata.setSource("BELGA");
		metadata.setUrgency("1");
		// metadata.setUser(user);
		// metadata.setUsername(user.getUsername());
		user.setUserMetadata(metadata);

		return user;
	}

}
