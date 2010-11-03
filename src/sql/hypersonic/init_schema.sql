-- Famundo sql

delete from fnbl_sync_source_type where id='famundo_contacts';
delete from fnbl_sync_source_type where id='famundo_calendar';
insert into fnbl_sync_source_type(id, description, class, admin_class)
values('famundo_contacts','Famundo Contacts SyncSource','com.funambol.famundo.contacts.ContactsSyncSource','com.funambol.famundo.admin.ContactsConfigPanel');
insert into fnbl_sync_source_type(id, description, class, admin_class)
values('famundo_calendar','Famundo Calendar SyncSource','com.funambol.famundo.calendar.CalendarSyncSource','com.funambol.famundo.admin.CalendarConfigPanel');

delete from fnbl_module where id='famundo';
insert into fnbl_module (id, name, description)
values('famundo','famundo','Famundo');

delete from fnbl_connector where id='famundo';
insert into fnbl_connector(id, name, description, admin_class)
values('famundo','FamundoConnector','Famundo Connector','');

delete from fnbl_connector_source_type where connector='famundo' and sourcetype='famundo_contacts';
delete from fnbl_connector_source_type where connector='famundo' and sourcetype='famundo_calendar';
insert into fnbl_connector_source_type(connector, sourcetype)
values('famundo','famundo_contacts');
insert into fnbl_connector_source_type(connector, sourcetype)
values('famundo','famundo_calendar');

delete from fnbl_module_connector where module='famundo' and connector='famundo';
insert into fnbl_module_connector(module, connector)
values('famundo','famundo');