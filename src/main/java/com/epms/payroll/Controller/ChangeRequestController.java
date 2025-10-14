package com.epms.payroll.Controller;

import com.epms.payroll.Entities.ChangeRequest;
import com.epms.payroll.Entities.Employee;
import com.epms.payroll.Services.ChangeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/change-requests")
public class ChangeRequestController {

    @Autowired
    private ChangeRequestService changeRequestService;

    @PostMapping
    public ResponseEntity<ChangeRequest> submitChangeRequest(
            @RequestParam String employeeId,
            @RequestParam(required = false) String sectionName,
            @RequestBody Object proposedChanges) {
        ChangeRequest changeRequest = changeRequestService.submitChangeRequest(employeeId, sectionName, proposedChanges);
        return ResponseEntity.ok(changeRequest);
    }

    @GetMapping
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ChangeRequest>> getPendingChangeRequests() {
        List<ChangeRequest> requests = changeRequestService.getPendingChangeRequests();
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{requestId}/approve")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Employee> approveChangeRequest(@PathVariable Long requestId) {
        Employee updatedEmployee = changeRequestService.approveChangeRequest(requestId);
        return ResponseEntity.ok(updatedEmployee);
    }

    @PutMapping("/{requestId}/reject")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ChangeRequest> rejectChangeRequest(@PathVariable Long requestId) {
        ChangeRequest rejectedRequest = changeRequestService.rejectChangeRequest(requestId);
        return ResponseEntity.ok(rejectedRequest);
    }
}