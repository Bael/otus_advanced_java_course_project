package com.github.bael.otus.java.userservice.rest.mapper;


import com.github.bael.otus.java.userservice.entity.UserAccount;
import com.github.bael.otus.java.userservice.rest.dto.UserAccountCreateRequest;
import com.github.bael.otus.java.userservice.rest.dto.UserAccountResponse;
import com.github.bael.otus.java.userservice.rest.dto.UserAccountUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserAccountMapper {

    UserAccount toEntity(UserAccountCreateRequest request);

    UserAccountResponse toResponse(UserAccount user);

    List<UserAccountResponse> toResponseList(List<UserAccount> users);

    void updateEntityFromRequest(UserAccountUpdateRequest request, @MappingTarget UserAccount user);
}
