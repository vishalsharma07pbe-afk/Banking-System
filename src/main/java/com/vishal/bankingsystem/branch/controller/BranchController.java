package com.vishal.bankingsystem.branch.controller;
import com.vishal.bankingsystem.branch.dto.BranchDto;
import com.vishal.bankingsystem.branch.service.BranchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @PostMapping
    public BranchDto createBranch(@Valid @RequestBody BranchDto branchDto) {
        return branchService.createBranch(branchDto);
    }

    @GetMapping("/{id}")
    public BranchDto getBranch(@PathVariable Long id) {
        return branchService.getBranchById(id);
    }

    @GetMapping
    public List<BranchDto> getAllBranches() {
        return branchService.getAllBranches();
    }

    @PutMapping("/{id}")
    public BranchDto updateBranch(@PathVariable Long id,
                                  @Valid @RequestBody BranchDto branchDto) {
        return branchService.updateBranch(id, branchDto);
    }

    @DeleteMapping("/{id}")
    public void deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
    }

    @GetMapping("/code/{branchCode}")
    public ResponseEntity<BranchDto> getByBranchCode(@PathVariable String branchCode){
        return ResponseEntity.ok(branchService.findByBranchCode(branchCode));
    }

    @GetMapping("/ifsc/{ifscCode}")
    public ResponseEntity<BranchDto> getByIfscCode(@PathVariable String ifscCode){
        return ResponseEntity.ok(branchService.findByIfscCode(ifscCode));
    }
}
