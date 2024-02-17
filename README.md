# Multi-Tenancy in Spring Data JDBC

Simple Spring project illustrating how to set up a multi-tenant Spring Data JDBC project using a column discriminator approach.

This code is part of the blog post: https://www.wirequery.io/blog/spring-data-jdbc-multitenancy

Before being able to start up this project, make sure you set up a database using `docker-compose up`.

After running `docker-compose up`, execute the following SQL statements in order for this example to function properly:

```sql
CREATE USER "mt_user" WITH PASSWORD 'mt_user';

GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES ON ALL TABLES IN SCHEMA public TO "mt_user";
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO "mt_user";
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES ON TABLES TO "mt_user";
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO "mt_user";
```
