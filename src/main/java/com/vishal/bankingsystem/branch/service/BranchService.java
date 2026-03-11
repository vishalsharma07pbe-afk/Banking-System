package com.vishal.bankingsystem.branch.service;
import com.vishal.bankingsystem.branch.dto.BranchDto;
import java.util.List;

public interface BranchService {
    BranchDto createBranch(BranchDto branchDto);
    BranchDto getBranchById(Long branchId);
    List<BranchDto> getAllBranches();
    BranchDto updateBranch(Long branchId, BranchDto branchDto);
    void closeBranch(Long branchId);
    BranchDto getBranchByIfscCode(String ifscCode);
    BranchDto getBranchByBranchCode(String branchCode);
}
