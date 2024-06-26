package com.nashtech.rookies.assetmanagement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nashtech.rookies.assetmanagement.audit.AuditListener;
import com.nashtech.rookies.assetmanagement.audit.Auditable;
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
@EntityListeners(AuditListener.class)
public class Asset extends BaseEntity implements Auditable {
    
    @Column(unique = true)
    private String assetCode;
    private String name;
    private String specification;
    @Enumerated(EnumType.STRING)
    private LocationConstant location;
    private LocalDate installedDate;

    @Embedded
    private AuditMetadata auditMetadata;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonManagedReference
    private Category category;

    @OneToMany(mappedBy = "asset")
            @JsonBackReference
    List<Assignment> assigments;
}
