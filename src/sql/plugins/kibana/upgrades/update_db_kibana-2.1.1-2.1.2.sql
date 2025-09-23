-- liquibase formatted sql
-- changeset kibana:update_db_kibana-2.1.1-2.1.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE kibana_dashboard modify COLUMN id_dashboard int AUTO_INCREMENT;