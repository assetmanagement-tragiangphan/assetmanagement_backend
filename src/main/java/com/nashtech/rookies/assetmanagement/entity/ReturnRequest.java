package com.nashtech.rookies.assetmanagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Entity
public class ReturnRequest extends BaseEntity {

    private LocalDate returnedDate;

    @ManyToOne
    @JoinColumn(name = "acceptedBy")
    private User acceptedBy;

    @ManyToOne
    @JoinColumn(name = "requestedBy")
    private User requestedBy;

    @OneToOne
    @JoinColumn(name = "assignmentId")
    private Assignment assignment;
}
