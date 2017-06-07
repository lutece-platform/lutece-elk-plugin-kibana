
--
-- Structure for table kibana_dashboard
--

DROP TABLE IF EXISTS kibana_dashboard;
CREATE TABLE kibana_dashboard (
id_dashboard int(6) NOT NULL,
idkibanadashboard varchar(255) default '' NOT NULL UNIQUE,
title long varchar NOT NULL,
PRIMARY KEY (id_dashboard)
);


DELETE FROM core_admin_right WHERE id_right = 'KIBANA_RBAC_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('KIBANA_RBAC_MANAGEMENT','kibana.adminFeature.ManageKibana.name',1,'jsp/admin/plugins/kibana/ManageDashboards.jsp','kibana.adminFeature.ManageKibana.description',0,'kibana',NULL,NULL,NULL,4);


DELETE FROM core_user_right WHERE id_right = 'KIBANA_RBAC_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('KIBANA_RBAC_MANAGEMENT',1);

-- Create role for kibana_manager
INSERT INTO core_admin_role (role_key,role_description) VALUES ('kibana_dashboards_manager','Gestion des tableaux de bords Kibana');
INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission)  SELECT MAX(rbac_id) + 1 , 'kibana_dashboards_manager', 'kibana_dashboard','*','*' FROM core_admin_role_resource;  

-- Give this role to admin
INSERT INTO core_user_role (role_key,id_user) VALUES ('kibana_dashboards_manager',1);
