package co.com.orbitta.core.domain;

public interface ObjectAuditableByUser {
	
	String getCreadoPor();

	void setCreadoPor(String creadoPor);

	String getModificadoPor();

	void setModificadoPor(String modificadoPor);
}
