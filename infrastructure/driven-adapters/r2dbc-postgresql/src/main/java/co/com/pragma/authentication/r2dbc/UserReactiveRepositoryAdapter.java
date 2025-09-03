package co.com.pragma.authentication.r2dbc;

import co.com.pragma.authentication.model.user.User;
import co.com.pragma.authentication.model.user.gateways.UserRepository;
import co.com.pragma.authentication.r2dbc.entity.UserEntity;
import co.com.pragma.authentication.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    User,
    UserEntity,
    Long,
    UserReactiveRepository
> implements UserRepository {

    private final TransactionalOperator transactionalOperator;

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<User> save(User newUser) {
        return super.save(newUser).as(transactionalOperator::transactional);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email).map(this::toEntity);
    }

    @Override
    public Mono<User> findByDocNumber(String docNumber) {
        return repository.findByDocNumber(docNumber).map(this::toEntity);
    }

}
