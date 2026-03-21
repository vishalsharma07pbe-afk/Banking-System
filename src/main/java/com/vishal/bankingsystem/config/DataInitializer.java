package com.vishal.bankingsystem.config;

import com.vishal.bankingsystem.auth.entity.PermissionEntity;
import com.vishal.bankingsystem.auth.entity.RoleEntity;
import com.vishal.bankingsystem.auth.repository.PermissionRepository;
import com.vishal.bankingsystem.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@ConditionalOnProperty(name = "app.bootstrap.rbac.enabled", havingValue = "true")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // to prevent duplicate data interactions everytime app runs
        if (permissionRepository.count()>0){
            return;
        }

        //Permissions
        PermissionEntity viewAccount = permissionRepository.save(new PermissionEntity(null,"VIEW_ACCOUNT"));
        PermissionEntity createAccount = permissionRepository.save(new PermissionEntity(null,"CREATE_ACCOUNT"));
        PermissionEntity deposit = permissionRepository.save(new PermissionEntity(null,"DEPOSIT"));
        PermissionEntity withdraw = permissionRepository.save(new PermissionEntity(null,"WITHDRAW"));
        PermissionEntity transfer = permissionRepository.save(new PermissionEntity(null,"TRANSFER"));
        PermissionEntity approveTransfer = permissionRepository.save(new PermissionEntity(null,"APPROVE_LARGE_TRANSFER"));
        PermissionEntity freezeAccount = permissionRepository.save(new PermissionEntity(null,"FREEZE_ACCOUNT"));
        PermissionEntity unfreezeAccount = permissionRepository.save(new PermissionEntity(null,"UNFREEZE_ACCOUNT"));
        PermissionEntity viewTransactions = permissionRepository.save(new PermissionEntity(null,"VIEW_TRANSACTION_HISTORY"));
        PermissionEntity viewAudit = permissionRepository.save(new PermissionEntity(null,"VIEW_AUDIT_LOGS"));
        PermissionEntity createUser = permissionRepository.save(new PermissionEntity(null,"CREATE_USER"));
        PermissionEntity updateUser = permissionRepository.save(new PermissionEntity(null,"UPDATE_USER"));
        PermissionEntity deleteUser = permissionRepository.save(new PermissionEntity(null,"DELETE_USER"));
        PermissionEntity assignRole = permissionRepository.save(new PermissionEntity(null,"ASSIGN_ROLE"));
        PermissionEntity resetPassword = permissionRepository.save(new PermissionEntity(null,"RESET_PASSWORD"));
        PermissionEntity unlockAccount = permissionRepository.save(new PermissionEntity(null,"UNLOCK_ACCOUNT"));
        PermissionEntity flagFraud = permissionRepository.save(new PermissionEntity(null,"FLAG_FRAUD"));
        PermissionEntity investigateFraud = permissionRepository.save(new PermissionEntity(null,"INVESTIGATE_FRAUD"));

        //Roles
        RoleEntity customer = new RoleEntity();
        customer.setName("CUSTOMER");
        customer.setPermissions(Set.of(createAccount, viewAccount, transfer, viewTransactions));

        RoleEntity preCustomer = new RoleEntity();
        preCustomer.setName("PRECUSTOMER");
        preCustomer.setPermissions(Set.of(createAccount));

        RoleEntity corporateCustomer = new RoleEntity();
        corporateCustomer.setName("CORPORATE_CUSTOMER");
        corporateCustomer.setPermissions(Set.of(viewAccount, transfer, viewTransactions));

        RoleEntity teller = new RoleEntity();
        teller.setName("TELLER");
        teller.setPermissions(Set.of(viewAccount, deposit, withdraw, viewTransactions));

        RoleEntity branchManager = new RoleEntity();
        branchManager.setName("BRANCH_MANAGER");
        branchManager.setPermissions(Set.of(viewAccount, approveTransfer, freezeAccount, unfreezeAccount, viewTransactions));

        RoleEntity operationsOfficer = new RoleEntity();
        operationsOfficer.setName("OPERATIONS_OFFICER");
        operationsOfficer.setPermissions(Set.of(createAccount, viewAccount, viewTransactions));

        RoleEntity supportAgent = new RoleEntity();
        supportAgent.setName("SUPPORT_AGENT");
        supportAgent.setPermissions(Set.of(resetPassword, unlockAccount, viewAccount));

        RoleEntity complianceOfficer = new RoleEntity();
        complianceOfficer.setName("COMPLIANCE_OFFICER");
        complianceOfficer.setPermissions(Set.of(viewAccount, viewTransactions, flagFraud));

        RoleEntity fraudAnalyst = new RoleEntity();
        fraudAnalyst.setName("FRAUD_ANALYST");
        fraudAnalyst.setPermissions(Set.of(viewAccount, viewTransactions, flagFraud, investigateFraud));

        RoleEntity auditor = new RoleEntity();
        auditor.setName("INTERNAL_AUDITOR");
        auditor.setPermissions(Set.of(viewAudit, viewTransactions, viewAccount));

        RoleEntity admin = new RoleEntity();
        admin.setName("ADMIN");
        admin.setPermissions(Set.of(createUser, updateUser, deleteUser, assignRole, viewAccount));

        RoleEntity securityAdmin = new RoleEntity();
        securityAdmin.setName("SECURITY_ADMIN");
        securityAdmin.setPermissions(Set.of(createUser, resetPassword, unlockAccount, assignRole));


        roleRepository.saveAll(List.of(
                customer,
                preCustomer,
                corporateCustomer,
                teller,
                branchManager,
                operationsOfficer,
                supportAgent,
                complianceOfficer,
                fraudAnalyst,
                auditor,
                admin,
                securityAdmin
        ));
    }
}
