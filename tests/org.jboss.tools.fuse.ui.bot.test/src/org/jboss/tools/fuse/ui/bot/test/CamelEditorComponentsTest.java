package org.jboss.tools.fuse.ui.bot.test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IViewReference;
import org.jboss.reddeer.common.logging.Logger;

import org.jboss.reddeer.core.lookup.WorkbenchPartLookup;
import org.jboss.reddeer.core.util.Display;
import org.jboss.reddeer.eclipse.jdt.ui.ProjectExplorer;
import org.jboss.reddeer.eclipse.ui.perspectives.JavaEEPerspective;
import org.jboss.reddeer.junit.internal.runner.ParameterizedRequirementsRunnerFactory;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.cleanworkspace.CleanWorkspaceRequirement.CleanWorkspace;
import org.jboss.reddeer.requirements.openperspective.OpenPerspectiveRequirement.OpenPerspective;
import org.jboss.reddeer.swt.impl.styledtext.DefaultStyledText;
import org.jboss.reddeer.workbench.handler.EditorHandler;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.common.reddeer.XPathEvaluator;
import org.jboss.tools.common.reddeer.preference.ConsolePreferenceUtil;
import org.jboss.tools.common.reddeer.view.ErrorLogView;
import org.jboss.tools.fuse.reddeer.ProjectTemplate;
import org.jboss.tools.fuse.reddeer.ProjectType;
import org.jboss.tools.fuse.reddeer.component.CamelComponent;
import org.jboss.tools.fuse.reddeer.component.CamelComponents;
import org.jboss.tools.fuse.reddeer.editor.CamelEditor;
import org.jboss.tools.fuse.reddeer.projectexplorer.CamelProject;
import org.jboss.tools.fuse.reddeer.view.FusePropertiesView;
import org.jboss.tools.fuse.reddeer.view.FusePropertiesView.DetailsProperty;
import org.jboss.tools.fuse.ui.bot.test.utils.DefaultCamelCatalog;
import org.jboss.tools.fuse.ui.bot.test.utils.EditorManipulator;
import org.jboss.tools.fuse.ui.bot.test.utils.ProjectFactory;
import org.jboss.tools.runtime.reddeer.utils.FuseServerManipulator;
import org.json.JSONException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.UseParametersRunnerFactory;

/**
 * Tests:
 * <ul>
 * <li>testExistInCamelCatalog</li>
 * <li>testDetailsTabAvailability</li>
 * <li>testAdvancedTabAvailability</li>
 * </ul>
 * 
 * @author djelinek
 */
@CleanWorkspace
@OpenPerspective(JavaEEPerspective.class)
@RunWith(RedDeerSuite.class)
@UseParametersRunnerFactory(ParameterizedRequirementsRunnerFactory.class)
public class CamelEditorComponentsTest { 

	private static Logger log = Logger.getLogger(CamelEditorComponentsTest.class);

	private static final String PROJECT_NAME = "cbr";

	private static final String CONTEXT = "camel-context.xml";

	private static IPath path;

	private static String camelVersion;

	private CamelComponent component;

	/**
	 * Sets parameters for parameterized test
	 * 
	 * @return List of all available Global elements
	 */
	@Parameters
	public static Collection<CamelComponent> setupData() {
		path = new Path(System.getProperty("camel.catalog"));
		camelVersion = path.lastSegment().substring(14);
		new WorkbenchShell();
		ProjectFactory.newProject(PROJECT_NAME).version(camelVersion).template(ProjectTemplate.CBR)
				.type(ProjectType.SPRING).create();
		for (IViewReference viewReference : WorkbenchPartLookup.getInstance().findAllViewReferences()) {
			if (viewReference.getPartName().equals("Welcome")) {
				final IViewReference iViewReference = viewReference;
				Display.syncExec(new Runnable() {
					@Override
					public void run() {
						iViewReference.getPage().hideView(iViewReference);
					}
				});
				break;
			}
		}
		new ProjectExplorer().open();
		CamelEditor editor = new CamelEditor(CONTEXT);
		editor.activate();
		List<CamelComponent> com = CamelComponents.getEndpoints();
		return com;
	}

	/**
	 * Utilizes passing parameters using the constructor
	 * 
	 * @param template
	 *            a Global element
	 */
	public CamelEditorComponentsTest(CamelComponent component) {
		this.component = component;
	}

	/**
	 * Prepares test environment - creates empty fuse integration project
	 */
	@BeforeClass
	public static void setupResetCamelContext() {
		
		log.info("Maximizing workbench shell.");
		new WorkbenchShell().maximize();

		log.info("Disable showing Console view after standard output changes");
		ConsolePreferenceUtil.setConsoleOpenOnError(false);
		ConsolePreferenceUtil.setConsoleOpenOnOutput(false);

		log.info("Disable showing Error Log view after changes");
		new ErrorLogView().selectActivateOnNewEvents(false);
		
		log.info("Create new Fuse project (tamplate: CBR, type: Spring)");
		ProjectFactory.newProject(PROJECT_NAME).version(camelVersion).template(ProjectTemplate.CBR)
				.type(ProjectType.SPRING).create();
	}

	/**
	 * Cleans up test environment
	 */
	@AfterClass
	public static void setupDeleteProjects() {
		new WorkbenchShell();
		EditorHandler.getInstance().closeAll(true);

		log.info("Deleting all projects");
		ProjectFactory.deleteAllProjects();

		log.info("Stopping and deleting configured servers");
		FuseServerManipulator.deleteAllServers();
		FuseServerManipulator.deleteAllServerRuntimes();
	}

	/**
	 * Cleans up test environment and switch to required tab in camel editor
	 */
	@After
	public void clearSetup() {
		new CamelProject(PROJECT_NAME).update(); // ????
		new CamelProject(PROJECT_NAME).openCamelContext(CONTEXT);
		CamelEditor.switchTab("Source");
		EditorManipulator.copyFileContentToCamelXMLEditor("resources/camel-context-cbr.xml");
		CamelEditor.switchTab("Design");
	}

	/**
	 * <p>
	 * Test verifies that all <i>Endpoints</i> are available inside Camel Catalog
	 * </p>
	 * <ol>
	 * <li>create a new project with Spring-DSL project type and Content Based Router template</li>
	 * <li>open Project Explorer view</li>
	 * <li>open camel-context.xml file</li>
	 * <li>checks that <i>Endpoint</i> component exists in Camel catalog</li>
	 * </ol>
	 */
	@Test
	public void testExistInCamelCatalog() {
		isExist();
	}
	
	/**
	 * <p>
	 * Test verifies <i>Endpoint</i> properties view, specially <i>Details</i> tab.
	 * </p>
	 * <ol>
	 * <li>creates a new project with Spring-DSL project type and Content Based Router template</li>
	 * <li>opens Project Explorer view</li>
	 * <li>opens camel-context.xml file</li>
	 * <li>checks that <i>Endpoint</i> component exists in Camel catalog</li>
	 * <li>adds camel component into Camel editor route</li>
	 * <li>activates property view and switches to <i>Details</i> tab</li>
	 * <li>tries set all available properties to test values</li>
	 * <li>saves and switches to Camel editor <i>Source</i> tab</li>
	 * <li>verifies that all changed values are available at source camel context XML file</li>
	 * </ol>
	 */
	@Test
	public void testDetailsTabAvailability() {
		isExist(); // if component does not exist, test will fail
		CamelEditor editor = new CamelEditor(CONTEXT);
		editor.activate();
		editor.addCamelComponent(component, 150, 150);

		FusePropertiesView view = new FusePropertiesView();
		view.activate();
		if (view.getTabs().contains("Advanced")) {
			view.selectTab("Details");
		} else {
			fail("Component '" + component.getPaletteEntry()
					+ "' properties view has not 'Advanced' tab, so all properties are on 'Details' tab");
		}
		view.setDetailsProperty(DetailsProperty.URI, "testUri");
		view.setDetailsProperty(DetailsProperty.DESC, "testDescription");
		view.setDetailsProperty(DetailsProperty.ID, "testId");
		view.setDetailsProperty(DetailsProperty.PATTERN, "testPattern");
		view.setDetailsProperty(DetailsProperty.REF, "");
		editor.save();

		CamelEditor.switchTab("Source");
		XPathEvaluator eval = new XPathEvaluator(
				new ByteArrayInputStream(new DefaultStyledText().getText().getBytes()));
		assertTrue("Property 'ID' is not correct.",
				eval.evaluateBoolean("/beans/camelContext/route/to[@id='" + "testId" + "']"));
		assertTrue("Property 'Pattern' is not correct.",
				eval.evaluateBoolean("/beans/camelContext/route/to[@pattern='" + "testPattern" + "']"));
		assertTrue("Property 'URI' is not correct.",
				eval.evaluateBoolean("/beans/camelContext/route/to[@uri='" + "testUri" + "']"));
		assertTrue("Property 'Description' is not correct.",
				eval.evaluateString("/beans/camelContext/route/to/description").equals("testDescription"));
	}

	/**
	 * <p>
	 * Test verifies that all <i>Endpoints</i> properties mentioned in Camel catalog are available at component property
	 * view <i>Advanced</i> tab.
	 * </p>
	 * <ol>
	 * <li>creates a new project with Spring-DSL project type and Content Based Router template</li>
	 * <li>opens Project Explorer view</li>
	 * <li>opens camel-context.xml file</li>
	 * <li>checks that <i>Endpoint</i> component exists in Camel catalog</li>
	 * <li>adds camel component into Camel editor route</li>
	 * <li>activates property view and switches to <i>Advanced</i> tab</li>
	 * <li>gets all available labels from property view</li>
	 * <li>gets all component properties from Camel catalog</li>
	 * <li>checks if all Camel catalog component properties are available at properties view <i>Advanced</i> tab</li>
	 * </ol>
	 */
	@Test
	public void testAdvancedTabAvailability() {
		isExist(); // if component does not exist, test will fail
		DefaultCamelCatalog catalog = new DefaultCamelCatalog(path);
		CamelEditor editor = new CamelEditor(CONTEXT);
		editor.activate();
		editor.addCamelComponent(component, 150, 150);

		FusePropertiesView view = new FusePropertiesView();
		view.activate();
		if (view.getTabs().contains("Advanced")) {
			view.selectTab("Advanced");
		} else {
			fail("Component '" + component.getPaletteEntry() + "' properties view has not 'Advanced' tab.");
		}
		List<String> tabs = view.getPropertiesTabsTitles();
		List<String> viewProperties = new ArrayList<>();
		for (String tab : tabs) {
			viewProperties.addAll(view.getAdvancedPropertiesText(tab));
		}

		List<String> err = new ArrayList<>();
		List<String> catalogProps;
		try {
			catalogProps = catalog.getAdvancedPropertiesNames(component);
			for (String props : viewProperties) {
				props = new StringBuffer(props).replace(0, 1, String.valueOf(Character.toLowerCase(props.charAt(0))))
						.toString().replaceAll(" ", "");
				props = props.replace("(deprecated)", "");
				props = props.replace("*", "");
				if (!catalogProps.contains(props))
					err.add(props);
			}
		} catch (JSONException exc) {
			fail("'" + component.getPaletteEntry() + "', error message: " + exc.getMessage());
		}

		StringBuffer buffer = new StringBuffer("Missed catalog properties ('" + component.getPaletteEntry() + "'):");
		if (!err.isEmpty()) {
			for (String e : err) {
				buffer.append("\n" + e);
			}
			fail(buffer.toString());
		}
	}

	/**
	 * This method checks, if camel editor component is available in camel catalog
	 */
	private void isExist() {
		DefaultCamelCatalog catalog = new DefaultCamelCatalog(path);
		new CamelEditor(CONTEXT).activate();
		assertTrue("\"" + component.getPaletteEntry() + "\" is not in Camel Catalog",
				catalog.isExistComponent(component.getPaletteEntry()));
	}

}
