package com.epms.payroll.Controller;

import com.epms.payroll.Dto.DesignationDto;
import com.epms.payroll.Entities.Designation;
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
        Designation designation = mapToEntity(designationDto);
        Designation savedDesignation = designationRepository.save(designation);
        return new ResponseEntity<>(mapToDto(savedDesignation), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DesignationDto> getDesignationById(@PathVariable Long id) {
        return designationRepository.findById(id)
                .map(designation -> ResponseEntity.ok(mapToDto(designation)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<DesignationDto>> getAllDesignations() {
        List<Designation> designations = designationRepository.findAll();
        List<DesignationDto> designationDtos = designations.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(designationDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DesignationDto> updateDesignation(@PathVariable Long id, @RequestBody DesignationDto designationDto) {
        return designationRepository.findById(id)
                .map(existingDesignation -> {
                    existingDesignation.setDesignationName(designationDto.getDesignationName());
                    existingDesignation.setDesignationCode(designationDto.getDesignationCode());
                    existingDesignation.setLevel(designationDto.getLevel());
                    Designation updatedDesignation = designationRepository.save(existingDesignation);
                    return ResponseEntity.ok(mapToDto(updatedDesignation));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDesignation(@PathVariable Long id) {
        if (!designationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        designationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Helper Methods for Mapping ---

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