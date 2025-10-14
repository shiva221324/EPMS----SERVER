package com.epms.payroll.Services;

import com.epms.payroll.Entities.ChangeRequest;
import com.epms.payroll.Entities.Employee;
import com.epms.payroll.Entities.WorkExperience;
import com.epms.payroll.Exception.custom.ResourceNotFoundException;
import com.epms.payroll.Repositories.ChangeRequestRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChangeRequestService {

    private static final Logger logger = LoggerFactory.getLogger(ChangeRequestService.class);

    @Autowired
    private ChangeRequestRepository changeRequestRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public ChangeRequest submitChangeRequest(String employeeId, String sectionName, Object proposedChanges) {
        Employee employee = employeeService.getEmployeeByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with employeeId: " + employeeId));

        try {
            String changesJson = objectMapper.writeValueAsString(proposedChanges);
            ChangeRequest changeRequest = new ChangeRequest(
                    employee.getId(),
                    employeeId,
                    sectionName,
                    changesJson,
                    ChangeRequest.ChangeRequestStatus.PENDING
            );
            return changeRequestRepository.save(changeRequest);
        } catch (Exception e) {
            logger.error("Failed to serialize proposed changes for employeeId: {}, section: {}", employeeId, sectionName, e);
            throw new RuntimeException("Failed to serialize proposed changes: " + e.getMessage(), e);
        }
    }

    public List<ChangeRequest> getPendingChangeRequests() {
        return changeRequestRepository.findByStatus(ChangeRequest.ChangeRequestStatus.PENDING);
    }

    @Transactional
    public Employee approveChangeRequest(Long requestId) {
        ChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Change request not found with id: " + requestId));

        logger.info("Processing approval for change request {}: employeeId={}, section={}, status={}, proposedChanges={}",
                requestId, request.getEmployeeEmployeeId(), request.getSectionName(), request.getStatus(), request.getProposedChanges());

        if (request.getStatus() != ChangeRequest.ChangeRequestStatus.PENDING) {
            logger.error("Change request {} for employeeId: {} is not in PENDING status. Current status: {}",
                    requestId, request.getEmployeeEmployeeId(), request.getStatus());
            throw new RuntimeException("Change request is not in PENDING status. Current status: " + request.getStatus());
        }

        logger.info("Fetching employee with id: {}", request.getEmployeeId());
        Employee employee = employeeService.getEmployeeById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId()));

        logger.info("Employee fetched: id={}, employeeId={}", employee.getId(), employee.getEmployeeId());

        try {
            String sectionName = request.getSectionName() != null ? request.getSectionName().toLowerCase() : null;
            if (sectionName == null) {
                // Full employee update
                Employee updatedEmployee = objectMapper.readValue(request.getProposedChanges(), Employee.class);
                return employeeService.updateEmployee(employee.getId(), updatedEmployee);
            } else if (sectionName.equals("previousworkexperience")) {
                // Handle previousWorkExperience as a list
                List<Map<String, Object>> workExperienceList = objectMapper.readValue(
                        request.getProposedChanges(),
                        new TypeReference<List<Map<String, Object>>>() {}
                );
                // Convert to WorkExperience entities
                List<WorkExperience> updatedWorkExperience = new ArrayList<>();
                for (Map<String, Object> workExp : workExperienceList) {
                    WorkExperience we = new WorkExperience();
                    we.setCompany((String) workExp.get("company"));
                    we.setRole((String) workExp.get("role"));
                    we.setDuration((String) workExp.get("duration"));
                    we.setDescription((String) workExp.get("description"));
                    we.setEmployee(employee);
                    updatedWorkExperience.add(we);
                }
                // Update employee with new work experience
                employee.getPreviousWorkExperience().clear();
                employee.getPreviousWorkExperience().addAll(updatedWorkExperience);
                return employeeService.updateEmployee(employee.getId(), employee);
            } else if (sectionName.equals("professionalsummary")) {
                // Handle professionalSummary as a string
                Map<String, String> changes = objectMapper.readValue(
                        request.getProposedChanges(),
                        new TypeReference<Map<String, String>>() {}
                );
                String summary = changes.get("Summary");
                employee.setProfessionalSummary(summary);
                return employeeService.updateEmployee(employee.getId(), employee);
            } else {
                // Other section updates
                Map<String, Object> changes = objectMapper.readValue(
                        request.getProposedChanges(),
                        new TypeReference<Map<String, Object>>() {}
                );
                return employeeService.updateSection(Long.valueOf(employee.getEmployeeId()), sectionName, changes);
            }
        } catch (Exception e) {
            logger.error("Failed to apply change request {} for employeeId: {}, section: {}. Error: {}",
                    requestId, request.getEmployeeEmployeeId(), request.getSectionName(), e.getMessage(), e);
            throw new RuntimeException("Failed to apply changes: " + e.getMessage(), e);
        } finally {
            request.setStatus(ChangeRequest.ChangeRequestStatus.APPROVED);
            changeRequestRepository.save(request);
        }
    }

    @Transactional
    public ChangeRequest rejectChangeRequest(Long requestId) {
        ChangeRequest request = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Change request not found with id: " + requestId));

        logger.info("Processing rejection for change request {}: employeeId={}, section={}, status={}, proposedChanges={}",
                requestId, request.getEmployeeEmployeeId(), request.getSectionName(), request.getStatus(), request.getProposedChanges());

        if (request.getStatus() != ChangeRequest.ChangeRequestStatus.PENDING) {
            logger.error("Change request {} for employeeId: {} is not in PENDING status. Current status: {}",
                    requestId, request.getEmployeeEmployeeId(), request.getStatus());
            throw new RuntimeException("Change request is not in PENDING status. Current status: " + request.getStatus());
        }

        request.setStatus(ChangeRequest.ChangeRequestStatus.REJECTED);
        return changeRequestRepository.save(request);
    }
}