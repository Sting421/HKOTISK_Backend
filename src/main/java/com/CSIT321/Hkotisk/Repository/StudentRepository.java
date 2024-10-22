package com.CSIT321.Hkotisk.Repository;

import com.CSIT321.Hkotisk.Entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository <StudentEntity, Integer> {
    StudentEntity saveAndFlush(StudentEntity entity);
    StudentEntity findByIdNumberAndIsDeletedTrue(String idNumber);
    StudentEntity findBySidAndIsDeletedFalse(int sid);
    StudentEntity findByIdNumberAndIsDeletedFalse(String idNumber);
    StudentEntity findByEmailAndIsDeletedFalse(String Email);
}
