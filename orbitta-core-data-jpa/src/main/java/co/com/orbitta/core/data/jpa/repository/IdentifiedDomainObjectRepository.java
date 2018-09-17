package co.com.orbitta.core.data.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import co.com.orbitta.core.domain.IdentifiedDomainObject;

@NoRepositoryBean
public interface IdentifiedDomainObjectRepository<E extends IdentifiedDomainObject<ID>, ID>
		extends JpaRepository<E, ID> {
}