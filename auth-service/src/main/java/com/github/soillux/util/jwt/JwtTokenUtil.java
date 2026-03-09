package com.github.soillux.util.jwt;

import com.github.soillux.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenUtil {

  public static final String TOKEN_TYPE = "token_type";
  public static final String ACCESS_TOKEN = "access";
  public static final String REFRESH_TOKEN = "refresh";

  public static final String ID = "id";
  public static final String EMAIL = "email";
  public static final String ROLES = "roles";

  private RSAPublicKey rsaPublicKey;
  private RSAPrivateKey rsaPrivateKey;

  @Value("${jwt.key.public}")
  private String publicKey;

  @Value("${jwt.key.private}")
  private String privateKey;

  @Value("${jwt.access-token.lifetime}")
  private Duration accessTokenLifetime;

  @Value("${jwt.refresh-token.lifetime}")
  private Duration refreshTokenLifetime;

  @PostConstruct
  public void initKeys() throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] publicBytes = Base64.getDecoder().decode(publicKey);
    byte[] privateBytes = Base64.getDecoder().decode(privateKey);

    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(publicBytes));
    rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
  }

  public Long extractUserId(String token) {
    return extractClaim(token, claims -> claims.get(ID, Long.class));
  }

  public List<String> extractUserRoles(String token) {
    List<?> roles = extractClaim(token, claims -> claims.get(ROLES, List.class));

    return roles.stream()
        .map(Object::toString)
        .toList();
  }

  public String generateAccessToken(UserDetails userDetails) {
    return buildToken(userDetails, accessTokenLifetime, ACCESS_TOKEN);
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return buildToken(userDetails, refreshTokenLifetime, REFRESH_TOKEN);
  }

  private String buildToken(UserDetails userDetails, Duration lifetime, String tokenType) {
    if (!(userDetails instanceof User user)) {
      throw new IllegalArgumentException("UserDetails must be an instance of User");
    }

    Instant now = Instant.now();
    Instant expiry = now.plus(lifetime);

    JwtBuilder builder = Jwts.builder()
        .header()
          .add("alg", "RS256")
          .type("JWT")
        .and()
        .claim(ID, user.getId())
        .claim(TOKEN_TYPE, tokenType);

    if (ACCESS_TOKEN.equals(tokenType)) {
      builder
          .claim(EMAIL, user.getEmail())
          .claim(ROLES, user.getRoles().stream()
              .map(role -> role.getName().name())
              .toList());
    }

    return builder
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiry))
        .signWith(rsaPrivateKey, Jwts.SIG.RS256)
        .compact();
  }

  public boolean isAccessTokenValid(String token) {
    return isValidToken(token, ACCESS_TOKEN);
  }

  public boolean isRefreshTokenValid(String token) {
    return isValidToken(token, REFRESH_TOKEN);
  }

  private boolean isValidToken(String token, String expectedType) {
    try {
      Claims claims = extractAllClaims(token);
      return expectedType.equals(claims.get(TOKEN_TYPE, String.class))
          && claims.getExpiration().after(new Date());
    } catch (JwtException | IllegalArgumentException _) {
      return false;
    }
  }

  private <T> T extractClaim(String token, Function<Claims, T> resolver) {
    return resolver.apply(extractAllClaims(token));
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(rsaPublicKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
