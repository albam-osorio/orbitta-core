package co.com.orbitta.core.web.api.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import co.com.orbitta.core.dto.IdentifiedDomainObject;
import co.com.orbitta.core.services.crud.api.CrudService;
import lombok.val;

abstract public class RestCrudController<M extends IdentifiedDomainObject<ID>, ID extends Serializable> {

	// -----------------------------------------------'-------------------------------------------------------------------------------------
	// -- PATHS
	// ------------------------------------------------------------------------------------------------------------------------------------
	protected static final String PATH_LIST = "";

	protected static final String PATH_LIST_SEARCH_IN = "/[{ids}]";

	protected static final String PATH_ENTITY = "/{id}";

	protected static final String PATH_DELETE = "/{id}/{version}";

	// -----------------------------------------------'-------------------------------------------------------------------------------------
	// -- FORMAT
	// ------------------------------------------------------------------------------------------------------------------------------------
	protected static final String FORMATO_DATE = "yyyy-MM-dd";

	protected static final String FORMATO_TIME = "HH:mm";

	public RestCrudController() {
		super();
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// -- Servicio
	// ------------------------------------------------------------------------------------------------------------------------------------
	abstract protected CrudService<M, ID> getService();

	// -----------------------------------------------'-------------------------------------------------------------------------------------
	// -- HTTP GET METHODS
	// ------------------------------------------------------------------------------------------------------------------------------------
	@GetMapping(PATH_LIST)
	public ResponseEntity<List<M>> list() {
		val ids = new ArrayList<ID>();
		val result = getService().findAllById(ids);
		return ResponseEntity.ok(result);
	}

	@GetMapping(PATH_LIST_SEARCH_IN)
	public ResponseEntity<List<M>> list(@PathVariable List<ID> ids) {
		val result = getService().findAllById(ids);
		return ResponseEntity.ok(result);
	}

	@GetMapping(path = PATH_ENTITY)
	public ResponseEntity<M> get(@PathVariable ID id) {
		Optional<M> result = getService().findById(id);

		if (!result.isPresent()) {
			throw new NotFoundException();
		}

		return ResponseEntity.ok(result.get());
	}

	// -----------------------------------------------'-------------------------------------------------------------------------------------
	// -- HTTP POST, PUT, DELETE METHODS
	// ------------------------------------------------------------------------------------------------------------------------------------
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody M model, BindingResult bindingResult) {
		if (model.getId() != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}

		if (bindingResult.hasErrors()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(bindingResult);
		}

		M result = getService().create(model);

		return ResponseEntity.created(showURI(result).toUri()).body(result);
	}

	@PutMapping
	public ResponseEntity<?> update(@Valid @RequestBody M model, BindingResult bindingResult) {
		if (model.getId() == null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}

		if (bindingResult.hasErrors()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(bindingResult);
		}

		M result = getService().update(model);

		return ResponseEntity.created(showURI(result).toUri()).body(result);
	}

	@DeleteMapping(PATH_ENTITY)
	public ResponseEntity<?> delete(@PathVariable ID id) {

		getService().delete(id);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping(PATH_DELETE)
	public ResponseEntity<?> delete(@PathVariable ID id, @PathVariable Integer version) {

		getService().delete(id, version);

		return ResponseEntity.ok().build();
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// -- URIs
	// ------------------------------------------------------------------------------------------------------------------------------------
	protected UriComponents listURI() {
		// @formatter:off
		val result = MvcUriComponentsBuilder
				.fromMethodCall(MvcUriComponentsBuilder.on(RestCrudController.class).list())
				.build()
				.encode();
		// @formatter:on

		return result;
	}

	@SuppressWarnings("unchecked")
	protected UriComponents showURI(IdentifiedDomainObject<ID> model) {
		// @formatter:off
		val result = MvcUriComponentsBuilder
				.fromMethodCall(MvcUriComponentsBuilder.on(RestCrudController.class).get(null))
				.buildAndExpand(model.getId())
				.encode();
		// @formatter:on

		return result;
	}
}