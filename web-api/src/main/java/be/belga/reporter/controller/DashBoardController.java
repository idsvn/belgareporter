package be.belga.reporter.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import be.belga.reporter.entity.Post;
import be.belga.reporter.entity.RestResponse;
import be.belga.reporter.repository.FileUploadRepository;
import be.belga.reporter.repository.PostRepository;
import be.belga.reporter.type.PostTypeEnum;

@Controller
@RequestMapping(value = "/dashboard")
public class DashBoardController {

	@Autowired
	PostRepository postRepository;

	@Autowired
	FileUploadRepository fileUploadRepository;

	@GetMapping(value = { "" })
	public String index(Model model, HttpServletRequest request) {

		List<Post> posts = postRepository.findAll(new Sort(Sort.Direction.ASC, "id"));

		for (Post post : posts) {
			if (post.getFileUpload() != null) {
				int byteSize = post.getFileUpload().getSize();
				if (post.getFileUpload().getSize() > 0) {
					post.setSize(humanReadableByteCount(byteSize, true));
				}
			}

		}

		String contextPath = "http://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath();

		model.addAttribute("contextPath", contextPath);
		model.addAttribute("lstPost", posts);
		model.addAttribute("objPost", new Post());
		model.addAttribute("PostEnumVideo", PostTypeEnum.VIDEO);
		model.addAttribute("PostEnumAudio", PostTypeEnum.AUDIO);

		return "home";
	}

	@GetMapping("/edit/{idPost:[\\d]+}")
	public String eit(@PathVariable(value = "idPost") Integer idPost, Model model, HttpServletRequest request) {

		if (idPost != null && idPost > 0) {
			Optional<Post> postOptional = postRepository.findById(idPost);
			if (postOptional.isPresent()) {
				Post post = postOptional.get();
				if(post.getFileUpload() != null) {
					int byteSize = post.getFileUpload().getSize();
					if (post.getFileUpload().getSize() > 0) {
						post.setSize(humanReadableByteCount(byteSize, true));
					}

					String contextPath = "http://" + request.getServerName() + ":" + request.getServerPort()
							+ request.getContextPath();

					model.addAttribute("contextPath", contextPath);
					
				}
				
				model.addAttribute("objPost", post);
				return "editPost";
			}
		}

		return "redirect:/dashboard";
	}

	@PostMapping("/delete")
	public ResponseEntity<RestResponse<String>> delete(@RequestParam(value = "idPost") Integer idPost, Model model) {

		if (idPost != null && idPost > 0) {

			postRepository.deleteById(idPost);

			return new ResponseEntity<>(new RestResponse<>(HttpStatus.OK.value(), "Delete Successful!!"),
					HttpStatus.OK);
		}

		return new ResponseEntity<>(new RestResponse<>(HttpStatus.NOT_FOUND.value(), "Delete Not Successful"),
				HttpStatus.NOT_FOUND);
	}

	@PostMapping("/update")
	public String updatePost(@ModelAttribute("objPost") Post post) {

		if (postRepository.findById(post.getId()).isPresent()) {

			Post postTemp = postRepository.findById(post.getId()).get();

			postTemp.setTitle(post.getTitle());
			postTemp.setTopic(post.getTopic());
			postTemp.setLead(post.getLead());
			postTemp.setBody(post.getBody());

			postRepository.save(postTemp);
		}

		return "redirect:/dashboard";
	}

	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
}
