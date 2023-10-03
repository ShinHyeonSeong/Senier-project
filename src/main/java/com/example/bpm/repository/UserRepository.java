package com.example.bpm.repository;

import com.example.bpm.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    @Query(value = "select * from user_info where email = :email", nativeQuery = true)
    Optional<UserEntity> findByEmail(String email);

    public List<UserEntity> findAllByEmailContaining(String searchKeyword);

    UserEntity findByName(String recvName);
}
