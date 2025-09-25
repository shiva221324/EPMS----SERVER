package com.epms.payroll.Controller;
import com.epms.payroll.Dto.BranchDto;
import com.epms.payroll.Entities.Branch;
import com.epms.payroll.Exception.codes.ErrorCode;
import com.epms.payroll.Exception.custom.BusinessException;
import com.epms.payroll.Exception.custom.ResourceNotFoundException;
import com.epms.payroll.Repositories.BranchRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/branches")
public class BranchController {

    private final BranchRepository branchRepository;

    public BranchController(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @PostMapping
    public ResponseEntity<BranchDto> createBranch(@RequestBody BranchDto branchDto) {
        // Check for duplicate branch code
        if (branchRepository.existsByBranchCode(branchDto.getBranchCode())) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.CONFLICT,
                    "Branch code already exists: " + branchDto.getBranchCode()
            );
        }

        Branch branch = mapToEntity(branchDto);
        Branch savedBranch = branchRepository.save(branch);
        return new ResponseEntity<>(mapToDto(savedBranch), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchDto> getBranchById(@PathVariable Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));
        return ResponseEntity.ok(mapToDto(branch));
    }

    @GetMapping
    public ResponseEntity<List<BranchDto>> getAllBranches() {
        List<BranchDto> branchDtos = branchRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(branchDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchDto> updateBranch(@PathVariable Long id, @RequestBody BranchDto branchDto) {
        Branch existingBranch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + id));

        existingBranch.setBranchName(branchDto.getBranchName());
        existingBranch.setBranchCode(branchDto.getBranchCode());
        existingBranch.setCity(branchDto.getCity());
        existingBranch.setState(branchDto.getState());
        existingBranch.setCountry(branchDto.getCountry());

        Branch updatedBranch = branchRepository.save(existingBranch);
        return ResponseEntity.ok(mapToDto(updatedBranch));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long id) {
        if (!branchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Branch not found with id: " + id);
        }
        branchRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Helper Methods ---
    private BranchDto mapToDto(Branch branch) {
        BranchDto dto = new BranchDto();
        dto.setBranchId(branch.getBranchId());
        dto.setBranchName(branch.getBranchName());
        dto.setBranchCode(branch.getBranchCode());
        dto.setCity(branch.getCity());
        dto.setState(branch.getState());
        dto.setCountry(branch.getCountry());
        return dto;
    }

    private Branch mapToEntity(BranchDto dto) {
        Branch branch = new Branch();
        branch.setBranchName(dto.getBranchName());
        branch.setBranchCode(dto.getBranchCode());
        branch.setCity(dto.getCity());
        branch.setState(dto.getState());
        branch.setCountry(dto.getCountry());
        return branch;
    }
}
