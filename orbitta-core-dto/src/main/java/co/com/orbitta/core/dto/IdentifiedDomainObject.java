package co.com.orbitta.core.dto;

import java.io.Serializable;

public interface IdentifiedDomainObject<ID extends Serializable> extends Serializable {

	ID getId();
}
