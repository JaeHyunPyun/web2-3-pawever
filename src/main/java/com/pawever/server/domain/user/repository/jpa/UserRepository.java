package com.pawever.server.domain.user.repository.jpa;

import com.pawever.server.domain.carehub.entity.Shelter;
import com.pawever.server.domain.user.dto.response.StaffProfileResponseDto;
import com.pawever.server.domain.user.entity.jpa.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT u FROM User u WHERE u.socialLoginUuid = :socialLoginUuid")
    Optional<User> findBySocialLoginUuid(@Param("socialLoginUuid") String socialLoginUuid);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isDeleted = true, u.deleted_at = CURRENT_TIMESTAMP WHERE u.socialLoginUuid = :socialLoginUuid")
    void softDeleteByUuid(@Param("socialLoginUuid") String socialLoginUuid);

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.socialLoginUuid = :socialLoginUuid")
    void hardDeleteByUuid(@Param("socialLoginUuid") String socialLoginUuid);

    @Query(value = "SELECT u.name, u.email, u.profile_image_url, " +
        "s.name, s.center_phone_number, s.manager_phone_number " +
        "FROM user u JOIN shelter s ON u.user_id = s.user_id " +
        "WHERE u.user_id = :userId",
        nativeQuery = true)
    List<StaffProfileResponseDto> findStaffProfileByUserId(@Param("userId") Long userId);

    List<Shelter> findSheltersByUserId(Long userId);
}
