package com.myhealth.healthmanagermain.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authority")
public class Authority implements Serializable {

  @Id
  @NotNull
  @Size(min = 3, max = 50)
  @Column(length = 50)
  private String name;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Authority other)) {
      return false;
    }
    return Objects.equals(name, other.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }

}

