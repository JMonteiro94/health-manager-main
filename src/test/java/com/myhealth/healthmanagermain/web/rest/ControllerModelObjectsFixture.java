package com.myhealth.healthmanagermain.web.rest;

import com.myhealth.healthmanagermain.bootstrap.RandomDataUtil;
import com.myhealth.healthmanagermain.config.Constants;
import com.myhealth.healthmanagermain.domain.Authority;
import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.domain.enums.UserType;
import com.myhealth.healthmanagermain.security.AuthoritiesConstants;
import com.myhealth.healthmanagermain.service.dto.AdminUserDTO;
import com.myhealth.healthmanagermain.web.rest.dto.ManagedUserDTO;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ControllerModelObjectsFixture {

  public static ManagedUserDTO getValidManagedUserVM() {
    ManagedUserDTO validUser = new ManagedUserDTO();
    validUser.setUsername("bob");
    validUser.setPassword("password");
    validUser.setFirstName("jonh");
    validUser.setLastName("doe");
    validUser.setEmail("email@test.com");
    validUser.setActivated(true);
    validUser.setImageUrl("http://placehold.it/50x50");
    validUser.setLangKey(Constants.DEFAULT_LANGUAGE);
    validUser.setType(UserType.PRIVATE);
    validUser.setBirthDate(RandomDataUtil.generateRandomBirthDate());
    validUser.setAuthorities(Set.of(AuthoritiesConstants.USER));
    return validUser;
  }

  public static UserAccount getValidUserAccount() {
    UserAccount user = new UserAccount();
    user.setUsername(RandomDataUtil.generateRandomUsername());
    user.setPassword(RandomDataUtil.generateEncodePassword());
    user.setActivated(true);
    user.setEmail(RandomDataUtil.generateRandomEmailString());
    user.setFirstName(RandomDataUtil.generateRandomName());
    user.setLastName(RandomDataUtil.generateRandomName());
    user.setImageUrl("http://placehold.it/50x50");
    user.setResetKey(RandomDataUtil.generateResetKey());
    user.setLangKey(Constants.DEFAULT_LANGUAGE);
    user.setType(UserType.PRIVATE);
    user.setResetDate(Instant.now().plusSeconds(60));
    user.setBirthDate(RandomDataUtil.generateRandomBirthDate());
    user.setAuthorities(Set.of(new Authority(AuthoritiesConstants.USER)));
    return user;
  }

  public static AdminUserDTO getValidAdminUserAccount() {
    return AdminUserDTO.builder()
        .username(RandomDataUtil.generateRandomUsername())
        .firstName(RandomDataUtil.generateRandomName())
        .lastName(RandomDataUtil.generateRandomName())
        .email(RandomDataUtil.generateRandomEmailString())
        .activated(false)
        .imageUrl("http://placehold.it/50x50")
        .langKey(Constants.DEFAULT_LANGUAGE)
        .type(UserType.PRIVATE)
        .birthDate(RandomDataUtil.generateRandomBirthDate())
        .authorities(Collections.singleton(AuthoritiesConstants.ADMIN)).build();
  }
}
