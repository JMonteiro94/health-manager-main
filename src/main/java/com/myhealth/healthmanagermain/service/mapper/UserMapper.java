package com.myhealth.healthmanagermain.service.mapper;

import com.myhealth.healthmanagermain.domain.Authority;
import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.service.dto.AdminUserDTO;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;


@UtilityClass
public class UserMapper {

  public AdminUserDTO userAccountToAdminUserDTO(UserAccount user) {
    return new AdminUserDTO(user);
  }

  public static UserAccount userAdminDTOToUserAccount(AdminUserDTO userDTO) {
    if (userDTO == null) {
      return null;
    } else {
      UserAccount user = new UserAccount();
      user.setId(userDTO.getId());
      user.setUsername(userDTO.getUsername());
      user.setFirstName(userDTO.getFirstName());
      user.setLastName(userDTO.getLastName());
      user.setEmail(userDTO.getEmail());
      user.setImageUrl(userDTO.getImageUrl());
      user.setActivated(userDTO.isActivated());
      user.setLangKey(userDTO.getLangKey());
      user.setType(userDTO.getType());
      user.setBirthDate(userDTO.getBirthDate());
      Set<Authority> authorities = authoritiesFromStrings(userDTO.getAuthorities());
      user.setAuthorities(authorities);
      return user;
    }
  }

  private static Set<Authority> authoritiesFromStrings(Set<String> authoritiesAsString) {
    Set<Authority> authorities = new HashSet<>();

    if (authoritiesAsString != null) {
      authorities =
          authoritiesAsString
              .stream()
              .map(string -> {
                Authority auth = new Authority();
                auth.setName(string);
                return auth;
              })
              .collect(Collectors.toSet());
    }

    return authorities;
  }
}
