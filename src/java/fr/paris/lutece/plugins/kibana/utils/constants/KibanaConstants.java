package fr.paris.lutece.plugins.kibana.utils.constants;

public class KibanaConstants {
	//URL
	public static final String KIBANA_URL_PATH = "kibana.baseUrl";
	
	// Right
	public static final String RIGHT_KIBANADASHBOARD = "KIBANA_MANAGEMENT";

	// Templates
	public static final String TEMPLATE_HOME = "/admin/plugins/kibana/kibana.html";
	public static final String TEMPLATE_ELASTICSEARH_ERROR = "/admin/plugins/kibana/elasticsearch_error.html";
	public static final String TEMPLATE_NO_DASHBOARD = "/admin/plugins/kibana/no_dashboard.html";

	// Views
	public static final String VIEW_DASHBOARD = "dashboard";

	// Properties for page titles
	public static final String PROPERTY_PAGE_TITLE_DASHBOARD = "kibana.adminFeature.KibanaDashboard.pageTitle";

	// Freemarker
	public static final String MARK_DASHBOARDS_LIST = "dashboards_list";
	public static final String MARK_CURRENT = "current";
	public static final String MARK_ERROR_MESSAGE = "error_message";
	public static final String PARAMETER_TAB = "tab";
	
}
