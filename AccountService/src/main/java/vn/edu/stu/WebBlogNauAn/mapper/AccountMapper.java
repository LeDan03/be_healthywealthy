package vn.edu.stu.WebBlogNauAn.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.edu.stu.WebBlogNauAn.dto.AdminRegisterDto;
import vn.edu.stu.WebBlogNauAn.dto.RegisterDto;
import vn.edu.stu.WebBlogNauAn.model.Account;
import vn.edu.stu.WebBlogNauAn.response.AccountResponse;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public AccountMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Account toAccount(RegisterDto registerDto) {
        return modelMapper.map(registerDto, Account.class);
    }

    public Account toAccount(AdminRegisterDto adminRegisterDto) {
        return modelMapper.map(adminRegisterDto, Account.class);
    }

    public RegisterDto toDto(Account account) {
        return modelMapper.map(account, RegisterDto.class);
    }

    public AccountResponse toAccountResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .avatarUrl(account.getAvatarUrl())
                .email(account.getEmail())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .status(account.getStatus())
                .role(account.getRole().getName())
                .build();
    }

    public List<AccountResponse> toAccountResponseList(List<Account> accounts) {
        return accounts.stream().map(this::toAccountResponse).collect(Collectors.toList());
    }
}
