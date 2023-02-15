package com.myhealth.healthmanagermain.web.rest;

import com.myhealth.healthmanagermain.aop.timer.MeasureTime;
import com.myhealth.healthmanagermain.security.jwt.JWTFilter;
import com.myhealth.healthmanagermain.security.jwt.JWTToken;
import com.myhealth.healthmanagermain.security.jwt.TokenProvider;
import com.myhealth.healthmanagermain.web.rest.vm.LoginVM;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class UserJWTController {

  @NotNull
  private final TokenProvider tokenProvider;
  @NotNull
  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  @MeasureTime
  @PostMapping("/authenticate")
  public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        loginVM.getUsername(),
        loginVM.getPassword()
    );

    Authentication authentication = authenticationManagerBuilder.getObject()
        .authenticate(authenticationToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = tokenProvider.createToken(authentication, loginVM.isRememberMe());
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, JWTFilter.BEARER + jwt);
    return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
  }
}
