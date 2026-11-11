package io.shiftleft.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Search login
 */
@Controller
public class SearchController {

@RequestMapping(value = "/search/user", method = RequestMethod.GET)
public String doGetSearch(@RequestParam String foo, HttpServletResponse response, HttpServletRequest request) {
    // Initialize default message
    String message = "Invalid search query";
    
    try {
        // Input validation: Check if the input is null or empty
        if (foo == null || foo.trim().isEmpty()) {
            return "Search query cannot be empty";
        }
        
        // Input validation: Whitelist approach - only allow alphanumeric and safe characters
        Pattern safePattern = Pattern.compile("^[a-zA-Z0-9\\s._-]{1,100}$");
        if (!safePattern.matcher(foo).matches()) {
            return "Invalid search query format. Only alphanumeric characters, spaces, dots, underscores, and hyphens are allowed";
        }
        
        // Removed SpEL expression parsing entirely to prevent code injection
        // Instead, treat the input as a simple search string
        message = performSafeSearch(foo);
        
        // Encode output to prevent XSS attacks
        message = Encode.forHtml(message);
        
    } catch (Exception ex) {
        // Log the exception securely without exposing stack traces to users
        // Use proper logging framework instead of System.out.println
        // logger.error("Search error occurred", ex);
        message = "An error occurred while processing your search";
    }
    
    return message;
}

// Helper method to perform safe search operation
private String performSafeSearch(String searchQuery) {
    // Implement your business logic here without using dynamic code evaluation
    // Example: Query database, search index, etc.
    return "Search results for: " + searchQuery;
}

    return message.toString();
  }
}
