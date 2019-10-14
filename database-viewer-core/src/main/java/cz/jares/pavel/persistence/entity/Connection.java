package cz.jares.pavel.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 *  Entity to store parameters about connection into database.
 * 
 * @author jaresp
 *
 */
@Entity
@Table(name="sys_connection")
@Data
@NoArgsConstructor
public class Connection {
		
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator=PersistenceConstants.HIBERNATE_SEQUENCE)
	@SequenceGenerator(name=PersistenceConstants.HIBERNATE_SEQUENCE, sequenceName=PersistenceConstants.HIBERNATE_SEQUENCE, allocationSize=1, initialValue=1)
	private Long id;
	
	@Version
	@Column(name="version")
	private Long version;
	
	@Column(name="name", length=50)
	private String name;
	
	@Column(name="hostname", length=50)
	private String hostname;
	
	@Column(name="port")
	private Integer port;
		
	@Column(name="database_name", length=50)
	private String databaseName;
	
	@Column(name="username", length=50)
	private String username;
	
	@Column(name="password", length=50)
	private String password;
		
}
