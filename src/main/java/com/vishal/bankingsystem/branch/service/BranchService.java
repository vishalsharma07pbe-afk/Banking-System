package com.vishal.bankingsystem.branch.service;
import com.vishal.bankingsystem.branch.dto.BranchDto;
import java.util.List;

public interface BranchService {
    BranchDto createBranch(BranchDto branchDto);
    BranchDto getBranchById(Long id);
    List<BranchDto> getAllBranches();
    BranchDto updateBranch(Long id, BranchDto branchDto);
    void deleteBranch(Long id);
    BranchDto findByIfscCode(String IfscCode);
    BranchDto findByBranchCode(String branchCode);
}
