package org.jboss.tools.bpmn2.reddeer.editor.jbpm.gateways;

import org.jboss.tools.bpmn2.reddeer.editor.ElementType;
import org.jboss.tools.bpmn2.reddeer.editor.jbpm.Element;
import org.jboss.tools.bpmn2.reddeer.properties.jbpm.InclusiveGatewayTab;
import org.jboss.tools.bpmn2.reddeer.properties.shell.GatewayConditionSetUpCTab;
import org.jboss.tools.bpmn2.reddeer.properties.shell.GatewayDirectionSetUpCTab;
import org.jboss.tools.bpmn2.reddeer.properties.shell.GatewayPrioritySetUpCTab;

/**
 * 
 */
public class InclusiveGateway extends Element {

	/**
	 * 
	 * @param name
	 */
	public InclusiveGateway(String name) {
		super(name, ElementType.INCLUSIVE_GATEWAY);
	}
	
	public InclusiveGateway(org.jboss.tools.bpmn2.reddeer.editor.Element element) {
		super(element);
	}
	
	/**
	 * 
	 * @param flow
	 * @param lang
	 * @param condition
	 */
	public void setCondition(String flow, String lang, String condition) {
		//properties.getTab("Gateway", InclusiveGatewayTab.class).setCondition(flow, lang, condition);
		graphitiProperties.setUpTabs(new GatewayConditionSetUpCTab(flow, lang, condition));
	}

	/**
	 * 
	 * @param flow
	 */
	public void setDefaultBranch(String flow) {
		properties.getTab("Gateway", InclusiveGatewayTab.class).setDefaultBranch(flow);
	}
	
	/**
	 * 
	 * @param flow
	 * @param priority
	 */
	public void setPriority(String flow, String priority) {
		//properties.getTab("Gateway", InclusiveGatewayTab.class).setPriority(flow, priority);
		graphitiProperties.setUpTabs(new GatewayPrioritySetUpCTab(flow, priority));
	}
	
	/**
	 * 
	 * @param direction
	 */
	public void setDirection(Direction direction) {
//		properties.getTab("Gateway", GatewayTab.class).setDirection(direction);
		graphitiProperties.setUpTabs(new GatewayDirectionSetUpCTab(direction));
	}
	
}
