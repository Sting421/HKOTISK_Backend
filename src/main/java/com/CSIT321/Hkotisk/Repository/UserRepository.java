package com.CSIT321.Hkotisk.Repository;

import com.CSIT321.Hkotisk.Entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndPasswordAndUsertype(String email, String password, String usertype);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}
