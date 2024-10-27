package com.CSIT321.Hkotisk.Controller;

import com.CSIT321.Hkotisk.Constant.ResponseCode;
import com.CSIT321.Hkotisk.Constant.WebConstants;
import com.CSIT321.Hkotisk.DTO.ReqRes;
import com.CSIT321.Hkotisk.Entity.User;
import com.CSIT321.Hkotisk.Exception.UserCustomException;
import com.CSIT321.Hkotisk.Repository.UserRepository;
import com.CSIT321.Hkotisk.Response.ServerResponse;
import com.CSIT321.Hkotisk.Service.MyUserDetailService;
import com.CSIT321.Hkotisk.Service.MyUserDetails;
import com.CSIT321.Hkotisk.Utility.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Optional;


@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signin")
    public ResponseEntity<ReqRes> login(@Valid @RequestBody ReqRes loginRequest) {
        ReqRes response = new ReqRes();
        Optional<User> userOptional = userRepo.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()) {
            response.setStatusCode(401);
            response.setMessage("Invalid email.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            response.setStatusCode(401);
            response.setMessage("Invalid password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            MyUserDetails userDetails = new MyUserDetails(user);
            var jwt = jwtUtil.generateToken(userDetails);
            var refreshToken = jwtUtil.generateRefreshToken(new HashMap<>(), userDetails);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getUsertype());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");
            if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                response.setRole("STAFF");
            } else {
                response.setRole("STUDENT");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ServerResponse> addUser(@Valid @RequestBody User user) {

        ServerResponse resp = new ServerResponse();
        try {
            // Encode the password before saving the user
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            userRepo.save(user);
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.STUD_REG);
        } catch (Exception e) {
            throw new UserCustomException("An error occurred while saving user, please check details or try again");
        }
        return new ResponseEntity<ServerResponse>(resp, HttpStatus.ACCEPTED);
    }

    @GetMapping("/signout")
    public ResponseEntity<ServerResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ServerResponse resp = new ServerResponse();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage("Successfully logged out");
        } else {
            resp.setStatus(ResponseCode.FAILURE_CODE);
            resp.setMessage("No user is currently logged in");
        }
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    private Authentication authenticate(String idNumber, String password) {

        System.out.println(idNumber+"---++----"+password);

        UserDetails userDetails = myUserDetailService.loadUserByUsername(idNumber);

        System.out.println("Sign in user details"+ userDetails);

        if(userDetails == null) {
            System.out.println("Sign in details - null" + userDetails);

            throw new BadCredentialsException("Invalid username and password");
        }
        if(!passwordEncoder.matches(password,userDetails.getPassword())) {
            System.out.println("Sign in userDetails - password mismatch"+userDetails);

            throw new BadCredentialsException("Invalid password");

        }
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

    }
}
