package com.belladati.demo.view;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.belladati.sdk.view.TableView.Table;
import com.belladati.sdk.view.View;
import com.belladati.sdk.view.ViewType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
	private final Future<?> future;
	private String script;

	public ViewDisplay(View view, Future<?> future) {
		this.id = view.getType().name().toLowerCase() + "-" + view.getId();
		this.title = view.getName();
		this.type = view.getType();
		this.future = future;
	}

	/**
	 * Tells this view display to invoke the future passed in the constructor.
	 * This may block the current thread until the future is resolved.
	 */
	public void processFuture() {
		// get JSON data from the future
		// then set up a script injecting the JSON into the page
		if (future == null) {
			return;
		}
		switch (type) {
		case CHART:
			try {
				script = "chartJson['" + id + "'] = " + postProcess((JsonNode) future.get());
			} catch (JsonProcessingException e) {
				logger.log(Level.WARNING, "Error getting data", e);
			} catch (InterruptedException e) {
				logger.log(Level.WARNING, "Error getting data", e);
			} catch (ExecutionException e) {
				logger.log(Level.WARNING, "Error getting data", e);
			}
			break;
		case KPI:
			try {
				script = "kpiJson['" + id + "'] = " + postProcess((JsonNode) future.get());
			} catch (JsonProcessingException e) {
				logger.log(Level.WARNING, "Error getting data", e);
			} catch (InterruptedException e) {
				logger.log(Level.WARNING, "Error getting data", e);
			} catch (ExecutionException e) {
				logger.log(Level.WARNING, "Error getting data", e);
			}
			break;
		case TABLE:
			try {
				script = "tableJson['" + id + "'] = " + postProcess((Table) future.get());
			} catch (InterruptedException e) {
				logger.log(Level.WARNING, "Error getting data", e);
			} catch (ExecutionException e) {
				logger.log(Level.WARNING, "Error getting data", e);
			}
			break;
		default: // do nothing
		}
	}

	private String postProcess(JsonNode node) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(node);
	}

	private String postProcess(final Table table) throws InterruptedException {
		final int rowCount = table.getRowCount();
		final int columnCount = table.getColumnCount();
		final ObjectNode tableNode = new ObjectMapper().createObjectNode();

		ExecutorService service = Executors.newCachedThreadPool();
		service.submit(new Runnable() {
			@Override
			public void run() {
				tableNode.put("left", table.loadLeftHeader(0, rowCount).get("content"));
			}
		});
		service.submit(new Runnable() {
			@Override
			public void run() {
				tableNode.put("top", table.loadTopHeader(0, columnCount).get("content"));
			}
		});
		service.submit(new Runnable() {
			@Override
			public void run() {
				tableNode.put("data", table.loadData(0, rowCount, 0, columnCount).get("content"));
			}
		});

		service.shutdown();
		service.awaitTermination(10, TimeUnit.SECONDS);
		return tableNode.toString();
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
