package com.CSIT321.Hkotisk.Service;

import com.CSIT321.Hkotisk.Repository.StudentRepository;
import com.CSIT321.Hkotisk.Entity.StudentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class StudentService implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public StudentService(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public UserDetails loadUserByUsername(String idNumber) throws UsernameNotFoundException {
        StudentEntity student = studentRepository.findByIdNumberAndIsDeletedFalse(idNumber);
        if (student != null){
            return User.builder()
                    .username(student.getIdNumber())
                    .password(student.getPassword())
                    .roles(String.valueOf(student.getUserType()))
                    .build();

        }else{
            throw new UsernameNotFoundException(idNumber);
        }
    }



    public List<StudentEntity> getAllStudent() {
        return studentRepository.findAll();
    }



    public StudentEntity updateStudent(int sid, StudentEntity newStudentDetails){
        StudentEntity student = new StudentEntity();

        //1. check if the student id exists
        if (studentRepository.findById(sid).isPresent()){
            //2. search the ID  number of the student that will be updated
            student = studentRepository.findBySidAndIsDeletedFalse(sid);
            //3. update the record
            student.setIdNumber(newStudentDetails.getIdNumber());
            student.setEmail(newStudentDetails.getEmail());
            student.setFirstName(newStudentDetails.getFirstName());
            student.setLastName(newStudentDetails.getLastName());
            student.setPassword(passwordEncoder.encode(newStudentDetails.getPassword()));
        }else{
            throw new NoSuchElementException("Student" + sid + "does not exist!");
        }

        return studentRepository.save(student);

    }

    public String deleteStudent(int sid){
        String msg = "";

        if (studentRepository.findById(sid).isPresent()){
            studentRepository.deleteById(sid);
            msg = "Student " + sid +"is successfully deleted!";
        } else
            msg = "Student " + sid + " does not exist.";
        return msg;
    }


}
