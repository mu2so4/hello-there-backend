package ru.nsu.ccfit.muratov.hello.there.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.ccfit.muratov.hello.there.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> { }
