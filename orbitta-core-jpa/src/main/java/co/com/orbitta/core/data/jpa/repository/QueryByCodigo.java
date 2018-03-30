package co.com.orbitta.core.data.jpa.repository;

import java.io.Serializable;
import java.util.Optional;

import co.com.orbitta.core.dto.IdentifiedDomainObject;
import co.com.orbitta.core.dto.ObjectWithCode;

public interface QueryByCodigo<E extends IdentifiedDomainObject<ID> & ObjectWithCode, ID extends Serializable>{
	Optional<E> findByCodigo(String codigo);
}