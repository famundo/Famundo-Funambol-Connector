package com.funambol.famundo.admin;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import com.funambol.admin.AdminException;
import com.funambol.famundo.calendar.CalendarSyncSource;
import com.funambol.famundo.util.FamundoContentType;
import com.funambol.famundo.util.TypeManager;

public class CalendarConfigPanel extends FamundoConfigPanel {
	public static final long serialVersionUID = 5;

    protected JCheckBox _eventValue;
    protected JCheckBox _taskValue;	
	
	protected TypeManager getTypeManager()
	{
		return TypeManager.createCalendarManager();
	}
	
	protected String getPanelName()
	{
		return "Edit Famundo Calendar SyncSource";
	}
	
	public CalendarConfigPanel()
	{
        super();

        this.add(_eventValue, null);
        this.add(_taskValue, null);

        _typeCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                updateEntityTypeCheckBoxes();
            }
        });		
	}
	
    protected int addExtraComponents(int x, int y, int xGap, int yGap)
    {
        _eventValue = new JCheckBox("Events");
        _taskValue  = new JCheckBox("Tasks");

        _eventValue.setFont(defaultFont);
        _eventValue.setSelected(true);
        _eventValue.setBounds(170, y, 100, 25);
        _eventValue.setEnabled(true);

        x += xGap;

        _taskValue.setFont(defaultFont);
        _taskValue.setSelected(true);
        _taskValue.setBounds(170 + xGap, y, 100, 25);
        _taskValue.setEnabled(true);

        return y + yGap;
    }	

	public void updateForm()
	{
		if (!(getSyncSource() instanceof CalendarSyncSource))
		{
			notifyError(
					new AdminException("This is not a CalendarSyncSource! "
							+ "Unable to process SyncSource values."
					)
			);
			return;
		}

		super.updateForm();
	}
	
    protected void updateEntityTypeCheckBoxes()
    {
        if (isSIFSelected())
        {
            _eventValue.setSelected(areEventsAllowed());
            _taskValue.setSelected(areTasksAllowed());

            _eventValue.setEnabled(false);
            _taskValue.setEnabled(false);
        }
        else
        {
            boolean events = true;
            boolean tasks = true;
        	
        	CalendarSyncSource syncSource = (CalendarSyncSource)getSyncSource();
        	switch (syncSource.getAcceptedTypes())
        	{
        		case CalendarSyncSource.ACCEPTEDTYPE_EVENTS:
        			tasks = false;
        			break;
        		case CalendarSyncSource.ACCEPTEDTYPE_TASKS:
        			events = false;
        			break;
        	}

        	_eventValue.setSelected(events);
            _taskValue.setSelected(tasks);

            _eventValue.setEnabled(true);
            _taskValue.setEnabled(true);
        }
    }
    
    protected void validateValues() throws IllegalArgumentException
    {
        super.validateValues();

        if (!_eventValue.isSelected() && !_taskValue.isSelected())
            throw new IllegalArgumentException(
                    "Please check at least one between 'Events' and 'Tasks'.");
    }
    
    protected void getValues()
    {
    	super.getValues();
    	
        if (_eventValue == null || _taskValue == null)
            return;
    	
    	int acceptedTypes;
    	if (_eventValue.isSelected())
    	{
    		if (_taskValue.isSelected())
    			acceptedTypes = CalendarSyncSource.ACCEPTEDTYPE_ALL;
    		else
    			acceptedTypes = CalendarSyncSource.ACCEPTEDTYPE_EVENTS;
    	}
    	else
    		acceptedTypes = CalendarSyncSource.ACCEPTEDTYPE_TASKS;
    	
    	CalendarSyncSource syncSource = (CalendarSyncSource)getSyncSource();
    	syncSource.setAcceptedTypes(acceptedTypes);
    }
    
    private boolean areEventsAllowed()
    {
        if (_typeCombo.getSelectedItem() == null)
            return false;

        FamundoContentType type = (FamundoContentType)_typeCombo.getSelectedItem();
        return "SIF-E".equals(type.getName());
    }

    private boolean areTasksAllowed()
    {
        if (_typeCombo.getSelectedItem() == null)
            return false;

        FamundoContentType type = (FamundoContentType)_typeCombo.getSelectedItem();
        return "SIF-T".equals(type.getName());
    }    
}
