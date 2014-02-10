package com.belladati.demo.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.belladati.sdk.report.Attribute;
import com.belladati.sdk.report.AttributeValue;

/**
 * A data attribute shown in the report display. Similar to {@link Attribute},
 * but additionally holds a list of values selected in a filter.
 * 
 * @author Chris Hennigfeld
 */
public class ViewAttribute {

	private final String code;
	private final String name;
	private List<String> selectedValues = new ArrayList<>();

	private final List<AttributeValue> values = new ArrayList<>();

	public ViewAttribute(Attribute attribute) {
		this.code = attribute.getCode();
		this.name = attribute.getName();
		this.values.addAll(attribute.getValues().get());
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public List<AttributeValue> getValues() {
		return values;
	}

	public void setSelectedValues(List<String> selectedValues) {
		this.selectedValues.clear();
		this.selectedValues.addAll(selectedValues);
	}

	public List<String> getSelectedValues() {
		return Collections.unmodifiableList(selectedValues);
	}
}
