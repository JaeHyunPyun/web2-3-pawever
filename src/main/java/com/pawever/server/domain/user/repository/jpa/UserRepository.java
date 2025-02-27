package com.pawever.server.domain.user.repository.jpa;

import com.pawever.server.domain.user.entity.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT u FROM User u WHERE u.socialLoginUuid = :social_login_uuid")
    User findUuid(@Param("social_login_uuid") String social_login_uuid);

}
