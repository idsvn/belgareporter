package be.belga.reporter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import be.belga.reporter.entity.Metadata;
import be.belga.reporter.entity.Post;
import be.belga.reporter.entity.User;
import be.belga.reporter.repository.PostRepository;
import be.belga.reporter.type.PostTypeEnum;
import be.belga.reporter.type.StatusTypeEnum;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class PostTests {

    @Autowired
    private PostRepository postRepository;

    @Test
    public void findAll() {
        List<Post> lst = postRepository.findAll();
        assertTrue(lst.size() > 0);
    }

    @Test
    public void testCreateFullShortPost() {
        Post post = getFullPost();
        Post result = postRepository.save(post);
        assertNotNull(result);
    }

    private Post getFullPost() {
        Post post = new Post();
        post.setCaption("Caption of Post");
        post.setCreateDate(new Date());
        post.setStatus(StatusTypeEnum.A);
        post.setTitle("Title of Short");
        post.setTopic("SHORT INFO");
        post.setType(PostTypeEnum.SHORT);

        User user = new User();
        user.setUsername("admin");
        post.setUser(user);

        Metadata metaData = new Metadata();
        metaData.setAuthor("Author");
        metaData.setCity("HCM");
        metaData.setCountry("Vietnam");
        metaData.setCredit("BELGA");
        metaData.setDistribition("Distribution");
        metaData.setEditorial("Editorial");
        metaData.setInfo("General Info");
        metaData.setIptc("04234300");
        metaData.setKeywords("Keyword");
        metaData.setLabel("SHORT Lable");
        metaData.setLanguage("FR");
        metaData.setPackage_("FOOD");
        metaData.setSource("BELGA");
        metaData.setUrgency("1");
        metaData.setPost(post);

        post.setMetadata(metaData);
        return post;
    }
}
