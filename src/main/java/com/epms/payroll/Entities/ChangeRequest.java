package com.epms.payroll.Entities;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonRawValue;

import java.time.LocalDateTime;

@Entity
@Table(name = "change_requests")
@Data
public class ChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id")
    private Long employeeId; // References Employee entity ID

    @Column(name = "employee_employee_id")
    private String employeeEmployeeId; // For easier lookup

    @Column(name = "section_name")
    private String sectionName; // Null for full employee update

    @Column(name = "proposed_changes", columnDefinition = "jsonb")
    @Type(JsonType.class)
    private String proposedChanges; // JSONB for proposed changes

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ChangeRequestStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ChangeRequestStatus {
        PENDING, APPROVED, REJECTED
    }

    // Constructors
    public ChangeRequest() {}

    public ChangeRequest(Long employeeId, String employeeEmployeeId, String sectionName, String proposedChanges, ChangeRequestStatus status) {
        this.employeeId = employeeId;
        this.employeeEmployeeId = employeeEmployeeId;
        this.sectionName = sectionName;
        this.proposedChanges = proposedChanges;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeEmployeeId() {
        return employeeEmployeeId;
    }

    public void setEmployeeEmployeeId(String employeeEmployeeId) {
        this.employeeEmployeeId = employeeEmployeeId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getProposedChanges() {
        return proposedChanges;
    }

    public void setProposedChanges(String proposedChanges) {
        this.proposedChanges = proposedChanges;
    }

    public ChangeRequestStatus getStatus() {
        return status;
    }

    public void setStatus(ChangeRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}