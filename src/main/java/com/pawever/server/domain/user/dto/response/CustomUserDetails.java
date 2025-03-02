package com.pawever.server.domain.user.dto.response;

import com.pawever.server.domain.user.entity.jpa.User;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private final UserAuthInfoDto userAuthInfoDto;

    public CustomUserDetails(UserAuthInfoDto userAuthInfoDto) {

        this.userAuthInfoDto = userAuthInfoDto;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return userAuthInfoDto.getRole().name();
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {

        return userAuthInfoDto.getSocialLoginUuid();   // 사용자 구분이 가능하도록 Uuid를 삽입
    }

    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return true;
    }
}
