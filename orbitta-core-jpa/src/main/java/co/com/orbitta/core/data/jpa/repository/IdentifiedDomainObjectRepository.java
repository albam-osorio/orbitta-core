package co.com.orbitta.core.data.jpa.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import co.com.orbitta.core.data.jpa.domain.BaseEntity;

@NoRepositoryBean
public interface BaseEntityRepository<E extends BaseEntity<ID>, ID extends Serializable> extends JpaRepository<E, ID> {

}
