-- liquibase formatted sql
-- changeset kibana:create_db_kibana.sql
-- preconditions onFail:MARK_RAN onError:WARN

--
-- Structure for table kibana_dashboard
--

DROP TABLE IF EXISTS kibana_dashboard;
CREATE TABLE kibana_dashboard (
id_dashboard int(6) NOT NULL,
idkibanadashboard varchar(255) default '' NOT NULL UNIQUE,
title long varchar NOT NULL,
dataSourceName long varchar,
PRIMARY KEY (id_dashboard)
);
