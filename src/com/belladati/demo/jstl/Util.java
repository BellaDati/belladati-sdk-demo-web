package com.belladati.demo.jstl;

import java.util.Collection;

/**
 * JSTL helper functions.
 * 
 * @author Chris Hennigfeld
 */
public class Util {

	/**
	 * Checks if the given collection contains the specified item.
	 * 
	 * @param collection collection to look in
	 * @param item item to look for
	 * @return <tt>true</tt> if the item was found
	 */
	public static <T> boolean contains(Collection<T> collection, T item) {
		return collection.contains(item);
	}

	/**
	 * Formats the given raw string as HTML.
	 * 
	 * @param raw the raw string
	 * @return HTML-formatted string
	 */
	public static String formatHtml(String raw) {
		// currently we just take care of line breaks
		// in production, a full HTML escape should be used
		return raw.replace("\n", "<br />");
	}
}
