package co.com.pragma.authentication.api.dto;

public record UserExistenceResponseDto(boolean found,
                                       String email) {
}
