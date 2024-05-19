package ru.nsu.ccfit.muratov.hello.there.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.muratov.hello.there.entity.UserEntity;
import ru.nsu.ccfit.muratov.hello.there.repository.UserRepository;

import java.util.List;
import java.util.logging.Logger;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository repository;

    private static final Logger logger = Logger.getLogger(UserDetailsServiceImpl.class.getCanonicalName());

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //buggy code below
        UserEntity user = repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("username not found"));
        return new User(user.getUsername(), user.getPassword(), getAuthorities(user));
    }

    private static List<SimpleGrantedAuthority> getAuthorities(UserEntity user) {
        return user.getRoles().stream().map((role) -> new SimpleGrantedAuthority(role.getName())).toList();
    }
}
