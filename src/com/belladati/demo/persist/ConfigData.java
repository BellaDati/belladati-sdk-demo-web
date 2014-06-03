package com.belladati.demo.persist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.belladati.sdk.filter.Filter;
import com.belladati.sdk.intervals.DateUnit;
import com.belladati.sdk.intervals.Interval;
import com.belladati.sdk.intervals.TimeUnit;

/**
 * Stores the currently used report configuration for a report.
 * 
 * @author Chris Hennigfeld
 */
public class ConfigData implements Serializable {
	// implement serializable to allow persistent session storage
	private static final long serialVersionUID = -4561000545952890572L;
	private final String reportId;
	private final List<Filter<?>> filters = new ArrayList<>();
	private Interval<DateUnit> dateInterval;
	private Interval<TimeUnit> timeInterval;

	public ConfigData(String reportId) {
		this.reportId = reportId;
	}

	/**
	 * Sets the filters to the given collection.
	 * 
	 * @param filters filters to set
	 */
	public void setFilters(Collection<? extends Filter<?>> filters) {
		this.filters.clear();
		this.filters.addAll(filters);
	}

	/**
	 * Sets the date interval to the given interval
	 * 
	 * @param dateInterval interval to set
	 */
	public void setDateInterval(Interval<DateUnit> dateInterval) {
		this.dateInterval = dateInterval;
	}

	/**
	 * Sets the time interval to the given interval
	 * 
	 * @param timeInterval interval to set
	 */
	public void setTimeInterval(Interval<TimeUnit> timeInterval) {
		this.timeInterval = timeInterval;
	}

	/**
	 * Returns the filters currently set.
	 * 
	 * @return filters currently set
	 */
	public Collection<Filter<?>> getFilters() {
		return Collections.unmodifiableList(filters);
	}

	/**
	 * Returns the date interval currently set.
	 * 
	 * @return date interval currently set, or <tt>null</tt>
	 */
	public Interval<DateUnit> getDateInterval() {
		return dateInterval;
	}

	/**
	 * Returns the time interval currently set.
	 * 
	 * @return time interval currently set, or <tt>null</tt>
	 */
	public Interval<TimeUnit> getTimeInterval() {
		return timeInterval;
	}

	/**
	 * Returns the ID of the filtered report.
	 * 
	 * @return ID of the filtered report
	 */
	public String getReportId() {
		return reportId;
	}

	/**
	 * Clears the current configuration.
	 */
	public void clear() {
		setFilters(Collections.<Filter<?>> emptyList());
		setDateInterval(null);
		setTimeInterval(null);
	}
}
