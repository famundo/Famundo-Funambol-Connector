package com.funambol.famundo.admin;

import com.funambol.admin.AdminException;
import com.funambol.famundo.contacts.ContactsSyncSource;
import com.funambol.famundo.util.TypeManager;

public class ContactsConfigPanel extends FamundoConfigPanel {
	public static final long serialVersionUID = 5;

	protected TypeManager getTypeManager()
	{
		return TypeManager.createContactsManager();
	}

	protected String getPanelName()
	{
		return "Edit Famundo Contacts SyncSource";
	}

	public void updateForm()
	{
		if (!(getSyncSource() instanceof ContactsSyncSource))
		{
			notifyError(
					new AdminException("This is not a ContactsSyncSource! "
							+ "Unable to process SyncSource values."
					)
			);
			return;
		}

		super.updateForm();
	}
}
