package com.nashtech.rookies.assetmanagement.entity;

import com.nashtech.rookies.assetmanagement.audit.AuditListener;
import com.nashtech.rookies.assetmanagement.audit.Auditable;
import com.nashtech.rookies.assetmanagement.util.GenderConstant;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Entity
@Table(name = "_user")
@EntityListeners(AuditListener.class)
public class User extends BaseEntity implements Auditable {
    private String staffCode;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private GenderConstant gender;
    @Enumerated(EnumType.STRING)
    private LocationConstant location;
    private LocalDate joinedDate;
    private LocalDate dateOfBirth;
    private Boolean isChangePassword;

    @Embedded
    private AuditMetadata auditMetadata;

    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;
    @OneToMany(mappedBy = "assignee")
    private List<Assignment> assignments;
    @OneToMany(mappedBy = "acceptedBy")
    private List<ReturnRequest> returnRequests;
    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @OneToMany(mappedBy = "auditMetadata.createdBy")
    private List<Asset> createdAssets;
    @OneToMany(mappedBy = "auditMetadata.updatedBy")
    private List<Asset> updatedAssets;
    @OneToMany(mappedBy = "auditMetadata.createdBy")
    private List<User> createdUsers;
    @OneToMany(mappedBy = "auditMetadata.updatedBy")
    private List<User> updatedUsers;
    @OneToMany(mappedBy = "auditMetadata.createdBy")
    private List<Assignment> createdAssignments;
    @OneToMany(mappedBy = "auditMetadata.updatedBy")
    private List<Assignment> updatedAssignments;

}

