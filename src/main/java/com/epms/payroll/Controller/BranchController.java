package com.epms.payroll.Controller;
import com.epms.payroll.Dto.BranchDto;
import com.epms.payroll.Entities.Branch;
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
        Branch branch = mapToEntity(branchDto);
        Branch savedBranch = branchRepository.save(branch);
        return new ResponseEntity<>(mapToDto(savedBranch), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchDto> getBranchById(@PathVariable Long id) {
        return branchRepository.findById(id)
                .map(branch -> ResponseEntity.ok(mapToDto(branch)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<BranchDto>> getAllBranches() {
        List<Branch> branches = branchRepository.findAll();
        List<BranchDto> branchDtos = branches.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(branchDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchDto> updateBranch(@PathVariable Long id, @RequestBody BranchDto branchDto) {
        return branchRepository.findById(id)
                .map(existingBranch -> {
                    existingBranch.setBranchName(branchDto.getBranchName());
                    existingBranch.setBranchCode(branchDto.getBranchCode());
                    existingBranch.setCity(branchDto.getCity());
                    existingBranch.setState(branchDto.getState());
                    existingBranch.setCountry(branchDto.getCountry());
                    Branch updatedBranch = branchRepository.save(existingBranch);
                    return ResponseEntity.ok(mapToDto(updatedBranch));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long id) {
        if (!branchRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        branchRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Helper Methods for Mapping ---

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
