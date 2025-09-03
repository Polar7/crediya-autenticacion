package co.com.pragma.authentication.r2dbc;

import co.com.pragma.authentication.model.rol.Rol;
import co.com.pragma.authentication.model.rol.gateways.RolRepository;
import co.com.pragma.authentication.model.user.User;
import co.com.pragma.authentication.model.user.gateways.UserRepository;
import co.com.pragma.authentication.r2dbc.entity.RolEntity;
import co.com.pragma.authentication.r2dbc.entity.UserEntity;
import co.com.pragma.authentication.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class RolReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Rol,
        RolEntity,
        Long,
        RolReactiveRepository
> implements RolRepository {

    public RolReactiveRepositoryAdapter(RolReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Rol.class));
    }

    @Override
    public Mono<Rol> findByName(String name) {
        return repository.findByName(name).map(this::toEntity);
    }

}
