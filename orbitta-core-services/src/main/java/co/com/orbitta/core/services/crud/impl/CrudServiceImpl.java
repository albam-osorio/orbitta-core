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

abstract public class CrudServiceImpl<E extends IdentifiedDomainObject<ID>, M extends IdentifiedDomainObject<ID>, I extends IdentifiedDomainObject<ID>, ID extends Serializable>
		implements CrudService<M, I, ID> {

	abstract protected JpaRepository<E, ID> getRepository();

	// -----------------------------------------------------------------------------------------------------------------------
	// READ
	// -----------------------------------------------------------------------------------------------------------------------
	@Override
	public List<I> getList() {
		val entities = getRepository().findAll();
		val result = getListItemModel(entities);
		return result;
	}

	@Override
	public List<I> getList(Collection<ID> ids) {
		val entities = getRepository().findAllById(ids);
		val result = getListItemModel(entities);
		return result;
	}

	protected List<I> getListItemModel(Collection<E> entities) {
		// @formatter:off
		val result = entities
				.stream()
				.map(e -> getItemModelFromEntity(e))
				.collect(toList());
		// @formatter:on
		return result;
	}

	@Override
	public Optional<M> getModel(ID id) {
		Optional<E> entity = getStoredEntity(id);
		if (entity.isPresent()) {
			return Optional.empty();
		}

		val result = getModelFromEntity(entity.get());
		return Optional.of(result);
	}

	// -----------------------------------------------------------------------------------------------------------------------
	// CREATE
	// -----------------------------------------------------------------------------------------------------------------------
	@Override
	public M create(M model) {
		E entity = getNewEntity();
		entity = mapModelToEntity(model, entity);

		entity = beforeCreate(entity);
		entity = saveEntity(entity);

		val result = getModelFromEntity(entity);
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
		Optional<E> optional = getStoredEntity(model.getId());
		if (!optional.isPresent()) {
			throw new EntityNotFoundException("id = " + String.valueOf(model.getId()));
		}

		E entity = mapModelToEntity(model, optional.get());
		entity = beforeUpdate(entity);
		entity = saveEntity(entity);

		M result = getModelFromEntity(entity);
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
		Optional<E> optional = getStoredEntity(id);
		if (!optional.isPresent()) {
			throw new EntityNotFoundException("id = " + String.valueOf(id));
		}

		E entity = optional.get();
		if (entity instanceof VersionableObject) {
			throw new UnsupportedOperationException(
					"La entidad implenta la interfaz VersionableObject y debe ser eliminada por medio del metodo delete(ID id, int version)");
		}

		entity = beforeDelete(entity);
		deleteEntity(entity);
	}

	@Override
	public void delete(ID id, int version) {
		Optional<E> optional = getStoredEntity(id);
		if (!optional.isPresent()) {
			throw new EntityNotFoundException("id = " + String.valueOf(id));
		}

		E entity = optional.get();
		if (!(entity instanceof VersionableObject)) {
			throw new UnsupportedOperationException(
					"La entidad NO implenta la interfaz VersionableObject y debe ser eliminada por medio del metodo delete(ID id)");
		}

		val versionable = (VersionableObject) entity;
		if (versionable.getVersion() != version) {
			throw new OptimisticLockException();
		}

		entity = beforeDelete(entity);
		deleteEntity(entity);
	}

	protected E beforeDelete(E entity) {
		return entity;
	}

	// -----------------------------------------------------------------------------------------------------------------------
	//
	// -----------------------------------------------------------------------------------------------------------------------
	@Override
	public List<M> create(List<M> models) {
		val result = new ArrayList<M>();
		for (M m : models) {
			result.add(create(m));
		}
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

	@Override
	public void delete(List<ID> ids) {
		for (val e : ids) {
			delete(e);
		}
	}

	@Override
	public void delete(Map<ID, Integer> models) {
		for (val e : models.entrySet()) {
			delete(e.getKey(), e.getValue());
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------
	//
	// -----------------------------------------------------------------------------------------------------------------------
	abstract protected M getModelFromEntity(E entity);

	abstract protected I getItemModelFromEntity(E entity);

	abstract protected E mapModelToEntity(M model, E entity);

	// -----------------------------------------------------------------------------------------------------------------------
	//
	// -----------------------------------------------------------------------------------------------------------------------
	abstract protected E getNewEntity();

	protected Optional<E> getStoredEntity(ID id) {
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
}
