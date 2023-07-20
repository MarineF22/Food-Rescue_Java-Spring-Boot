package com.fournierMarine.cgi.stage.FoodRescueApp.controllers;

import com.fournierMarine.cgi.stage.FoodRescueApp.DataBaseQueries;
import com.fournierMarine.cgi.stage.FoodRescueApp.models.Users;
import com.fournierMarine.cgi.stage.FoodRescueApp.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.apache.commons.codec.digest.DigestUtils;
import javax.servlet.http.HttpSession;
@Controller
public class RegisterController {

    private DataBaseQueries query;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    public RegisterController(DataBaseQueries query) {
        this.query = query;
    }

    @PostMapping("/register")
    public ModelAndView register(String email, String username, String password, String confirmPassword) {
        if (query.checkUser(email)) {
            ModelAndView mav = new ModelAndView("register");
            mav.addObject("error", "Email already exists");
            return mav;
        }

        if (!password.equals(confirmPassword)) {
            ModelAndView mav = new ModelAndView("register");
            mav.addObject("error", "Passwords do not match");
            return mav;
        }
        //usersRepository.deleteAll();

        Users user = new Users();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(encryptPassword(password));

        usersRepository.save(user);

        ModelAndView mav = new ModelAndView("login");
        mav.addObject("username", username);
        return mav;
    }

    @GetMapping("/register")
    public ModelAndView getRegister() {
        ModelAndView mav = new ModelAndView("register");
        return mav;
    }

    private String encryptPassword(String password) {
        return DigestUtils.md5Hex(password);
    }


    @PostMapping("/login")
    public ModelAndView login(String email, String password, HttpSession session) {
        if (!query.checkUser(email)) {
            ModelAndView mav = new ModelAndView("login");
            mav.addObject("error", "Email doesn't exist");
            return mav;
        }

        Users user = query.getUser(email);
        String hashedPassword = encryptPassword(password);

        if (user.getPassword().equals(hashedPassword)) {
            session.setAttribute("loggedInUser", user.getEmail());
            return new ModelAndView("redirect:/");
        } else {
            ModelAndView mav = new ModelAndView("login");
            mav.addObject("error", "Incorrect password");
            return mav;
        }

    }

    @GetMapping("/login")
    public ModelAndView getLogin(String email) {
        ModelAndView mav = new ModelAndView("login");
        return mav;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Clear the logged-in user session attribute
        session.removeAttribute("loggedInUser");

        // Redirect to the index page
        return "redirect:/";
    }
}
