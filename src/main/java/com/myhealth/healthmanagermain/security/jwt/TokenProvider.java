package com.myhealth.healthmanagermain.security.jwt;

import com.myhealth.healthmanagermain.config.properties.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenProvider {

  private static final String AUTHORITIES_KEY = "auth";
  private static final String INVALID_JWT_TOKEN = "Invalid JWT token.";

  private final SecurityProperties securityProperties;

  private final Key key;

  private final JwtParser jwtParser;

  public TokenProvider(@NonNull SecurityProperties securityProperties) {
    this.securityProperties = securityProperties;
    String secret = securityProperties.getSecret();
    if (StringUtils.isBlank(secret)) {
      throw new MissingResourceException("JWT secret is missing", "TokenProvider", "secret");
    }
    key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
  }

  public String createToken(@NonNull Authentication authentication, boolean rememberMe) {
    String authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

    long now = (new Date()).getTime();
    Date validity;
    if (rememberMe) {
      validity = new Date(
          now + ((long) 1000 * this.securityProperties.getTokenValidityInSecondsRememberMe()));
    } else {
      validity = new Date(
          now + ((long) 1000 * this.securityProperties.getTokenValidityInSeconds()));
    }

    return Jwts
        .builder()
        .setSubject(authentication.getName())
        .claim(AUTHORITIES_KEY, authorities)
        .signWith(key, SignatureAlgorithm.HS512)
        .setExpiration(validity)
        .compact();
  }

  public Authentication getAuthentication(@NonNull String token) {
    Claims claims = jwtParser.parseClaimsJws(token).getBody();

    Collection<? extends GrantedAuthority> authorities = Arrays
        .stream(claims.get(AUTHORITIES_KEY).toString().split(","))
        .filter(auth -> !auth.trim().isEmpty())
        .map(SimpleGrantedAuthority::new)
        .toList();

    User principal = new User(claims.getSubject(), "", authorities);

    return new UsernamePasswordAuthenticationToken(principal, token, authorities);
  }

  public boolean validateToken(@NonNull String authToken) {
    try {
      jwtParser.parseClaimsJws(authToken);
      return true;
    } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException |
             SignatureException e) {
      log.trace(INVALID_JWT_TOKEN, e);
    } catch (IllegalArgumentException e) {
      log.error("Token validation error {}", e.getMessage());
    }

    return false;
  }
}
