package cz.jares.pavel.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cz.jares.pavel.persistence.entity.Connection;

/**
 * 
 * @author jaresp
 *
 */
@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long>, JpaSpecificationExecutor<Connection> {

}
