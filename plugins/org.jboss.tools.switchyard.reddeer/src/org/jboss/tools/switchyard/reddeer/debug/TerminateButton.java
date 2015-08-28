package org.jboss.tools.switchyard.reddeer.debug;

import org.jboss.reddeer.swt.impl.shell.WorkbenchShell;
import org.jboss.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.jboss.reddeer.common.matcher.RegexMatcher;
import org.jboss.reddeer.core.matcher.WithTooltipTextMatcher;

public class TerminateButton extends DefaultToolItem {

	public TerminateButton() {
		super(new WorkbenchShell(), new WithTooltipTextMatcher(new RegexMatcher("Terminate.*")));
	}

}
