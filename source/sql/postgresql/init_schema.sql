--
-- Initialization data for the Foundation module
--
-- @version $Id: init_schema.sql,v 1.4 2007/02/04 09:21:20 nichele Exp $
--

--
-- Module structure registration
--
delete from fnbl_module where id='famundo';
insert into fnbl_module (id, name, description)
values('famundo','famundo','Famundo Connector');

delete from fnbl_connector where id='famundo';
insert into fnbl_connector(id, name, description)
values('famundo','FamundoConnector','Famundo Connector');

--
-- SyncSource Types
--
delete from fnbl_sync_source_type where id='contact-famundo';
insert into fnbl_sync_source_type(id, description, class, admin_class)
values('contact-famundo','PIM Contact SyncSource','com.famundo.SIFSyncSource','');

--
-- Connectors
--
delete from fnbl_connector_source_type where connector='famundo' and sourcetype='contact-famundo';
insert into fnbl_connector_source_type(connector, sourcetype)
values('famundo','contact-famundo');

--
-- Module - Connector
--
delete from fnbl_module_connector where module='famundo' and connector='famundo';
insert into fnbl_module_connector(module, connector)
values('famundo','famundo');

--
-- SyncSources
--


delete from fnbl_sync_source where uri='scard';
insert into fnbl_sync_source (uri, config, name, sourcetype)
  values('scard', 'famundo/SIFContactSource.xml','scard','contact-famundo');

