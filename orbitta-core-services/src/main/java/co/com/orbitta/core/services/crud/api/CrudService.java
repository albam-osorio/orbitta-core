package co.com.orbitta.core.services.crud.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import co.com.orbitta.core.dto.IdentifiedDomainObject;

@Transactional(readOnly = true)
public interface CrudService<M extends IdentifiedDomainObject<ID>, ID extends Serializable> {

	M findOneById(ID id);

	Optional<M> findById(ID id);

	List<M> findAllById(Collection<ID> ids);

	@Transactional
	M create(M model);

	@Transactional
	List<M> create(List<M> models);

	@Transactional
	M update(M model);

	@Transactional
	List<M> update(List<M> models);

	@Transactional
	void delete(ID id);

	@Transactional
	void delete(List<ID> ids);

	@Transactional
	void delete(ID id, int version);

	@Transactional
	void delete(Map<ID, Integer> models);
}