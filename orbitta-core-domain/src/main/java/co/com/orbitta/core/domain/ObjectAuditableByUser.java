package co.com.orbitta.core.domain;

public interface ObjectAuditableByUser {
	
	String getCreadoPor();

	void setCreadoPor(String creadorPor);

	String getModificadoPor();

	void setModificadoPor(String modificadoPor);
}
