package co.com.pragma.authentication.r2dbc;

import co.com.pragma.authentication.r2dbc.entity.RolEntity;
import co.com.pragma.authentication.r2dbc.entity.UserEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RolReactiveRepository extends ReactiveCrudRepository<RolEntity, Long>, ReactiveQueryByExampleExecutor<RolEntity> {

    Mono<RolEntity> findByName(String name);

}
