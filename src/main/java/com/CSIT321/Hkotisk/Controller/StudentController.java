package com.CSIT321.Hkotisk.Controller;

import com.CSIT321.Hkotisk.Service.StudentService;
import com.CSIT321.Hkotisk.Entity.StudentEntity;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;


    @GetMapping
    public List<StudentEntity> getAllStudent() {
        return studentService.getAllStudent();
    }

    @PutMapping
    public StudentEntity updateStudent(@RequestParam int sid, @RequestBody StudentEntity newStudentDetails) {
        return studentService.updateStudent(sid, newStudentDetails);
    }

    @DeleteMapping
    public String deleteStudent(@RequestParam int sid) {
        return studentService.deleteStudent(sid);
    }
}
