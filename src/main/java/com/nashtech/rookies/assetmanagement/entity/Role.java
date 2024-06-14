package com.nashtech.rookies.assetmanagement.entity;

import com.nashtech.rookies.assetmanagement.util.RoleConstant;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Entity
public class Role extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private RoleConstant name;

    @OneToMany(mappedBy = "role")
    private List<User> users;
}

