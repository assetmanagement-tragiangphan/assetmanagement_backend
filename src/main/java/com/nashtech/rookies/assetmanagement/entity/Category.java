package com.nashtech.rookies.assetmanagement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nashtech.rookies.assetmanagement.audit.AuditListener;
import com.nashtech.rookies.assetmanagement.audit.Auditable;
import jakarta.persistence.*;
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
@EntityListeners(AuditListener.class)
public class Category extends BaseEntity implements Auditable {
    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String prefix;

    @Embedded
    private AuditMetadata auditMetadata;

    @OneToMany(mappedBy = "category")
    @JsonBackReference
    private List<Asset> assets;

    @Override
    public String toString() {
        return "Category{" + "name=" + name + ", prefix=" + prefix + ", auditMetadata=" + auditMetadata + ", assets=" + assets.size() + '}';
    }
    
    
    
}