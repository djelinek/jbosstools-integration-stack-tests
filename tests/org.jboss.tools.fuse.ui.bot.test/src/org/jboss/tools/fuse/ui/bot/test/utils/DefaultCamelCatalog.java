package org.jboss.tools.fuse.ui.bot.test.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.jboss.reddeer.common.logging.Logger;
import org.jboss.tools.common.reddeer.FileUtils;
import org.jboss.tools.fuse.reddeer.component.CamelComponent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Methods for using CamelCatalog in tests, catalog have to be placed in test resources
 * 
 * @author djelinek
 */
public class DefaultCamelCatalog {

	private static String COMPONENTS;

	private static String COMPONENTS_LIST;

	private static String DATAFORMATS;

	private static String DATAFORMATS_LIST;

	private static Logger log = Logger.getLogger(DefaultCamelCatalog.class);

	private static final String JSON_SUFFIX = ".json";

	/**
	 * Creates the CamelCatalog.
	 */
	public DefaultCamelCatalog(String path) {
		path += "org/apache/camel/catalog/";
		COMPONENTS = path + "components";
		COMPONENTS_LIST = path + "components.properties";
		DATAFORMATS = path + "dataformats";
		DATAFORMATS_LIST = path + "dataformats.properties";
	}

	/**
	 * Creates the CamelCatalog.
	 */
	public DefaultCamelCatalog(IPath path) {
		path = path.append("org/apache/camel/catalog/");
		COMPONENTS = path.append("components").toString();
		COMPONENTS_LIST = path.append("components.properties").toString();
		DATAFORMATS = path.append("dataformats").toString();
		DATAFORMATS_LIST = path.append("dataformats.properties").toString();
	}

	/**
	 * Returns all the components
	 * 
	 * @return String
	 */
	public String Components() {
		try {
			return FileUtils.getFileContent(COMPONENTS_LIST);
		} catch (IOException e) {
			log.error("Resource missing: can't find a failing test case to copy (" + COMPONENTS_LIST + ")!");
		}
		log.info("Text in active text editor was replaced with content of the file: " + COMPONENTS_LIST + ".");
		return "";
	}

	/**
	 * Returns all the data formats
	 * 
	 * @return String
	 */
	public String DataFormats() {
		try {
			return FileUtils.getFileContent(DATAFORMATS_LIST);
		} catch (IOException e) {
			log.error("Resource missing: can't find a failing test case to copy (" + COMPONENTS_LIST + ")!");
		}
		log.info("Text in active text editor was replaced with content of the file: " + COMPONENTS_LIST + ".");
		return "";
	}

	/**
	 * Checks if component is in CamelCatalog
	 * 
	 * @param name
	 *            (name of Component)
	 * @return true/false
	 */
	public boolean isExistComponent(String name) {
		return isExist(name, Components());
	}

	/**
	 * Checks if DataFormat is in CamelCatalog
	 * 
	 * @param name
	 *            (name of DataFormat)
	 * @return true/false
	 */
	public boolean isExistDataFormat(String name) {
		return isExist(name, DataFormats());
	}

	/**
	 * Returns the component information as JSon format.
	 *
	 * @param name
	 *            (the component name)
	 * @return component details in JSon
	 */
	public String componentJSonSchema(String name) {
		try {
			return FileUtils.getFileContent(getJSONFile(name, COMPONENTS + "/"));
		} catch (IOException e) {
			log.error("Resource missing: can't find a failing test case to copy (" + name + ")!");
		}
		log.info("Text in active text editor was replaced with content of the file: " + name + ".");
		return "";
	}

	/**
	 * Returns the data format information as JSon format.
	 *
	 * @param name
	 *            (the data format name)
	 * @return data format details in JSon
	 */
	public String dataFormatJSonSchema(String name) {
		try {
			return FileUtils.getFileContent(getJSONFile(name, DATAFORMATS + "/"));
		} catch (IOException e) {
			log.error("Resource missing: can't find a failing test case to copy (" + name + ")!");
		}
		log.info("Text in active text editor was replaced with content of the file: " + name + ".");
		return "";
	}

	/**
	 * Returns the component name from the given endpoint uri
	 *
	 * @param uri
	 *            (the endpoint uri)
	 * @return the component name (which scheme), or <tt>null</tt> if not possible to determine
	 */
	public String endpointComponentName(String uri) {
		if (uri != null) {
			int idx = uri.indexOf(":");
			if (idx > 0) {
				return uri.substring(0, idx);
			}
		}
		return "";
	}

	/**
	 * Map keys - name of property Map values - data type of property
	 * 
	 * @return map<String, String> of available advanced properties from camel catalog
	 * @throws JSONException
	 */
	public HashMap<String, String> getAdvancedProperties(CamelComponent component) throws JSONException {

		JSONObject json = null, obj = null;
		JSONArray arr = null;

		try {
			json = new JSONObject(componentJSonSchema(endpointComponentName(component.getLabel())));
		} catch (Exception e) {
			json = new JSONObject(componentJSonSchema(component.getPaletteEntry().toLowerCase()));
		}

		obj = json.getJSONObject("properties");
		arr = obj.names();

		HashMap<String, String> properties = new HashMap<>();
		for (int i = 0; i < arr.length(); i++) {
			try {
				if (!String.valueOf(obj.getJSONObject(arr.get(i).toString()).get("enum").toString()).isEmpty())
					properties.put(arr.getString(i), "enum");
			} catch (Exception e) {
				properties.put(arr.getString(i), obj.getJSONObject(arr.get(i).toString()).get("type").toString());
			}
		}
		return properties;
	}

	/**
	 * Return list of Advanced properties names
	 * 
	 * @param CamelComponent
	 * @return List<String>
	 * @throws JSONException
	 */
	public List<String> getAdvancedPropertiesNames(CamelComponent component) throws JSONException {

		JSONObject json = null, obj = null;
		JSONArray arr = null;

		try {
			json = new JSONObject(componentJSonSchema(endpointComponentName(component.getLabel())));
		} catch (Exception e) {
			json = new JSONObject(componentJSonSchema(component.getPaletteEntry().toLowerCase()));
		}

		obj = json.getJSONObject("properties");
		arr = obj.names();

		List<String> properties = new ArrayList<String>();
		for (int i = 0; i < arr.length(); i++) {
			properties.add(arr.get(i).toString());
		}

		obj = json.getJSONObject("componentProperties");
		arr = obj.names();

		if (arr != null) {
			for (int i = 0; i < arr.length(); i++) {
				properties.add(arr.get(i).toString());
			}
		}

		return properties;
	}

	public List<String> getAdvancedPropertiesLabels(CamelComponent component) throws JSONException {

		List<String> labels = new ArrayList<>();
		StringBuffer buff = new StringBuffer();
		for (String name : getAdvancedPropertiesNames(component)) {
			String[] s = name.split("(?=[A-Z])");
			for (int i = 0; i < s.length; i++) {
				buff.append(s[i].trim());
				/*
				 * if (s[i].length() == 1) { try { if (s[i + 1].length() == 1) continue; } catch
				 * (ArrayIndexOutOfBoundsException e) { } }
				 */
				if (i < s.length - 1 && s[i].length() > 1)
					buff.append(" ");
			}
			buff.replace(0, 1, String.valueOf(Character.toUpperCase(buff.charAt(0))));
			labels.add(buff.toString());
			buff.delete(0, buff.length());
		}
		return labels;
	}

	/**
	 * Return type of Advanced property - enum -> Combo box - string, integer, object -> Text field - boolean -> Button
	 * (Checkbox)
	 * 
	 * @param component
	 *            CamelComponent
	 * @param propertyName
	 *            String
	 * 
	 * @return String (enum, string, integer, object, boolean)
	 * @throws JSONException
	 */
	public String getAdvancedPropertyType(CamelComponent component, String propertyName) throws JSONException {

		JSONObject json = null, obj = null;
		JSONArray arr = null;

		try {
			json = new JSONObject(componentJSonSchema(endpointComponentName(component.getLabel())));
		} catch (Exception e) {
			json = new JSONObject(componentJSonSchema(component.getPaletteEntry().toLowerCase()));
		}

		obj = json.getJSONObject("properties");
		arr = obj.names();

		String object = null;
		for (int i = 0; i < arr.length(); i++) {
			if (arr.get(i).equals(propertyName))
				try {
					object = obj.getJSONObject(arr.get(i).toString()).get("enum").toString();
					return "enum";
				} catch (Exception e) {
					return obj.getJSONObject(arr.get(i).toString()).get("type").toString();
				}
		}

		return object;
	}

	public boolean isDefaultValue(CamelComponent component, String property, String value) throws JSONException {

		JSONObject json = null, obj = null;
		JSONArray arr = null;

		try {
			json = new JSONObject(componentJSonSchema(endpointComponentName(component.getLabel())));
		} catch (Exception e) {
			json = new JSONObject(componentJSonSchema(component.getPaletteEntry().toLowerCase()));
		}

		obj = json.getJSONObject("properties");
		arr = obj.names();

		for (int i = 0; i < arr.length(); i++) {
			if (arr.get(i).equals(property))
				if (obj.getJSONObject(arr.get(i).toString()).get("defaultValue").toString() == value)
					return true;
		}
		return false;
	}

	public boolean isDefaultValue(CamelComponent component, String label) throws JSONException {

		JSONObject json = null, obj = null;
		try {
			json = new JSONObject(componentJSonSchema(endpointComponentName(component.getLabel())));
		} catch (Exception e) {
			json = new JSONObject(componentJSonSchema(component.getPaletteEntry().toLowerCase()));
		}

		obj = json.getJSONObject("properties");
		label = new StringBuffer(label).replace(0, 1, String.valueOf(Character.toLowerCase(label.charAt(0)))).toString()
				.replaceAll(" ", "");

		JSONObject row = obj.getJSONObject(label);

		try {
			return row.getBoolean("defaultValue");
		} catch (JSONException e) {
			return false;
		}
	}

	public boolean isRequiredValue(CamelComponent component, String label) throws JSONException {

		JSONObject json = null;
		try {
			json = new JSONObject(componentJSonSchema(endpointComponentName(component.getLabel())));
		} catch (Exception e) {
			json = new JSONObject(componentJSonSchema(component.getPaletteEntry().toLowerCase()));
		}

		label = new StringBuffer(label).replace(0, 1, String.valueOf(Character.toLowerCase(label.charAt(0)))).toString()
				.replaceAll(" ", "");

		JSONObject row;
		try {
			row = json.getJSONObject("properties").getJSONObject(label);
		} catch (JSONException e) {
			row = json.getJSONObject("componentProperties").getJSONObject(label);
		}

		try {
			return row.getBoolean("required");
		} catch (JSONException e) {
			return false;
		}
	}

	public boolean isDeprecatedValue(CamelComponent component, String label) throws JSONException {

		JSONObject json = null;
		try {
			json = new JSONObject(componentJSonSchema(endpointComponentName(component.getLabel())));
		} catch (Exception e) {
			json = new JSONObject(componentJSonSchema(component.getPaletteEntry().toLowerCase()));
		}

		label = new StringBuffer(label).replace(0, 1, String.valueOf(Character.toLowerCase(label.charAt(0)))).toString()
				.replaceAll(" ", "");

		JSONObject row;
		try {
			row = json.getJSONObject("properties").getJSONObject(label);
		} catch (JSONException e) {
			row = json.getJSONObject("componentProperties").getJSONObject(label);
		}
		
		try {
			return row.getBoolean("deprecated");
		} catch (JSONException e) {
			return false;
		}
	}

	public List<String> getSyntaxList(CamelComponent component) throws JSONException {
		String json = getSyntax(component);
		List<String> syntax = new ArrayList<>();
		String[] parts = json.split(":");
		for (int i = 1; i < parts.length; i++) {
			syntax.add(parts[i]);
		}
		return syntax;
	}

	public String getComponentValue(CamelComponent component, String field) throws JSONException {
		JSONObject json = null, obj = null;
		try {
			json = new JSONObject(componentJSonSchema(endpointComponentName(component.getLabel())));
		} catch (Exception e) {
			json = new JSONObject(componentJSonSchema(component.getPaletteEntry().toLowerCase()));
		}
		obj = json.getJSONObject("component");
		return obj.getString(field);
	}

	public String getPropertiesValue(CamelComponent component, String field) throws JSONException {
		JSONObject json = null, obj = null;
		try {
			json = new JSONObject(componentJSonSchema(endpointComponentName(component.getLabel())));
		} catch (Exception e) {
			json = new JSONObject(componentJSonSchema(component.getPaletteEntry().toLowerCase()));
		}
		obj = json.getJSONObject("properties");
		return obj.getString(field);
	}

	public String getComponentPropertiesValue(CamelComponent component, String field) throws JSONException {
		JSONObject json = null, obj = null;
		try {
			json = new JSONObject(componentJSonSchema(endpointComponentName(component.getLabel())));
		} catch (Exception e) {
			json = new JSONObject(componentJSonSchema(component.getPaletteEntry().toLowerCase()));
		}
		obj = json.getJSONObject("componentProperties");
		return obj.getString(field);
	}

	public String getKind(CamelComponent component) throws JSONException {
		return getComponentValue(component, "kind");
	}

	public String getSyntax(CamelComponent component) throws JSONException {
		return getComponentValue(component, "syntax");
	}

	public String getTitle(CamelComponent component) throws JSONException {
		return getComponentValue(component, "title");
	}

	public String getLabel(CamelComponent component) throws JSONException {
		return getComponentValue(component, "label");
	}

	public String getDescription(CamelComponent component) throws JSONException {
		return getComponentValue(component, "description");
	}

	public String getVersion(CamelComponent component) throws JSONException {
		return getComponentValue(component, "version");
	}

	private boolean isExist(String name, String com) {
		boolean exist = false;
		if (com.contains(name.toLowerCase().replaceAll("\\s+", "")))
			exist = true;
		else if (com.contains(name.toLowerCase().replaceAll("-", "")))
			exist = true;
		else if (com.contains(name.toLowerCase().replaceAll("\\s+", "-")))
			exist = true;
		return exist;
	}

	private String getJSONFile(String name, String file) {
		if (new File(file + name.replaceAll("\\s+", "") + JSON_SUFFIX).exists())
			file += name.replaceAll("\\s+", "") + JSON_SUFFIX;
		else if (new File(file + name.replaceAll("-", "") + JSON_SUFFIX).exists())
			file += name.replaceAll("-", "") + JSON_SUFFIX;
		else
			file += name.replaceAll("\\s+", "-") + JSON_SUFFIX;
		return file;
	}

}