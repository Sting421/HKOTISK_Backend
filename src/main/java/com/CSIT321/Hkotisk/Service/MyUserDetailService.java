package com.CSIT321.Hkotisk.Service;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.CSIT321.Hkotisk.Entity.User;
import com.CSIT321.Hkotisk.Repository.UserRepository;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        Optional<User> user = userRepository.findByEmail(s);

        user.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return user.map(MyUserDetails::new).get();
    }

}