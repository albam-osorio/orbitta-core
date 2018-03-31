package co.com.orbitta.core.services.crud.impl;

import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;

import org.springframework.data.jpa.repository.JpaRepository;

import co.com.orbitta.core.dto.IdentifiedDomainObject;
import co.com.orbitta.core.dto.VersionableObject;
import co.com.orbitta.core.services.crud.api.CrudService;
import lombok.val;

abstract public class CrudServiceImpl<E extends IdentifiedDomainObject<ID>, M extends IdentifiedDomainObject<ID>, ID extends Serializable>
		implements CrudService<M, ID> {

	abstract protected JpaRepository<E, ID> getRepository();

	// -----------------------------------------------------------------------------------------------------------------------
	// READ
	// -----------------------------------------------------------------------------------------------------------------------
	@Override
	public M findOneById(ID id) {
		E entity = findOneEntityById(id);

		val result = asModel(entity);
		return result;
	}

	@Override
	public Optional<M> findById(ID id) {
		Optional<E> optional = findEntityById(id);

		Optional<M> result;
		if (!optional.isPresent()) {
			result = Optional.empty();
		} else {
			result = Optional.of(asModel(optional.get()));
		}
		return result;
	}

	@Override
	public List<M> findAllById(Collection<ID> ids) {
		List<E> entities;

		if (ids.isEmpty()) {
			entities = getRepository().findAll();
		} else {
			entities = getRepository().findAllById(ids);
		}

		val result = asModels(entities);
		return result;
	}

	// -----------------------------------------------------------------------------------------------------------------------
	// CREATE
	// -----------------------------------------------------------------------------------------------------------------------
	@Override
	public M create(M model) {
		E entity = newEntity();

		entity = asEntity(model, entity);
		entity = beforeCreate(entity);
		entity = saveEntity(entity);

		val result = asModel(entity);
		return result;
	}

	@Override
	public List<M> create(List<M> models) {
		val result = new ArrayList<M>();

		for (M m : models) {
			result.add(create(m));
		}
		return result;
	}

	protected E beforeCreate(E entity) {
		return entity;
	}

	// -----------------------------------------------------------------------------------------------------------------------
	// UPDATE
	// -----------------------------------------------------------------------------------------------------------------------
	@Override
	public M update(M model) {
		E entity = findOneEntityById(model.getId());

		entity = asEntity(model, entity);
		entity = beforeUpdate(entity);

		if (entity instanceof VersionableObject && model instanceof VersionableObject) {
			val e = ((VersionableObject) entity);
			val m = ((VersionableObject) model);
			e.setVersion(m.getVersion());
		}

		entity = saveEntity(entity);

		M result = asModel(entity);
		return result;
	}

	@Override
	public List<M> update(List<M> models) {
		val result = new ArrayList<M>();

		for (M m : models) {
			result.add(update(m));
		}
		return result;
	}

	protected E beforeUpdate(E entity) {
		return entity;
	}

	// -----------------------------------------------------------------------------------------------------------------------
	// DELETE
	// -----------------------------------------------------------------------------------------------------------------------
	@Override
	public void delete(ID id) {
		E entity = findOneEntityById(id);

		if (entity instanceof VersionableObject) {
			throw new UnsupportedOperationException(
					"La entidad implenta la interfaz VersionableObject y debe ser eliminada por medio del metodo delete(ID id, int version)");
		}

		entity = beforeDelete(entity);
		deleteEntity(entity);
	}

	@Override
	public void delete(List<ID> ids) {
		for (val e : ids) {
			delete(e);
		}
	}

	@Override
	public void delete(ID id, int version) {
		E entity = findOneEntityById(id);

		if (!(entity instanceof VersionableObject)) {
			throw new UnsupportedOperationException(
					"La entidad NO implenta la interfaz VersionableObject y debe ser eliminada por medio del metodo delete(ID id)");
		}

		val e = (VersionableObject) entity;
		if (e.getVersion() != version) {
			throw new OptimisticLockException();
		}

		entity = beforeDelete(entity);
		deleteEntity(entity);
	}

	@Override
	public void delete(Map<ID, Integer> models) {
		for (val e : models.entrySet()) {
			delete(e.getKey(), e.getValue());
		}
	}

	protected E beforeDelete(E entity) {
		return entity;
	}

	// -----------------------------------------------------------------------------------------------------------------------
	// Entities
	// -----------------------------------------------------------------------------------------------------------------------
	abstract protected E newEntity();

	protected E findOneEntityById(ID id) {
		val result = getRepository().findById(id);
		if (!result.isPresent()) {
			throw new EntityNotFoundException("id = " + String.valueOf(id));
		}
		return result.get();
	}

	protected Optional<E> findEntityById(ID id) {
		val result = getRepository().findById(id);
		return result;
	}

	protected E saveEntity(E entity) {
		val result = getRepository().save(entity);
		return result;
	}

	protected void deleteEntity(E entity) {
		getRepository().delete(entity);
	}

	// -----------------------------------------------------------------------------------------------------------------------
	//
	// -----------------------------------------------------------------------------------------------------------------------
	abstract protected E asEntity(M model, E entity);

	abstract protected M asModel(E entity);

	protected Optional<M> asModel(Optional<E> optional) {
		Optional<M> result;
		if (optional.isPresent()) {
			result = Optional.of(asModel(optional.get()));
		} else {
			result = Optional.empty();
		}
		return result;
	}

	protected List<M> asModels(Collection<E> entities) {
		val result = entities.stream().map(e -> asModel(e)).collect(toList());
		return result;
	}
}
