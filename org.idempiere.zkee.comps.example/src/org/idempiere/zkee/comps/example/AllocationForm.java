package org.idempiere.zkee.comps.example;

import org.adempiere.webui.panel.CustomForm;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;

public class AllocationForm extends CustomForm {
	private static final long serialVersionUID = 20251216L;
	public AllocationForm() {
		Component form = null;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
			form = Executions.createComponents("~./form.zul", this, null);
		} finally {
			Thread.currentThread().setContextClassLoader(cl);
		}
		
		Selectors.wireComponents(form, this, false);
	}
}