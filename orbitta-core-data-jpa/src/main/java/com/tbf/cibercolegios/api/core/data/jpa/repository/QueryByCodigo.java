package com.tbf.cibercolegios.api.core.data.jpa.repository;

import java.util.Optional;

import com.tbf.cibercolegios.api.core.domain.ObjectWithCode;

public interface QueryByCodigo<E extends ObjectWithCode<ID>, ID>{
	Optional<E> findByCodigo(String codigo);
}