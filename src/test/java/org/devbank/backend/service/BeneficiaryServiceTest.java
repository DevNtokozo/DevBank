package org.devbank.backend.service;

import org.devbank.backend.dto.BeneficiaryRequest;
import org.devbank.backend.dto.BeneficiaryResponse;
import org.devbank.backend.entity.Account;
import org.devbank.backend.entity.Beneficiary;
import org.devbank.backend.entity.User;
import org.devbank.backend.exception.BadRequestException;
import org.devbank.backend.exception.ForbiddenException;
import org.devbank.backend.exception.NotFoundException;
import org.devbank.backend.repository.AccountRepository;
import org.devbank.backend.repository.BeneficiaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficiaryServiceTest {

    @Mock
    private BeneficiaryRepository beneficiaryRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private BeneficiaryService beneficiaryService;

    private User user;
    private User anotherUser;

    private Account sourceAccount;
    private Account destinationAccount;

    private Beneficiary beneficiary;

    @BeforeEach
    void setUp() {

        user = new User(
                "testuser",
                "Test@123",
                "CUSTOMER",
                "ACTIVE"
        );

        user.setId(1L);

        anotherUser = new User(
                "anotheruser",
                "Test@123",
                "CUSTOMER",
                "ACTIVE"
        );

        anotherUser.setId(2L);

        sourceAccount = new Account();

        sourceAccount.setId(1L);
        sourceAccount.setUser(user);
        sourceAccount.setAccountNumber("1000000001");
        sourceAccount.setAccountType("CHEQUE");
        sourceAccount.setBalance(
                new java.math.BigDecimal("25000.00")
        );
        sourceAccount.setStatus("ACTIVE");

        destinationAccount = new Account();

        destinationAccount.setId(2L);
        destinationAccount.setUser(anotherUser);
        destinationAccount.setAccountNumber("1000000003");
        destinationAccount.setAccountType("CHEQUE");
        destinationAccount.setBalance(
                new java.math.BigDecimal("15000.00")
        );
        destinationAccount.setStatus("ACTIVE");

        beneficiary = new Beneficiary();

        beneficiary.setId(1L);
        beneficiary.setAccount(sourceAccount);
        beneficiary.setName("Recipient");
        beneficiary.setAccountNumber("1000000003");
        beneficiary.setBankName("Dev Bank");
        beneficiary.setStatus("ACTIVE");
        beneficiary.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldGetBeneficiariesForAccount() {

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(beneficiaryRepository.findByAccountId(1L))
                .thenReturn(List.of(beneficiary));

        List<BeneficiaryResponse> result =
                beneficiaryService.getBeneficiaries(
                        1L,
                        1L
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        BeneficiaryResponse response = result.get(0);

        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "Recipient",
                response.getName()
        );

        assertEquals(
                "1000000003",
                response.getAccountNumber()
        );

        assertEquals(
                "Dev Bank",
                response.getBankName()
        );

        assertEquals(
                "ACTIVE",
                response.getStatus()
        );

        assertNotNull(response.getCreatedAt());

        verify(accountRepository).findById(1L);

        verify(beneficiaryRepository)
                .findByAccountId(1L);
    }

    @Test
    void shouldRejectUnknownAccount() {

        when(accountRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> beneficiaryService.getBeneficiaries(
                        999L,
                        1L
                )
        );

        verify(accountRepository).findById(999L);

        verify(
                beneficiaryRepository,
                never()
        ).findByAccountId(anyLong());
    }

    @Test
    void shouldRejectAccessToAnotherUsersAccount() {

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        assertThrows(
                ForbiddenException.class,
                () -> beneficiaryService.getBeneficiaries(
                        1L,
                        2L
                )
        );

        verify(accountRepository).findById(1L);

        verify(
                beneficiaryRepository,
                never()
        ).findByAccountId(anyLong());
    }

    @Test
    void shouldAddBeneficiary() {

        BeneficiaryRequest request =
                new BeneficiaryRequest();

        request.setName("Recipient");
        request.setAccountNumber("1000000003");
        request.setBankName("Dev Bank");

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(beneficiaryRepository
                .existsByAccountIdAndAccountNumber(
                        1L,
                        "1000000003"
                ))
                .thenReturn(false);

        when(accountRepository.findByAccountNumber(
                "1000000003"
        )).thenReturn(Optional.of(destinationAccount));

        when(beneficiaryRepository.save(any(Beneficiary.class)))
                .thenReturn(beneficiary);

        BeneficiaryResponse result =
                beneficiaryService.addBeneficiary(
                        1L,
                        request,
                        1L
                );

        assertNotNull(result);

        assertEquals(
                1L,
                result.getId()
        );

        assertEquals(
                "Recipient",
                result.getName()
        );

        assertEquals(
                "1000000003",
                result.getAccountNumber()
        );

        assertEquals(
                "Dev Bank",
                result.getBankName()
        );

        assertEquals(
                "ACTIVE",
                result.getStatus()
        );

        verify(beneficiaryRepository)
                .existsByAccountIdAndAccountNumber(
                        1L,
                        "1000000003"
                );

        verify(accountRepository)
                .findByAccountNumber("1000000003");

        verify(beneficiaryRepository)
                .save(any(Beneficiary.class));
    }

    @Test
    void shouldRejectDuplicateBeneficiary() {

        BeneficiaryRequest request =
                new BeneficiaryRequest();

        request.setName("Recipient");
        request.setAccountNumber("1000000003");
        request.setBankName("Dev Bank");

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(beneficiaryRepository
                .existsByAccountIdAndAccountNumber(
                        1L,
                        "1000000003"
                ))
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> beneficiaryService.addBeneficiary(
                        1L,
                        request,
                        1L
                )
        );

        verify(
                beneficiaryRepository,
                never()
        ).save(any(Beneficiary.class));

        verify(
                accountRepository,
                never()
        ).findByAccountNumber(anyString());
    }

    @Test
    void shouldRejectInactiveBeneficiaryAccount() {

        BeneficiaryRequest request =
                new BeneficiaryRequest();

        request.setName("Recipient");
        request.setAccountNumber("1000000003");
        request.setBankName("Dev Bank");

        destinationAccount.setStatus("INACTIVE");

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(beneficiaryRepository
                .existsByAccountIdAndAccountNumber(
                        1L,
                        "1000000003"
                ))
                .thenReturn(false);

        when(accountRepository.findByAccountNumber(
                "1000000003"
        )).thenReturn(Optional.of(destinationAccount));

        assertThrows(
                BadRequestException.class,
                () -> beneficiaryService.addBeneficiary(
                        1L,
                        request,
                        1L
                )
        );

        verify(
                beneficiaryRepository,
                never()
        ).save(any(Beneficiary.class));
    }

    @Test
    void shouldRejectOwnAccountAsBeneficiary() {

        BeneficiaryRequest request =
                new BeneficiaryRequest();

        request.setName("My Own Account");
        request.setAccountNumber("1000000001");
        request.setBankName("Dev Bank");

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(beneficiaryRepository
                .existsByAccountIdAndAccountNumber(
                        1L,
                        "1000000001"
                ))
                .thenReturn(false);

        when(accountRepository.findByAccountNumber(
                "1000000001"
        )).thenReturn(Optional.of(sourceAccount));

        assertThrows(
                BadRequestException.class,
                () -> beneficiaryService.addBeneficiary(
                        1L,
                        request,
                        1L
                )
        );

        verify(
                beneficiaryRepository,
                never()
        ).save(any(Beneficiary.class));
    }

    @Test
    void shouldDeleteBeneficiary() {

        when(beneficiaryRepository.findById(1L))
                .thenReturn(Optional.of(beneficiary));

        assertDoesNotThrow(() ->
                beneficiaryService.deleteBeneficiary(
                        1L,
                        1L
                )
        );

        verify(beneficiaryRepository)
                .findById(1L);

        verify(beneficiaryRepository)
                .delete(beneficiary);
    }

    @Test
    void shouldRejectDeletingUnknownBeneficiary() {

        when(beneficiaryRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> beneficiaryService.deleteBeneficiary(
                        999L,
                        1L
                )
        );

        verify(beneficiaryRepository)
                .findById(999L);

        verify(
                beneficiaryRepository,
                never()
        ).delete(any(Beneficiary.class));
    }

    @Test
    void shouldRejectDeletingAnotherUsersBeneficiary() {

        when(beneficiaryRepository.findById(1L))
                .thenReturn(Optional.of(beneficiary));

        assertThrows(
                ForbiddenException.class,
                () -> beneficiaryService.deleteBeneficiary(
                        1L,
                        2L
                )
        );

        verify(beneficiaryRepository)
                .findById(1L);

        verify(
                beneficiaryRepository,
                never()
        ).delete(any(Beneficiary.class));
    }
}