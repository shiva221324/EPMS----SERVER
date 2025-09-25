package com.epms.payroll.Controller;

import com.epms.payroll.Dto.DesignationDto;
import com.epms.payroll.Entities.Designation;
import com.epms.payroll.Exception.codes.ErrorCode;
import com.epms.payroll.Exception.custom.BusinessException;
import com.epms.payroll.Exception.custom.ResourceNotFoundException;
import com.epms.payroll.Repositories.DesignationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/designations")
public class DesignationController {

    private final DesignationRepository designationRepository;

    public DesignationController(DesignationRepository designationRepository) {
        this.designationRepository = designationRepository;
    }

    @PostMapping
    public ResponseEntity<DesignationDto> createDesignation(@RequestBody DesignationDto designationDto) {
        // Check for duplicate designation code
        if (designationRepository.existsByDesignationCode(designationDto.getDesignationCode())) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.CONFLICT,
                    "Designation code already exists: " + designationDto.getDesignationCode()
            );
        }

        Designation designation = mapToEntity(designationDto);
        Designation savedDesignation = designationRepository.save(designation);
        return new ResponseEntity<>(mapToDto(savedDesignation), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DesignationDto> getDesignationById(@PathVariable Long id) {
        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found with id: " + id));
        return ResponseEntity.ok(mapToDto(designation));
    }

    @GetMapping
    public ResponseEntity<List<DesignationDto>> getAllDesignations() {
        List<DesignationDto> designationDtos = designationRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(designationDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DesignationDto> updateDesignation(@PathVariable Long id, @RequestBody DesignationDto designationDto) {
        Designation existingDesignation = designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found with id: " + id));

        existingDesignation.setDesignationName(designationDto.getDesignationName());
        existingDesignation.setDesignationCode(designationDto.getDesignationCode());
        existingDesignation.setLevel(designationDto.getLevel());

        Designation updatedDesignation = designationRepository.save(existingDesignation);
        return ResponseEntity.ok(mapToDto(updatedDesignation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDesignation(@PathVariable Long id) {
        if (!designationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Designation not found with id: " + id);
        }
        designationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Helper Methods ---
    private DesignationDto mapToDto(Designation designation) {
        DesignationDto dto = new DesignationDto();
        dto.setDesignationId(designation.getDesignationId());
        dto.setDesignationName(designation.getDesignationName());
        dto.setDesignationCode(designation.getDesignationCode());
        dto.setLevel(designation.getLevel());
        return dto;
    }

    private Designation mapToEntity(DesignationDto dto) {
        Designation designation = new Designation();
        designation.setDesignationName(dto.getDesignationName());
        designation.setDesignationCode(dto.getDesignationCode());
        designation.setLevel(dto.getLevel());
        return designation;
    }
}
