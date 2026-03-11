package com.vishal.bankingsystem.branch.controller;
import com.vishal.bankingsystem.branch.dto.BranchDto;
import com.vishal.bankingsystem.branch.service.BranchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_ADMIN')")
    @PostMapping
    public BranchDto createBranch(@Valid @RequestBody BranchDto branchDto) {
        return branchService.createBranch(branchDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_ADMIN','OPERATIONS_OFFICER','BRANCH_MANAGER','INTERNAL_AUDITOR')")
    @GetMapping("/{id}")
    public BranchDto getBranch(@PathVariable Long id) {
        return branchService.getBranchById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_ADMIN','OPERATIONS_OFFICER','BRANCH_MANAGER','INTERNAL_AUDITOR')")
    @GetMapping
    public List<BranchDto> getAllBranches() {
        return branchService.getAllBranches();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public BranchDto updateBranch(@PathVariable Long id,
                                  @Valid @RequestBody BranchDto branchDto) {
        return branchService.updateBranch(id, branchDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteBranch(@PathVariable Long id) {
        branchService.closeBranch(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_ADMIN','OPERATIONS_OFFICER','BRANCH_MANAGER','INTERNAL_AUDITOR')")
    @GetMapping("/code/{branchCode}")
    public ResponseEntity<BranchDto> getByBranchCode(@PathVariable String branchCode){
        return ResponseEntity.ok(branchService.getBranchByBranchCode(branchCode));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SECURITY_ADMIN','OPERATIONS_OFFICER','BRANCH_MANAGER','INTERNAL_AUDITOR')")
    @GetMapping("/ifsc/{ifscCode}")
    public ResponseEntity<BranchDto> getByIfscCode(@PathVariable String ifscCode){
        return ResponseEntity.ok(branchService.getBranchByIfscCode(ifscCode));
    }
}
