package me.dulce.gamesite.gamesite2.transportcontroller;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@Controller
public class RestWebController {

    public static final String FRONTEND_ENDPOINT_GROUP = "pages";

    @Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE").allowedHeaders("*")
						.allowedOrigins("*");
			}
		};
	}

    @GetMapping("/")
    public RedirectView index() {
        return new RedirectView("/" + FRONTEND_ENDPOINT_GROUP);
    }

	//any page in /pages returns static resource
	@RequestMapping("/pages/**")
	public ModelAndView requestAnyPath() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("index.html");
		return mav;
	}
	
}