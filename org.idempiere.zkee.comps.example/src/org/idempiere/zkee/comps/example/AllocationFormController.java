package org.idempiere.zkee.comps.example;

import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.IFormController;
import org.compiere.apps.form.Allocation;
import org.idempiere.ui.zk.annotation.Form;
import org.zkoss.zk.ui.select.Selectors;

@Form
public class AllocationFormController extends Allocation implements IFormController {
	private AllocationForm allocationForm;
	
	public AllocationFormController () {
		allocationForm = new AllocationForm();
		Selectors.wireEventListeners(allocationForm, this);
		try {
			super.dynInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ADForm getForm () {
		return allocationForm;
	}
}
