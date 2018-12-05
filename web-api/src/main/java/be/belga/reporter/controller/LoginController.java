package be.belga.reporter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import be.belga.reporter.entity.User;
import be.belga.reporter.repository.UserRepository;

@Controller
@RequestMapping(value = "/login")
public class LoginController {
	
	@Autowired
	UserRepository userRepository;

	@GetMapping(value = { "" })
	public String login() {
		
		return "login";
	}
	
	@PostMapping(value = "/process_login")
	public String process(@RequestParam String username, @RequestParam String password , RedirectAttributes ra) {
		
		if(username != null) {
			User user = userRepository.findOneByUsername(username);
			if(user != null) {
				if(user.getPassword().equals(password)) {
					ra.addFlashAttribute("error", "");
					return "redirect:/dashboard";
				}
				else {
					ra.addFlashAttribute("error", "check again username and password");
				}
			}
		}
		 
		return "redirect:/login";
		
	}
}
