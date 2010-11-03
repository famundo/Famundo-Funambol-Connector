package com.funambol.famundo.admin;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;

import com.funambol.admin.AdminException;
import com.funambol.admin.ui.SourceManagementPanel;
import com.funambol.famundo.util.FamundoContentType;
import com.funambol.famundo.util.FamundoSyncSource;
import com.funambol.famundo.util.TypeManager;
import com.funambol.framework.engine.source.ContentType;
import com.funambol.framework.engine.source.SyncSourceInfo;

public abstract class FamundoConfigPanel extends SourceManagementPanel implements Serializable {
	public static final String NAME_ALLOWED_CHARS = 
		"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-_.";
	
	protected JLabel _panelName = new JLabel();

	protected TitledBorder _titledBorder;

	protected JLabel _nameLabel = new JLabel();
	protected JTextField _nameValue = new JTextField();

	protected JLabel _typeLabel = new JLabel();
	protected JComboBox _typeCombo = new JComboBox();

	protected JLabel _sourceUriLabel = new JLabel();
	protected JTextField _sourceUriValue = new JTextField();
	
	protected JLabel _domainLabel = new JLabel();
	protected JTextField _domainValue = new JTextField();
	 
	protected JButton _confirmButton = new JButton();	
	
	
	protected abstract TypeManager getTypeManager();
	protected abstract String getPanelName();

	public FamundoConfigPanel()
	{
		init();
	}
	
	private void init()
	{
		this.setLayout(null);

		_titledBorder = new TitledBorder("");

		_panelName.setFont(titlePanelFont);
		_panelName.setText(getPanelName());
		_panelName.setBounds(new Rectangle(14, 5, 316, 28));
		_panelName.setAlignmentX(SwingConstants.CENTER);
		_panelName.setBorder(_titledBorder);

		final int LABEL_X = 14;
		final int VALUE_X = 170;
		int y = 60;
		final int GAP_X = 150;
		final int GAP_Y = 30;

		_sourceUriLabel.setText("Source URI: ");
		_sourceUriLabel.setFont(defaultFont);
		_sourceUriLabel.setBounds(new Rectangle(LABEL_X, y, 150, 18));
		_sourceUriValue.setFont(defaultFont);
		_sourceUriValue.setBounds(new Rectangle(VALUE_X, y, 350, 18));

		y += GAP_Y;

		_nameLabel.setText("Name: ");
		_nameLabel.setFont(defaultFont);
		_nameLabel.setBounds(new Rectangle(LABEL_X, y, 150, 18));
		_nameValue.setFont(defaultFont);
		_nameValue.setBounds(new Rectangle(VALUE_X, y, 350, 18));

		y += GAP_Y;

		_typeLabel.setText("Type: ");
		_typeLabel.setFont(defaultFont);
		_typeLabel.setBounds(new Rectangle(LABEL_X, y, 150, 18));
		_typeCombo.setFont(defaultFont);
		_typeCombo.setBounds(new Rectangle(VALUE_X, y, 350, 18));

		y += GAP_Y;

		_domainLabel.setText("Domain (.famundo.com): ");
		_domainLabel.setFont(defaultFont);
		_domainLabel.setBounds(new Rectangle(LABEL_X, y, 150, 18));
		_domainValue.setFont(defaultFont);
		_domainValue.setBounds(new Rectangle(VALUE_X, y, 350, 18));

		y += GAP_Y;
		int x = LABEL_X;

		y = addExtraComponents(x, y, GAP_X, GAP_Y);
		
		y += GAP_Y;
		y += GAP_Y;

		_confirmButton.setFont(defaultFont);
		_confirmButton.setText("Add");
		_confirmButton.setBounds(VALUE_X, y, 70, 25);
		
		_confirmButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event ) {
				try
				{
					validateValues();
					getValues();
					if (getState() == STATE_INSERT)
					{
						FamundoConfigPanel.this.actionPerformed(
								new ActionEvent(FamundoConfigPanel.this,
										ACTION_EVENT_INSERT, event.getActionCommand()));
					}
					else
					{
						FamundoConfigPanel.this.actionPerformed(
								new ActionEvent(FamundoConfigPanel.this, 
										ACTION_EVENT_UPDATE, event.getActionCommand()));
					}
				}
				catch (Exception e)
				{
					notifyError(new AdminException(e.getMessage()));
				}
			}
		});

		this.add(_panelName        , null);
		this.add(_nameLabel        , null);
		this.add(_sourceUriLabel   , null);
		this.add(_sourceUriValue   , null);
		this.add(_nameValue        , null);
		this.add(_domainLabel      , null);
		this.add(_domainValue      , null);
		this.add(_typeLabel        , null);
		this.add(_typeCombo        , null);
		this.add(_confirmButton    , null);
	}
	
	public void updateForm()
	{
		FamundoSyncSource syncSource = (FamundoSyncSource)getSyncSource();
		if (getState() == STATE_INSERT)
		{
			_confirmButton.setText("Add");
		}
		else if (getState() == STATE_UPDATE)
		{
			_confirmButton.setText("Save");
		}
		
		_sourceUriValue.setText(syncSource.getSourceURI());
		_nameValue.setText(syncSource.getName());

		if (syncSource.getSourceURI() != null)
		{
			_sourceUriValue.setEditable(false);
		}
		
		Properties properties = syncSource.getProperties();
		if (properties != null)
			_domainValue.setText(properties.getProperty("domain"));
		
		_typeCombo.removeAllItems();
		ArrayList types = getTypeManager().getTypes();
		for (int i = 0; i < types.size(); i++)
		{
			FamundoContentType type = (FamundoContentType)types.get(i);
			_typeCombo.addItem(type);
			if (type.equals(syncSource.getContentType()))
				_typeCombo.setSelectedIndex(i);
		}
	}
	
	protected void validateValues() throws IllegalArgumentException
	{
		String value = null;

		value = _nameValue.getText();
		if (StringUtils.isEmpty(value))
		{
			throw new IllegalArgumentException(
					"Field 'Name' cannot be empty. "
					+ "Please provide a SyncSource name.");
		}

		if (!StringUtils.containsOnly(value, NAME_ALLOWED_CHARS.toCharArray()))
		{
			throw new IllegalArgumentException(
					"Only the following characters are allowed for field 'Name':"
					+ "\n" + NAME_ALLOWED_CHARS);
		}

		value = _typeCombo.getSelectedItem().toString();
		if (StringUtils.isEmpty(value))
		{
			throw new IllegalArgumentException("Field 'Type' cannot be empty. "
					+ "Please provide a SyncSource type.");
		}

		value = _sourceUriValue.getText();
		if (StringUtils.isEmpty(value))
		{
			throw new IllegalArgumentException(
					"Field 'Source URI' cannot be empty. "
					+ "Please provide a SyncSource URI.");
		}
		
		value = _domainValue.getText();
		if (!value.startsWith("."))
		{
			throw new IllegalArgumentException(
					"Domain name must start with a dot."
					);
		}
	}
	
	protected void getValues()
	{
		FamundoSyncSource syncSource = (FamundoSyncSource)getSyncSource();
		
		syncSource.setSourceURI(_sourceUriValue.getText().trim());
		syncSource.setName(_nameValue.getText().trim());
		FamundoContentType type = (FamundoContentType)_typeCombo.getSelectedItem();
		syncSource.setContentType(type);
		
		Properties properties = syncSource.getProperties();
		if (properties == null)
			properties = new Properties();
		properties.setProperty("domain", _domainValue.getText().trim());
		syncSource.setProperties(properties);
		
		ContentType contentType = new ContentType(type.getContentType(), type.getVersion());
		syncSource.setInfo(new SyncSourceInfo(new ContentType[] {contentType}, 0));
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(525, 360);
	}
	
    protected int addExtraComponents(int x, int y, int xGap, int yGap)
    {
        return y;
    }
    
    protected boolean isSIFSelected()
    {
        if (_typeCombo.getItemCount() == 0)
            return false;

        FamundoContentType type = (FamundoContentType)_typeCombo.getSelectedItem();
        return type.getName().startsWith("SIF");
    }    
}
