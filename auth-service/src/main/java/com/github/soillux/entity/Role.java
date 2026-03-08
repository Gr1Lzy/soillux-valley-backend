package com.github.soillux.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

import static com.github.soillux.entity.EnumRole.ROLE_ANONYMOUS;

@Getter
@Setter
@Entity
@ToString(exclude = {"users"})
@RequiredArgsConstructor
@Table(name = "roles")
public class Role extends AbstractEntity implements GrantedAuthority {

  @Enumerated(EnumType.STRING)
  @Column(name = "name", unique = true, nullable = false)
  private EnumRole name;

  @ManyToMany(mappedBy = "roles")
  private Set<User> users = new HashSet<>();

  public static Role of(EnumRole name) {
    Role role = new Role();
    role.setName(name);
    return role;
  }

  @Override
  public String getAuthority() {
    return name != null ? name.toString() : ROLE_ANONYMOUS.toString();
  }
}
