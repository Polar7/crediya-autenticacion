package co.com.pragma.authentication.model.user;

public record UserExistence(boolean found,
                            Long id,
                            String email) {
}
