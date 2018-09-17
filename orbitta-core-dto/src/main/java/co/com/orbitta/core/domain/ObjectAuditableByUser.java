package co.com.orbitta.core.dto;

public interface ObjectAuditableByUser {
	
	String getCreatedBy();

	void setCreatedBy(String createdBy);

	String getModifiedBy();

	void setModifiedBy(String modifiedBy);
}
