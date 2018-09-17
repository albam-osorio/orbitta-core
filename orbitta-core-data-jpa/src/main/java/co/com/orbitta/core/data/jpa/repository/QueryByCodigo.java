package co.com.orbitta.core.data.jpa.repository;

import java.util.Optional;

import co.com.orbitta.core.domain.ObjectWithCode;

public interface QueryByCodigo<E extends ObjectWithCode<ID>, ID>{
	Optional<E> findByCodigo(String codigo);
}