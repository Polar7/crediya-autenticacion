package co.com.pragma.authentication.usecase.user;

import co.com.pragma.authentication.model.rol.Rol;
import co.com.pragma.authentication.model.rol.RolEnum;
import co.com.pragma.authentication.model.rol.gateways.RolRepository;
import co.com.pragma.authentication.model.user.User;
import co.com.pragma.authentication.model.user.UserExistence;
import co.com.pragma.authentication.model.user.UserInfo;
import co.com.pragma.authentication.model.user.gateways.UserRepository;
import co.com.pragma.authentication.usecase.exception.InvalidInputDataException;
import co.com.pragma.authentication.usecase.exception.NotFoundException;
import co.com.pragma.authentication.usecase.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    private final RolRepository rolRepository;

    public Mono<UserExistence> findEmailUserByDocNumber(String docNumber) {
        return userRepository.findByDocNumber(docNumber)
                .map(user -> new UserExistence(true, user.getId(), user.getEmail()))
                .switchIfEmpty(Mono.just(new UserExistence(false, null, null)));
    }

    public Mono<Void> saveUser(User newUser) {
        return validateRequiredFields(newUser)
                .then(Mono.defer(() -> validateSalary(newUser)))
                .then(Mono.defer(() -> validateEmailDomain(newUser)))
                .then(Mono.defer(() -> userRepository.findByEmail(newUser.getEmail())))
                .flatMap(existingUser -> Mono.error(new UserAlreadyExistsException("The email already exists.")))
                .switchIfEmpty(
                        Mono.defer(this::getIdRolClient)
                                .flatMap(idRol -> {
                                    newUser.setRolId(idRol);
                                    newUser.setPassword(UUID.randomUUID().toString());
                                    return userRepository.save(newUser);
                                })
                )
                .then();
    }

    public Mono<List<UserInfo>> findUsersByEmails(List<String> emails) {
        return userRepository.findAllByEmails(emails)
                .map(user -> new UserInfo(user.getEmail(), user.getName() + " " + user.getSurname(), user.getSalaryBase()))
                .collectList();
    }

    private Mono<Void> validateRequiredFields(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return Mono.error(new InvalidInputDataException("Name cannot be null or empty."));
        }
        if (user.getSurname() == null || user.getSurname().trim().isEmpty()) {
            return Mono.error(new InvalidInputDataException("Surname cannot be null or empty."));
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return Mono.error(new InvalidInputDataException("Email cannot be null or empty."));
        }
        if (user.getSalaryBase() == null) {
            return Mono.error(new InvalidInputDataException("Salary base cannot be null."));
        }
        return Mono.empty();
    }

    private Mono<Void> validateSalary(User user) {
        if (user.getSalaryBase().compareTo(new BigDecimal(15000000)) > 0) {
            return Mono.error(new InvalidInputDataException("The salary cannot exceed 15'000.000"));
        }
        return Mono.empty();
    }

    private Mono<Void> validateEmailDomain(User user) {
        List<String> validDomains = Arrays.asList("@bancolombia.com", "@pragma.com");
        String userEmail = user.getEmail();

        if (validDomains.stream().noneMatch(userEmail::endsWith)) {
            return Mono.error(new InvalidInputDataException("The email must be from an authorized domain."));
        }
        return Mono.empty();
    }

    private Mono<Long> getIdRolClient() {
        return rolRepository.findByName(RolEnum.CLIENT.getName())
                .switchIfEmpty(Mono.error(new NotFoundException("Rol not found")))
                .map(Rol::getId);
    }

}
