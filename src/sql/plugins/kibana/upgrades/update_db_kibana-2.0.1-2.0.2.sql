
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
