package com.formation.web;

import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
 @GetMapping("/")
 String home() { return "index"; }

 @GetMapping("/profil")
 String profil(@AuthenticationPrincipal OAuth2User user, Model model) {
   model.addAttribute("login", user.getAttribute("login"));
   model.addAttribute("name", user.getAttribute("name"));
   model.addAttribute("avatarUrl", user.getAttribute("avatar_url"));
   model.addAttribute("githubUrl", user.getAttribute("html_url"));
   model.addAttribute("attributes", user.getAttributes());
   return "profil";
 }

 @GetMapping("/api/me")
 @ResponseBody
 Map<String,Object> me(@AuthenticationPrincipal OAuth2User user) {
   return user.getAttributes();
 }
}
