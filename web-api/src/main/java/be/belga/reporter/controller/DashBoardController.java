package be.belga.reporter.controller;

import java.util.List;
import java.util.Optional;

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

@Controller
@RequestMapping(value = "/dashboard")
public class DashBoardController {

	@Autowired
	PostRepository postRepository;

	@Autowired
	FileUploadRepository fileUploadRepository;

	@GetMapping(value = { "" })
	public String index(Model model) {

		List<Post> posts = postRepository.findAll(new Sort(Sort.Direction.ASC, "id"));

		model.addAttribute("lstPost", posts);
		model.addAttribute("objPost", new Post());

		return "home";
	}

	@GetMapping("/edit/{idPost:[\\d]+}")
	public String eit(@PathVariable(value = "idPost") Integer idPost, Model model) {

		if (idPost != null && idPost > 0) {
			Optional<Post> postOptional = postRepository.findById(idPost);
			if (postOptional.isPresent()) {

				model.addAttribute("objPost", postOptional.get());
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
}
