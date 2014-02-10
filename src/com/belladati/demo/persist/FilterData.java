package com.belladati.demo.persist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.belladati.sdk.filter.Filter;

/**
 * Stores the currently used report filters for a report.
 * 
 * @author Chris Hennigfeld
 */
public class FilterData implements Serializable {
	// implement serializable to allow persistent session storage
	private static final long serialVersionUID = -4561000545952890572L;
	private final String reportId;
	private final List<Filter<?>> filters = new ArrayList<>();

	public FilterData(String reportId) {
		this.reportId = reportId;
	}

	/**
	 * Sets the filters to the given collection.
	 * 
	 * @param filters filters to set
	 */
	public void set(Collection<? extends Filter<?>> filters) {
		this.filters.clear();
		this.filters.addAll(filters);
	}

	/**
	 * Returns the filters currently set.
	 * 
	 * @return filters currently set
	 */
	public Collection<Filter<?>> get() {
		return Collections.unmodifiableList(filters);
	}

	/**
	 * Returns the ID of the filtered report.
	 * 
	 * @return ID of the filtered report
	 */
	public String getReportId() {
		return reportId;
	}
}
