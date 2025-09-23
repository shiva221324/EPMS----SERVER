package com.epms.payroll.Controller;

import com.epms.payroll.Entities.Role;
import com.epms.payroll.Repositories.RoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Create a new role
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        if(roleRepository.existsByName(role.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Role savedRole = roleRepository.save(role);
        return new ResponseEntity<>(savedRole, HttpStatus.CREATED);
    }

    // Get all roles
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return ResponseEntity.ok(roles);
    }

    // Get role by id
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        return roleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete role by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        if (!roleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        roleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
