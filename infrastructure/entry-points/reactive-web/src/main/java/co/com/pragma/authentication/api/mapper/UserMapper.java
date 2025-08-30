package co.com.pragma.authentication.api.mapper;

import co.com.pragma.authentication.api.dto.SaveUserRequestDTO;
import co.com.pragma.authentication.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toModel(SaveUserRequestDTO saveUserRequestDTO);

}
