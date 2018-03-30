package co.com.orbitta.core.services.crud.api;

import java.io.Serializable;
import java.util.Optional;

import co.com.orbitta.core.dto.IdentifiedDomainObject;

public interface QueryByCodigoService<M extends IdentifiedDomainObject<ID>, ID extends Serializable> {
	Optional<M> findByCodigo(String codigo);
}
