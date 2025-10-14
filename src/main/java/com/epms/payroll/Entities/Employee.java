package com.epms.payroll.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "employees")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String employeeId;

    private String avatarUrl;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, String> headerInfo;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, String> personalDetails;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, String> contactDetails;

    @Column(columnDefinition = "text")
    private String professionalSummary;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<WorkExperience> previousWorkExperience;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, String> emergencyContact;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, Object> visaDetails;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, Object> groupHealthInsurance;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, Object> personalHealthInsurance;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, Object> termInsurance;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, Object> aadharInfo;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, Object> panInfo;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, Object> voterIdInfo;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, Object> passportInfo;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, Object> bankInfo;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, Object> vehicleLicenseInfo;

    @Column(updatable = false)
    private LocalDate createdAt = LocalDate.now();

    private LocalDate updatedAt = LocalDate.now();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "employee"})
    @ToString.Exclude
    private User user;

    public Employee() {}

    public Employee(String name, String employeeId) {
        this.name = name;
        this.employeeId = employeeId;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDate.now();
    }

    public void updateSection(String sectionName, Object sectionData) {
        try {
            switch (sectionName.toLowerCase()) {
                case "personaldetails" -> setPersonalDetails((Map<String, String>) sectionData);
                case "contactdetails" -> setContactDetails((Map<String, String>) sectionData);
                case "emergencycontact" -> setEmergencyContact((Map<String, String>) sectionData);
                case "professionalsummary" -> setProfessionalSummary((String) sectionData);
                case "visadetails" -> setVisaDetails((Map<String, Object>) sectionData);
                case "grouphealthinsurance" -> setGroupHealthInsurance((Map<String, Object>) sectionData);
                case "personalhealthinsurance" -> setPersonalHealthInsurance((Map<String, Object>) sectionData);
                case "terminsurance" -> setTermInsurance((Map<String, Object>) sectionData);
                case "aadharinfo" -> setAadharInfo((Map<String, Object>) sectionData);
                case "paninfo" -> setPanInfo((Map<String, Object>) sectionData);
                case "voteridinfo" -> setVoterIdInfo((Map<String, Object>) sectionData);
                case "passportinfo" -> setPassportInfo((Map<String, Object>) sectionData);
                case "bankinfo" -> setBankInfo((Map<String, Object>) sectionData);
                case "vehiclelicenseinfo" -> setVehicleLicenseInfo((Map<String, Object>) sectionData);
                default -> throw new IllegalArgumentException("Unknown section: " + sectionName);
            }
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid data type for section: " + sectionName, e);
        }
    }
}