package co.com.orbitta.core.dto;

import java.time.LocalDateTime;

public interface ObjectAuditableByTime {
	
	LocalDateTime getFechaCreacion();

	void setFechaCreacion(LocalDateTime fechaCreacion);

	LocalDateTime getFechaModificacion();

	void setFechaModificacion(LocalDateTime fechaModificacion);
}
