package com.vishal.bankingsystem.branch.service;

import com.vishal.bankingsystem.branch.enums.BranchStatus;
import com.vishal.bankingsystem.branch.repository.BranchRepository;
import com.vishal.bankingsystem.branch.dto.BranchDto;
import com.vishal.bankingsystem.branch.entity.Branch;
import com.vishal.bankingsystem.branch.mapper.BranchMapper;
import com.vishal.bankingsystem.exception.ConflictException;
import com.vishal.bankingsystem.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BranchServiceImpl implements BranchService{
    private final BranchRepository branchRepository;

    public BranchServiceImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    public BranchDto createBranch(BranchDto branchDto) {
        Optional<Branch> existingBranch = branchRepository.findByBranchCode(branchDto.getBranchCode());
        Branch branch;

        if (existingBranch.isPresent()) {
            branch = existingBranch.get();
            if (branch.getStatus() != BranchStatus.CLOSED) {
                throw new ConflictException("Branch already exists and is not closed");
            }
        } else {
            branch = BranchMapper.dtoToEntity(branchDto);
        }

        branch.setBranchName(branchDto.getBranchName());
        branch.setIfscCode(branchDto.getIfscCode());
        branch.setPhoneNumber(branchDto.getPhoneNumber());
        branch.setEmail(branchDto.getEmail());
        branch.setCity(branchDto.getCity());
        branch.setState(branchDto.getState());
        branch.setCountry(branchDto.getCountry());
        branch.setPostalCode(branchDto.getPostalCode());
        branch.setStatus(BranchStatus.ACTIVE);

        Branch saved = branchRepository.save(branch);
        return BranchMapper.entityToDto(saved);
    }

    @Override
    public BranchDto getBranchById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        return BranchMapper.entityToDto(branch);
    }

    @Override
    public List<BranchDto> getAllBranches() {
        return branchRepository.findAll()
                .stream()
                .filter(branch -> branch.getStatus() == BranchStatus.ACTIVE)
                .map(BranchMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BranchDto updateBranch(Long id, BranchDto branchDto) {

        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        branch.setBranchName(branchDto.getBranchName());
        branch.setPhoneNumber(branchDto.getPhoneNumber());
        branch.setEmail(branchDto.getEmail());
        branch.setCity(branchDto.getCity());
        branch.setState(branchDto.getState());
        branch.setCountry(branchDto.getCountry());
        branch.setPostalCode(branchDto.getPostalCode());
        branch.setStatus(branchDto.getStatus());
        Branch saved = branchRepository.save(branch);
        return BranchMapper.entityToDto(saved);
    }

    @Override
    public void deleteBranch(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        branch.setStatus(BranchStatus.CLOSED);
        branchRepository.save(branch);
    }

    @Override
    public BranchDto findByIfscCode(String IfscCode) {
        Branch branch = branchRepository.findByIfscCode(IfscCode)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found for IFSC code: " + IfscCode));
        return BranchMapper.entityToDto(branch);
    }

    @Override
    public BranchDto findByBranchCode(String branchCode) {
        Branch branch = branchRepository.findByBranchCode(branchCode)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found for code: " + branchCode));
        return BranchMapper.entityToDto(branch);
    }
}
