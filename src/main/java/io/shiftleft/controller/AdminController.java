package io.shiftleft.controller;

import io.shiftleft.model.AuthToken;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Admin checks login
 */
@Controller
public class AdminController {
  private String fail = "redirect:/";

  // helper
private boolean isAdmin(String auth) {
    try {
        // Use JWT token validation instead of deserializing user-controlled data
        // Parse and validate the JWT token
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSecretKey())
            .build()
            .parseClaimsJws(auth)
            .getBody();
        
        // Extract and validate the admin claim
        String role = claims.get("role", String.class);
        return "ADMIN".equals(role);
        
    } catch (JwtException ex) {
        System.out.println("Invalid JWT token: " + ex.getMessage());
        return false;
    } catch (Exception ex) {
        System.out.println("Token validation error: " + ex.getMessage());
        return false;
    }
}

private SecretKey getSecretKey() {
    // Load secret key from secure configuration (e.g., environment variable or secure vault)
    // Never hardcode the secret key in production code
    String secret = System.getenv("JWT_SECRET_KEY");
    if (secret == null || secret.isEmpty()) {
        throw new IllegalStateException("JWT_SECRET_KEY must be configured");
    }
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
}

  }

  //
  @RequestMapping(value = "/admin/printSecrets", method = RequestMethod.POST)
  public String doPostPrintSecrets(HttpServletResponse response, HttpServletRequest request) {
    return fail;
  }


  @RequestMapping(value = "/admin/printSecrets", method = RequestMethod.GET)
  public String doGetPrintSecrets(@CookieValue(value = "auth", defaultValue = "notset") String auth, HttpServletResponse response, HttpServletRequest request) throws Exception {

    if (request.getSession().getAttribute("auth") == null) {
      return fail;
    }

    String authToken = request.getSession().getAttribute("auth").toString();
    if(!isAdmin(authToken)) {
      return fail;
    }

    ClassPathResource cpr = new ClassPathResource("static/calculations.csv");
    try {
      byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
      response.getOutputStream().println(new String(bdata, StandardCharsets.UTF_8));
      return null;
    } catch (IOException ex) {
      ex.printStackTrace();
      // redirect to /
      return fail;
    }
  }

  /**
   * Handle login attempt
   * @param auth cookie value base64 encoded
   * @param password hardcoded value
   * @param response -
   * @param request -
   * @return redirect to company numbers
   * @throws Exception
   */
@RequestMapping(value = "/admin/login", method = RequestMethod.POST)
public String doPostLogin(@CookieValue(value = "auth", defaultValue = "notset") String auth, 
                         @RequestBody String password, 
                         HttpServletResponse response, 
                         HttpServletRequest request) throws Exception {
    String succ = "redirect:/admin/printSecrets";

    try {
        // Check if valid token already exists
        if (!auth.equals("notset")) {
            if(isAdmin(auth)) {
                request.getSession().setAttribute("auth", auth);
                return succ;
            }
        }

        // Validate password input format
        String[] pass = password.split("=");
        if(pass.length != 2) {
            return fail;
        }
        
        // Validate password securely
        // In production, use a secure password hashing mechanism like bcrypt
        if(pass[1] != null && pass[1].length() > 0 && pass[1].equals("shiftleftsecret")) {
            // Create JWT token instead of serialized object
            String jwtToken = Jwts.builder()
                .setSubject("admin")
                .claim("role", "ADMIN")
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + 3600000)) // 1 hour expiration
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
            
            // Set secure cookie with HttpOnly and Secure flags
            Cookie authCookie = new Cookie("auth", jwtToken);
            authCookie.setHttpOnly(true);
            authCookie.setSecure(true); // Only send over HTTPS
            authCookie.setPath("/");
            authCookie.setMaxAge(3600); // 1 hour
            response.addCookie(authCookie);

            // Store token in session
            request.getSession().setAttribute("auth", jwtToken);

            return succ;
        }
        return fail;
        
    } catch (Exception ex) {
        // Log securely without exposing sensitive information
        System.err.println("Login error occurred");
        return fail;
    }
}

private SecretKey getSecretKey() {
    // Load secret key from secure configuration (e.g., environment variable or secure vault)
    // Never hardcode the secret key in production code
    String secret = System.getenv("JWT_SECRET_KEY");
    if (secret == null || secret.isEmpty()) {
        throw new IllegalStateException("JWT_SECRET_KEY must be configured");
    }
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
}

  }

  /**
   * Same as POST but just a redirect
   * @param response
   * @param request
   * @return redirect
   */
  @RequestMapping(value = "/admin/login", method = RequestMethod.GET)
  public String doGetLogin(HttpServletResponse response, HttpServletRequest request) {
    return "redirect:/";
  }
}
