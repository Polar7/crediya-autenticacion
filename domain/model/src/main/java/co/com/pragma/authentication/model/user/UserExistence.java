package co.com.pragma.authentication.model.user;

public record UserExistence(boolean found,
                            String email) {
}
