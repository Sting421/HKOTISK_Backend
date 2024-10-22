package com.CSIT321.Hkotisk.Service;

import com.CSIT321.Hkotisk.DTO.ReqRes;
import com.CSIT321.Hkotisk.Entity.StudentEntity;
import com.CSIT321.Hkotisk.Entity.UserType;
import com.CSIT321.Hkotisk.Repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class AuthService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private StudentService studentService;

    public ResponseEntity<ReqRes> register(ReqRes registrationRequest) {
        ReqRes response = new ReqRes();

        try {
            // Check if there are deleted accounts and whether the account is recoverable
            if (studentRepository.count() > 0) {
                if (studentRepository.findByIdNumberAndIsDeletedTrue(registrationRequest.getIdNumber()) != null) {
                    response.setMessage("This account was already deleted. Would you like to recover this account?");
                    response.setStatusCode(410);
                    return ResponseEntity.status(HttpStatus.GONE).body(response);
                }else if (studentRepository.findByEmailAndIsDeletedFalse(registrationRequest.getEmail()) != null){
                    response.setMessage("Email is already used.");
                    response.setStatusCode(409);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }else if (studentRepository.findByIdNumberAndIsDeletedFalse(registrationRequest.getIdNumber()) != null) {
                    response.setMessage("ID number is already in use.");
                    response.setStatusCode(409);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            }
        } catch (NullPointerException e) {
            System.out.println("There are no deleted accounts yet.");
        }

        // Validate password format
        if (!isValidPassword(registrationRequest.getPassword())) {
            response.setMessage("Invalid password format. It must be at least 8 characters with 1 uppercase letter.");
            response.setStatusCode(400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            // Create and save the new student
            var student = new StudentEntity();
            student.setIdNumber(registrationRequest.getIdNumber());
            student.setEmail(registrationRequest.getEmail());
            student.setFirstName(registrationRequest.getFirstName());
            student.setLastName(registrationRequest.getLastName());
            student.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            student.setUserType(UserType.STUDENT);
            student.setDeleted(false);

            StudentEntity newStudent = studentRepository.save(student);

            if (newStudent.getSid() > 0) {
                response.setStudent(newStudent);
                response.setMessage("User Saved Successfully");
                response.setStatusCode(200);
                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        response.setMessage("An unexpected error occurred.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


    private Authentication authenticate(String idNumber, String password) {

        System.out.println(idNumber+"---++----"+password);

        UserDetails userDetails = studentService.loadUserByUsername(idNumber);

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

    public ResponseEntity<ReqRes> login(ReqRes loginRequest) {
        ReqRes response = new ReqRes();
        StudentEntity student = studentRepository.findByIdNumberAndIsDeletedFalse(loginRequest.getIdNumber());

        if (student == null) {
            response.setStatusCode(401);
            response.setMessage("Invalid ID number.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), student.getPassword())) {
            response.setStatusCode(401);
            response.setMessage("Invalid password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getIdNumber(), loginRequest.getPassword()));

            var jwt = jwtUtils.generateToken(student);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), student);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(student.getUserType().name());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private boolean isValidPassword(String password) {
        // Password should be at least 8 characters with 1 uppercase letter
        return password != null && password.length() >= 8 && password.matches(".*[A-Z].*");
    }
}