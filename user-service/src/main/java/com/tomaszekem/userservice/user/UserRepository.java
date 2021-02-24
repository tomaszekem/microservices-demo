package com.tomaszekem.userservice.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("select u.email from UserEntity u where u.email in :emails")
    List<String> findUsedEmails(@Param("emails") Set<String> emails);

    @Query("select new com.tomaszekem.userservice.user.UserToEmailProjection(u.id, u.email) " +
            "from UserEntity u " +
            "where u.email in :emails")
    List<UserToEmailProjection> findByEmails(@Param("emails") Set<String> emails);

    List<UserEntity> findByIdIn(Set<Long> ids);

}
