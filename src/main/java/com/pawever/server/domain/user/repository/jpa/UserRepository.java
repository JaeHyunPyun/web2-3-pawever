package com.pawever.server.domain.user.repository.jpa;

import com.pawever.server.domain.user.entity.jpa.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT u FROM User u WHERE u.socialLoginUuid = :social_login_uuid")
    Optional<User> findUuid(@Param("social_login_uuid") String social_login_uuid);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isDeleted = true, u.deleted_at = CURRENT_TIMESTAMP WHERE u.socialLoginUuid = :social_login_uuid")
    void softDeleteByUuid(@Param("social_login_uuid") String social_login_uuid);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.socialLoginUuid = :social_login_uuid")
    void hardDeleteByUuid(@Param("social_login_uuid") String social_login_uuid);
}
