package co.com.pragma.authentication.r2dbc;

import co.com.pragma.authentication.model.user.User;
import co.com.pragma.authentication.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @Mock
    private UserReactiveRepository userReactiveRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserReactiveRepositoryAdapter userAdapter;

    private User sampleUser;
    private UserEntity sampleUserEntity;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .name("Jane")
                .email("jane.doe@example.com")
                .salaryBase(new BigDecimal("2500000"))
                .build();

        sampleUserEntity = UserEntity.builder()
                .id(1L)
                .name("Jane")
                .email("jane.doe@example.com")
                .salaryBase(new BigDecimal("2500000"))
                .build();
    }

    @Test
    void shouldSaveUserSuccessfully() {
        // Mock the repository save call to return the UserEntity
        when(userReactiveRepository.save(any(UserEntity.class))).thenReturn(Mono.just(sampleUserEntity));
        when(objectMapper.map(any(User.class), any(Class.class))).thenReturn(sampleUserEntity);
        when(objectMapper.map(any(UserEntity.class), any(Class.class))).thenReturn(sampleUser);

        // Call the adapter's save method
        Mono<User> result = userAdapter.save(sampleUser);

        // Verify the result with StepVerifier
        StepVerifier.create(result)
                .expectNextMatches(savedUser -> savedUser.getEmail().equals(sampleUser.getEmail()))
                .verifyComplete();

        // Verify that the repository's save method was called exactly once
        verify(userReactiveRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void findByEmail_shouldReturnUser_whenFound() {

        // Mock the reactive repository call
        when(userReactiveRepository.findByEmail(sampleUser.getEmail())).thenReturn(Mono.just(sampleUserEntity));

        // Mock the ObjectMapper call only for this test
        when(objectMapper.map(any(UserEntity.class), any(Class.class))).thenReturn(sampleUser);

        StepVerifier.create(userAdapter.findByEmail(sampleUser.getEmail()))
                .expectNext(sampleUser)
                .verifyComplete();
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenNotFound() {
        // Mock the reactive repository call, which is the only dependency needed here
        when(userReactiveRepository.findByEmail(any(String.class))).thenReturn(Mono.empty());

        StepVerifier.create(userAdapter.findByEmail("non-existent@email.com"))
                .expectNextCount(0)
                .verifyComplete();
    }

}
