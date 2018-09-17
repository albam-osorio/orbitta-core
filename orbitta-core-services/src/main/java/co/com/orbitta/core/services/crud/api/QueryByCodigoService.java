package co.com.orbitta.core.services.crud.api;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import co.com.orbitta.core.domain.ObjectWithCode;


public interface QueryByCodigoService<M extends ObjectWithCode<ID>, ID> {

	@Transactional(readOnly = true)
	Optional<M> findByCodigo(String codigo);

}