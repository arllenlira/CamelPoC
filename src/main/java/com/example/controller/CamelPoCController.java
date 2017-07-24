package com.example.controller;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.model.User;
import com.example.service.UserService;

@Controller
public class CamelPoCController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CamelContext context = new DefaultCamelContext();

	@RequestMapping(value = "/camelConsumeDelete", method = RequestMethod.GET)
	public ModelAndView camelConsumeDelete() throws Exception {
    	try {
			context.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					from("jpa://com.example.model.User?consumer.delay=5s").setBody().
					simple("Consuming!").to("stream:out");

				}
			});
			context.start();
			Thread.sleep(20000);
		} finally {
			context.stop();
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("camel");
		return modelAndView;
	}

	
	@RequestMapping(value = "/camelFlag", method = RequestMethod.GET)
	public ModelAndView camelFlag() throws Exception {
    	
		try {
			context.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					from("jpa://com.example.model.User2?consumer.delay=5s").setBody().
					simple("Consuming!").to("stream:out");

				}
			});
			context.start();
			Thread.sleep(20000);
		} finally {
			context.stop();
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("camel");
		return modelAndView;
	}
	
	@RequestMapping(value = "/camelConsumeNoDelete", method = RequestMethod.GET)
	public ModelAndView camelConsumeNoDelete() throws Exception {
    	

		try {
			context.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					from("jpa://com.example.model.User?consumer.delay=5s&consumeDelete=false").setBody().
					simple("Consuming!").to("stream:out");
				}
			});
			context.start();
			Thread.sleep(20000);
		} finally {
			context.stop();
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("camel");
		return modelAndView;
	}
	
	
	
	@RequestMapping(value = "/camelConsumeFile", method = RequestMethod.GET)
	public ModelAndView camelConsumeFile() throws Exception {
    	

		try {
			context.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					from("file:data/inbox?noop=true")
	                .to("file:data/outbox").end();
				}
			});
			context.start();
			Thread.sleep(20000);
		} finally {
			context.stop();
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("camel");
		return modelAndView;
	}
	
	@RequestMapping(value = "/camelConsumeJPAFile", method = RequestMethod.GET)
	public ModelAndView camelConsumeJPAFile() throws Exception {
		try {
			context.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					from("jpa://com.example.model.User?consumer.delay=5s"
							+ "&consumeDelete=false").marshal().json(JsonLibrary.Jackson)
					.to("file:data/outbox");
				}
			});
			context.start();
			Thread.sleep(20000);
		} finally {
			context.stop();
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("camel");
		return modelAndView;
	}

	@RequestMapping(value = "/camelJPAFlagFile", method = RequestMethod.GET)
    @Transactional
	public ModelAndView camelJPAFlagFile() throws Exception {
    	
		try {
			context.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					from("jpa://com.example.model.User2?"
							+ "consumer.delay=5s"
							+ "&consumeDelete=false")
					.marshal()
					.json(JsonLibrary.Jackson)
					.to("file:data/outbox");
				}
			});
			context.start();
			Thread.sleep(20000);
		} finally {
			//context.stop();
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("camel");
		return modelAndView;
	}

	@RequestMapping(value = { "/", "/login" }, method = RequestMethod.GET)
	public ModelAndView login() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}

	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public ModelAndView registration() {
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("registration");
		return modelAndView;
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView();
		User userExists = userService.findUserByEmail(user.getEmail());
		if (userExists != null) {
			bindingResult.rejectValue("email", "error.user",
					"There is already a user registered with the email provided");
		}
		if (bindingResult.hasErrors()) {
			modelAndView.setViewName("registration");
		} else {
			userService.saveUser(user);
			modelAndView.addObject("successMessage", "User has been registered successfully");
			modelAndView.addObject("user", new User());
			modelAndView.setViewName("registration");

		}
		return modelAndView;
	}

	@RequestMapping(value = "/admin/home", method = RequestMethod.GET)
	public ModelAndView home() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName",
				"Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
		modelAndView.setViewName("admin/home");
		return modelAndView;
	}
	
}