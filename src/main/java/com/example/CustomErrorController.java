package com.example;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute("javax.servlet.error.status_code");
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            model.addAttribute("statusCode", statusCode);
            
            if (statusCode == 404) {
                model.addAttribute("errorMessage", "Page not found");
            } else if (statusCode == 500) {
                model.addAttribute("errorMessage", "Internal server error");
            } else {
                model.addAttribute("errorMessage", "An error occurred");
            }
        } else {
            model.addAttribute("errorMessage", "Unknown error");
        }
        
        return "error";
    }
}