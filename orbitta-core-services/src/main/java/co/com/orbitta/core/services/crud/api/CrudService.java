package co.com.orbitta.core.services.crud.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import co.com.orbitta.core.dto.IdentifiedDomainObject;

@Transactional(readOnly = true)
public interface CrudService<M extends IdentifiedDomainObject<ID>, I extends IdentifiedDomainObject<ID>, ID extends Serializable> {

	List<I> getList();

	List<I> getList(Collection<ID> ids);

	Optional<M> getModel(ID id);

	@Transactional
	M create(M model);

	@Transactional
	M update(M model);

	@Transactional
	void delete(ID id);

	@Transactional
	void delete(ID id, int version);

	@Transactional
	List<M> create(List<M> models);

	@Transactional
	List<M> update(List<M> models);

	void delete(List<ID> ids);

	@Transactional
	void delete(Map<ID, Integer> models);
}