GRANT ALL PRIVILEGES ON DATABASE orcid to orcid;

CREATE DATABASE statistics;
CREATE USER statistics WITH PASSWORD 'statistics';
GRANT ALL PRIVILEGES ON DATABASE statistics to statistics;

CREATE USER orcidro WITH PASSWORD 'orcidro';
GRANT CONNECT ON DATABASE orcid to orcidro;
GRANT SELECT ON ALL TABLES IN SCHEMA public to orcidro;

CREATE DATABASE features;
GRANT ALL PRIVILEGES ON DATABASE features to orcid;

CREATE DATABASE message_listener;
GRANT ALL PRIVILEGES ON DATABASE message_listener to orcid;
CREATE USER dw_user WITH PASSWORD 'dw_user';

