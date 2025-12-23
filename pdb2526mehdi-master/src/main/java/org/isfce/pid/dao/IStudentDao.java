package org.isfce.pid.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.isfce.pid.model.Student;
import java.util.*;



public interface IStudentDao extends JpaRepository<Student, UUID> {
    Optional<Student> findByEmail(String email);
}
