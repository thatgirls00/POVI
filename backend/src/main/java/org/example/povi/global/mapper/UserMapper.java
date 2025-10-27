package org.example.povi.global.mapper;

import org.example.povi.domain.user.dto.ProfileRes;
import org.example.povi.domain.user.dto.ProfileUpdateReq;
import org.example.povi.domain.user.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring") // 스프링 빈으로 등록
public interface UserMapper {

    // DTO의 필드가 null이면, 엔티티의 해당 필드를 무시(업데이트 x)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(@MappingTarget User user, ProfileUpdateReq reqDto);

    @AfterMapping
    default void applyCustomUpdates(@MappingTarget User user, ProfileUpdateReq reqDto) {
        if (reqDto.nickname() != null) {
            user.updateNickname(reqDto.nickname());
        }
        if (reqDto.bio() != null) {
            user.updateBio(reqDto.bio());
        }
    }

    ProfileRes toProfileRes(User user);
}