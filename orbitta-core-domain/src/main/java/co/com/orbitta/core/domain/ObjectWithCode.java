package co.com.orbitta.core.domain;

public interface ObjectWithCode<ID> extends IdentifiedDomainObject<ID> {

	String getCodigo();

	void setCodigo(String codigo);
}
