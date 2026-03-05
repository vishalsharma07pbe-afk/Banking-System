package com.vishal.bankingsystem.branch.mapper;
import com.vishal.bankingsystem.branch.dto.BranchDto;
import com.vishal.bankingsystem.branch.entity.Branch;

public class BranchMapper {

    public static BranchDto entityToDto(Branch branch) {

        return new BranchDto(
                branch.getBranchCode(),
                branch.getBranchName(),
                branch.getIfscCode(),
                branch.getPhoneNumber(),
                branch.getEmail(),
                branch.getCity(),
                branch.getState(),
                branch.getCountry(),
                branch.getPostalCode(),
                branch.getStatus()
        );
    }

    public static Branch dtoToEntity(BranchDto dto) {

        Branch branch = new Branch();

        branch.setBranchCode(dto.getBranchCode());
        branch.setBranchName(dto.getBranchName());
        branch.setIfscCode(dto.getIfscCode());
        branch.setPhoneNumber(dto.getPhoneNumber());
        branch.setEmail(dto.getEmail());
        branch.setCity(dto.getCity());
        branch.setState(dto.getState());
        branch.setCountry(dto.getCountry());
        branch.setPostalCode(dto.getPostalCode());
        branch.setStatus(dto.getStatus());

        return branch;
    }
}
