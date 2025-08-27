package co.com.pragma.authentication.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationUtilTest {

    @Mock
    private Validator validator;

    @InjectMocks
    private ValidationUtil validationUtil;

    record TestObject(String name) {}

    @Test
    void shouldReturnMonoJustWhenValidationPasses() {
        // Objeto a validar
        TestObject validObject = new TestObject("TestName");

        // Simula que el validador retorna un conjunto de errores vacío
        when(validator.validate(any(TestObject.class))).thenReturn(Collections.emptySet());

        // Verifica que el Mono contiene el objeto y se completa correctamente
        StepVerifier.create(validationUtil.validate(validObject))
                .expectNext(validObject)
                .verifyComplete();
    }

    @Test
    void shouldReturnMonoErrorWhenValidationFails() {
        // Objeto a validar
        TestObject invalidObject = new TestObject(null);

        // Crea un mock para la violación de la restricción
        ConstraintViolation<TestObject> violationMock = mock(ConstraintViolation.class);

        // Simula el comportamiento del mock de la violación
        when(violationMock.getMessage()).thenReturn("cannot be null");
        Path pathMock = mock(Path.class);
        when(pathMock.toString()).thenReturn("name");
        when(violationMock.getPropertyPath()).thenReturn(pathMock);

        // Simula que el validador retorna un conjunto de errores
        Set<ConstraintViolation<TestObject>> errors = Set.of(violationMock);
        when(validator.validate(any(TestObject.class))).thenReturn(errors);

        // Verifica que el Mono lanza la excepción y contiene el mensaje correcto
        StepVerifier.create(validationUtil.validate(invalidObject))
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("name cannot be null")
                )
                .verify();
    }

}