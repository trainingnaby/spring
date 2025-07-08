package com.formation.controlleur;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MonPremierControlleurSpring {

	@GetMapping("/")
	@ResponseBody
	public String index() {
		return "Ici mon premier controlleur Spring !";
	}
}
