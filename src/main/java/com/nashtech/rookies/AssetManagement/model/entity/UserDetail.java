package com.nashtech.rookies.AssetManagement.model.entity;


import com.nashtech.rookies.AssetManagement.model.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(schema = "public", name = "userdetails")
@Getter
@Setter

public class UserDetail {

    @Id
    @GeneratedValue(generator = "user-generator")
    @GenericGenerator(name = "user-generator",
            parameters = @org.hibernate.annotations.Parameter(name = "prefix", value = "SD"),
            strategy = "com.nashtech.rookies.AssetManagement.utils.MyGenerator")
    private String id;
    @Column(nullable = true)
    private String firstName;
    @Column(nullable = true)
    private String lastName;
    private Gender gender;
    private Date dateOfBirth;
    private Date joinDate;

    public UserDetail(String id, String firstName, String lastName, Gender gender, Date dateOfBirth, Date joinDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.joinDate = joinDate;
    }

    public UserDetail() {
    }

    @Override
    public String toString() {
        return "UserDetail{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", dateOfBirth=" + dateOfBirth +
                ", joinDate=" + joinDate +
                '}';
    }
}
