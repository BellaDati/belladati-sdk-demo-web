package com.belladati.demo.view;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.belladati.sdk.view.View;
import com.belladati.sdk.view.ViewType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * View object representing a single chart or KPI view.
 * 
 * @author Chris Hennigfeld
 */
public class ViewDisplay {

	private static final Logger logger = Logger.getLogger(ViewDisplay.class.getName());

	private final String id;
	private final String title;
	private final ViewType type;
	private final Future<JsonNode> future;
	private final String frameSrc;
	private final String onLoad;
	private String script;

	public ViewDisplay(View view, Future<JsonNode> future) {
		this.id = view.getType().name().toLowerCase() + "-" + view.getId();
		this.title = view.getName();
		this.type = view.getType();
		this.future = future;
		if (type == ViewType.CHART) {
			this.frameSrc = "/render/Chart.html";
			this.onLoad = "chartLoaded(this.id)";
		} else if (type == ViewType.KPI) {
			this.frameSrc = "/render/KPI.html";
			this.onLoad = "kpiLoaded(this.id)";
		} else {
			this.frameSrc = "";
			this.onLoad = "";
		}
	}

	/**
	 * Tells this view display to invoke the future passed in the constructor.
	 * This may block the current thread until the future is resolved.
	 */
	public void processFuture() {
		// get JSON data from the future
		// then set up a script injecting the JSON into the page
		switch (type) {
		case CHART:
			if (future != null) {
				try {
					script = "chartJson['" + id + "'] = " + new ObjectMapper().writeValueAsString(future.get());
				} catch (JsonProcessingException e) {
					logger.log(Level.WARNING, "Error getting data", e);
				} catch (InterruptedException e) {
					logger.log(Level.WARNING, "Error getting data", e);
				} catch (ExecutionException e) {
					logger.log(Level.WARNING, "Error getting data", e);
				}
			}
			break;
		case KPI:
			if (future != null) {
				try {
					script = "kpiJson['" + id + "'] = " + new ObjectMapper().writeValueAsString(future.get());
				} catch (JsonProcessingException e) {
					logger.log(Level.WARNING, "Error getting data", e);
				} catch (InterruptedException e) {
					logger.log(Level.WARNING, "Error getting data", e);
				} catch (ExecutionException e) {
					logger.log(Level.WARNING, "Error getting data", e);
				}
			}
		default: // do nothing
		}
	}

	/**
	 * ID to use for the view DOM element.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Title shown for the view.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * iFrame src attribute, should be one of the HTML pages in <tt>render</tt>.
	 */
	public String getFrameSrc() {
		return frameSrc;
	}

	/**
	 * onLoad script to call when the iFrame loads. Should trigger rendering.
	 */
	public String getOnLoad() {
		return onLoad;
	}

	/**
	 * A script to invoke for the view in the context of the main page.
	 */
	public String getScript() {
		return script;
	}

	/**
	 * CSS class to set on the iFrame.
	 */
	public String getCssClass() {
		return type.name().toLowerCase();
	}
}
