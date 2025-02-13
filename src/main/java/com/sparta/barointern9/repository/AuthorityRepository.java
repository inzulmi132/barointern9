package com.sparta.barointern9.repository;

import com.sparta.barointern9.entity.Authority;
import com.sparta.barointern9.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    List<Authority> findAllByUser(User user);
}
