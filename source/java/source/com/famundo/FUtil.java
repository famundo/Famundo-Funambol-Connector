package com.famundo;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * UTility's for package
 * @author dudi
 *
 */
public class FUtil {
	/**
	 * check if string is null or empty or full of white spaces
	 * @param s String to test
	 * @return boolean true if blank
	 */
	public static boolean isBlank(String s) {
		return s == null || s.equals("") || s.matches("^\\s*$");
	}

	/**
	 * build the full famundo http path to the action like /api/contact/get_sync_item_from_id/21
	 * @param type   FumundoObjectType the object type
	 * @param action the famundo action (e.g get_all_sync_item_keys) 
	 * @param id     Integer id of item will add /id to path (can be null)
	 * @param params String[] http params caller must do the escaping (can be null)
	 * @return String the full http path
	 */
	public static String buildPath(FumundoObjectType type, String action, Integer id, String[] params) {
		StringBuilder sb = new StringBuilder("/api/");
		sb.append(type.getHttpPath()).append("/").append(action);
		
		if(id != null)
			sb.append("/").append(id.intValue());
		
		if((params != null) && (params.length > 0)) {
			params = removeNulls(params);
			if (params.length != 0) {
				for (int i = 0; i < params.length; i++) {
					sb.append( ((i==0) ? "?" : "&") ).append( params[i] );
				}
			}
		}
		return sb.toString();
	}

	/**
	 * build the full famundo http path to the action like /api/contact/get_all_sync_item_keys
	 * @param type   FumundoObjectType the object type
	 * @param action the famundo action (e.g get_all_sync_item_keys) 
	 */
	public static String buildPath(FumundoObjectType type, String action) {
		return buildPath(type, action, null, null);
	}

	/**
	 * build the full famundo http path to the action like /api/contact/get_sync_item_from_id/21
	 * @param type   FumundoObjectType the object type
	 * @param action the famundo action (e.g get_all_sync_item_keys) 
	 * @param id     Integer id of item will add /id to path 
	 */
	public static String buildPath(FumundoObjectType type, String action, int id) {
		return buildPath(type, action, new Integer(id), null);
	}
	
	/**
	 * build the full famundo http path to the action like: 
	 * /api/contact/get_deleted_sync_item_keys?since=YYYY-MM-DD hh:mm:sec&until=YYYY-MM-DD hh:mm:sec
	 * @param type   FumundoObjectType the object type
	 * @param action the famundo action (e.g get_all_sync_item_keys) 
	 * @param params String[] http params caller must do the escaping
	 * @return String the full http path
	 */
	public static String buildPath(FumundoObjectType type, String action, String[] params) {
		return buildPath(type, action, null, params);
	}
	/**
	 * gets the domain name form the url template 
	 * @param String familyUrlTemplate asummes format like "https://<FAMILY_NAME>.famundo.com/";
	 * @return String doamin name (e.g famundo.com )
	 */
	public static String getFamilyUrlTemplateDomain(String familyUrlTemplate)
	{
		// asume format like "https://<FAMILY_NAME>.famundo.com/";
		int i = familyUrlTemplate.indexOf("<FAMILY_NAME>");
		return familyUrlTemplate.substring(i + "<FAMILY_NAME>".length() +1, familyUrlTemplate.length() - 1 );
	}
	
	/**
	 * parse out the family name from a string that may be the url
	 * @param family the family name that may be the full family URL
	 * @param familyUrlTemplate the template to the famundo URLS asummes format like "https://<FAMILY_NAME>.famundo.com/";
	 * @return the family name or empty string
	 */
	public static String parseFamilyName(String family, String familyUrlTemplate)
	{
		if( isBlank(family) )
			return "";
		
		family = family.toLowerCase();
		String domain = Pattern.quote( getFamilyUrlTemplateDomain(familyUrlTemplate) );
		Matcher match = Pattern.compile("^\\s*(https?://)?(.+?)(\\." + domain + "(/.*)?)?\\s*$").matcher(family);
		if( match.matches() )
			family = match.group(2);
		
		return family.trim();
	}

	/**
	 * parse an opejct uid and extract the famundo database id of the object 
	 * @param uid String the object uid (e.g "/family1/contact/125")
	 * @return int the id (e.g 125) or -1 if cant parse it 
	 */
	public static int parseIdFromUid(String uid) {
		if( isBlank(uid) )
			return -1;
		
		Matcher match = Pattern.compile("^.*/(\\d+)\\s*$").matcher(uid);
		if( match.matches() )
			return Integer.parseInt( match.group(1), 10 );
		
		match = Pattern.compile("^\\s*(\\d+)\\s*$").matcher(uid);
		if( match.matches() )
			return Integer.parseInt( match.group(1), 10 );
		
		return -1;
	}

	/**
	 * build a fumondo object uid
	 * @param family String the family name
	 * @param type String the object type (e.g "contact")
	 * @param id int the objects famundo database id
	 * @return String the objects uid
	 */
	public static String id2uid(String family, String type, int id) {
		return new StringBuilder(family).append("/").append(type).append("/").append(id).toString();
	}
		
	/**
	 * build a fumondo object uid
	 * @param family Family 
	 * @param type FumundoObjectType
	 * @param id int the objects famundo database id
	 * @return String the objects uid
	 */
	public static String id2uid(Family family, FumundoObjectType type, int id) {
		return id2uid(family.getFamily(), type.getHttpPath(), id); 
	}

	
	private static String twoDidgits(int i) {
		if(i < 10 )
			return "0" + Integer.toString(i);
		else
			return Integer.toString(i);
	}
	
	/**
	 * Create a https url param for a date value, use for the since untill values.<br/>
	 * format is "name=YYYY-MM-DD+hh:mm:ss"
	 * @param name String http query param name
	 * @param dt Date the date to format
	 * @return String the http query param or null if dt is null
	 */
	public static String date2urlParam(String name, Date dt) {
		if(dt == null)
			return null;
		
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		StringBuffer sb = new StringBuffer();
		sb.append(name).append("=")
		  .append(cal.get(Calendar.YEAR        )  ).append("-")
		  .append(twoDidgits(cal.get(Calendar.MONTH       )+1)).append("-")
		  .append(twoDidgits(cal.get(Calendar.DAY_OF_MONTH)  )).append("+")
		  .append(twoDidgits(cal.get(Calendar.HOUR_OF_DAY )  )).append(":")
		  .append(twoDidgits(cal.get(Calendar.MINUTE      )  )).append(":")
		  .append(twoDidgits(cal.get(Calendar.SECOND      )  ));
		return sb.toString();
		//return String.format("%s=%tY-%<tm-%<td+%<tH:%<tM:%<tS",name, dt);
	}
	
	/**
	 * removes null's from a string array
	 * @param sa String[] string array to remove nulls from
	 * @return a new array or the original one without any nulls
	 */
	public static String[] removeNulls(String[] sa) {
		int i;
		String[] ret = null;
		// count none null values
		int len = sa.length;
		for (i = 0; i < sa.length; i++) {
			if(sa[i] == null) {
				--len;
			}
		}
		
		if(len == sa.length)
			return sa;
		
		// copy none null values
		ret = new String[len];
		int j = 0;
		for (i = 0; i < sa.length; i++) {
			if(sa[i] != null) {
				ret[j] = sa[i];
				++j;
			}
		}
		return ret;
	}
	
	public static Date toUTC(Date dt) {
		return new Date( dt.getTime() + (dt.getTimezoneOffset()*60*1000) );
	}
}
