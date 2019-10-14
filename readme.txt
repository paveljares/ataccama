Project contains the technologies:
- Spring (boot, MVC, bean, data)
- JPA
- Orica
- Jackson
- ehCache
- JUnit
- Mockito
- JaCoCo
- H2
- Swagger2
- Lombok

For running you need install database. It uses MariaDB (all application count with mySql format of queries). For testing purpose in folder ddl is file 01_init.sql. It create tables with mock data.

Then application requires configuration, you can it find in folder config. File database-viewer.properties please put in the same folder as jar.