package com.belladati.demo.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.belladati.demo.persist.FilterData;
import com.belladati.demo.view.ViewAttribute;
import com.belladati.demo.view.ViewDisplay;
import com.belladati.sdk.filter.Filter;
import com.belladati.sdk.filter.Filter.MultiValueFilter;
import com.belladati.sdk.filter.FilterOperation;
import com.belladati.sdk.filter.FilterValue;
import com.belladati.sdk.report.Attribute;
import com.belladati.sdk.report.AttributeValue;
import com.belladati.sdk.report.Comment;
import com.belladati.sdk.report.Report;
import com.belladati.sdk.view.View;
import com.belladati.sdk.view.ViewType;

/**
 * Controller handling reports.
 * 
 * @author Chris Hennigfeld
 */
@Controller
public class ReportController {

	private static final Logger logger = Logger.getLogger(ReportController.class.getName());
	private static final String FILTER_DATA = "filters";

	@Autowired
	private ServiceManager serviceManager;

	@Autowired
	private HttpSession session;

	/**
	 * Handles the root URL.
	 */
	@RequestMapping("/")
	public ModelAndView initialUrl() {
		if (serviceManager.isLoggedIn()) {
			return new ModelAndView("redirect:/reports");
		} else {
			return new ModelAndView("redirect:/login");
		}
	}

	/**
	 * Displays the report list.
	 */
	@RequestMapping("/reports")
	public ModelAndView showReports() {
		if (!serviceManager.isLoggedIn()) {
			return new ModelAndView("redirect:/login");
		}
		ModelAndView modelAndView = new ModelAndView("reports");
		// load report list from BellaDati, pass to view
		modelAndView.addObject("reports", serviceManager.getService().getReportInfo().load().toList());
		return modelAndView;
	}

	/**
	 * Displays a single report.
	 * 
	 * @param reportId ID of the report to display
	 */
	@RequestMapping(value = "/reports/{id}", method = RequestMethod.GET)
	public ModelAndView showReportViews(final @PathVariable("id") String reportId) throws ExecutionException,
		InterruptedException {
		if (!serviceManager.isLoggedIn()) {
			return new ModelAndView("redirect:/login");
		}
		ModelAndView modelAndView = new ModelAndView("report");

		Map<?, ?> sessionData = (Map<?, ?>) session.getAttribute(FILTER_DATA);
		final FilterData filterData = sessionData != null ? (FilterData) sessionData.get(reportId) : null;
		final Report report = serviceManager.getService().loadReport(reportId);
		modelAndView.addObject("reportName", report.getName());
		modelAndView.addObject("reportId", report.getId());

		// start executor service, submit various parallel queries
		ExecutorService service = Executors.newCachedThreadPool();

		// query view data
		List<ViewDisplay> viewDisplays = new ArrayList<>();
		for (final View view : report.getViews()) {
			if (view.getType() == ViewType.CHART || view.getType() == ViewType.KPI || view.getType() == ViewType.TABLE) {
				Future<?> future = service.submit(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						if (filterData != null) {
							return view.loadContent(filterData.get());
						}
						return view.loadContent();
					}
				});
				viewDisplays.add(new ViewDisplay(view, future));
			}
		}

		// query report comments
		Future<List<Comment>> commentFuture = service.submit(new Callable<List<Comment>>() {
			@Override
			public List<Comment> call() throws Exception {
				return report.getComments().load().toList();
			}
		});

		// query attribute values
		List<Future<Attribute>> attributeLoaders = new ArrayList<>();
		for (final Attribute attribute : report.getAttributes()) {
			if (isFilter(reportId, attribute)) {
				attributeLoaders.add(service.submit(new Callable<Attribute>() {
					@Override
					public Attribute call() {
						attribute.getValues().load();
						return attribute;
					}
				}));
			}
		}

		// once all queries are submitted, start processing their responses

		// store view content
		for (ViewDisplay viewDisplay : viewDisplays) {
			viewDisplay.processFuture();
		}
		modelAndView.addObject("views", viewDisplays);

		// store comments
		modelAndView.addObject("comments", commentFuture.get());

		// store attribute values
		List<ViewAttribute> viewAttributes = new ArrayList<>();
		for (Future<Attribute> future : attributeLoaders) {
			Attribute attribute = future.get();
			viewAttributes.add(new ViewAttribute(attribute));
		}
		// we're done with the executor
		service.shutdown();

		modelAndView.addObject("viewAttributes", viewAttributes);
		if (filterData != null) {
			for (Filter<?> item : filterData.get()) {
				for (ViewAttribute viewAttribute : viewAttributes) {
					if (viewAttribute.getCode().equals(item.getAttribute().getCode())) {
						List<String> values = new ArrayList<>();
						for (AttributeValue value : ((MultiValueFilter) item).getValues()) {
							values.add(value.getValue());
						}
						viewAttribute.setSelectedValues(values);
					}
				}
			}
		}
		return modelAndView;
	}

	/**
	 * Sets or clears filters for the given report.
	 * 
	 * @param reportId ID of the report to filter
	 * @param action <tt>set</tt> to set a filter, otherwise it's cleared
	 * @param request request containing filter parameters
	 */
	@RequestMapping(value = "/reports/{id}", method = RequestMethod.POST)
	public ModelAndView setFilter(@PathVariable("id") String reportId, @RequestParam("action") String action,
		HttpServletRequest request) {
		// read filter storage from session, create if it doesn't exist
		@SuppressWarnings("unchecked")
		Map<String, FilterData> sessionFilters = (Map<String, FilterData>) session.getAttribute(FILTER_DATA);
		if (sessionFilters == null) {
			sessionFilters = new HashMap<>();
			session.setAttribute(FILTER_DATA, sessionFilters);
		}

		// find filter data for given report, create if it doesn't exist
		FilterData filterData = sessionFilters.get(reportId);
		if (filterData == null) {
			filterData = new FilterData(reportId);
			sessionFilters.put(reportId, filterData);
		}

		// create filter collection, empty to clear
		Collection<? extends Filter<?>> filters = new ArrayList<>();

		if ("set".equals(action)) {
			// we have something to filter
			filters = parseFilters(reportId, request);
		}
		filterData.set(filters);
		return new ModelAndView("redirect:/reports/" + reportId);
	}

	/**
	 * Creates filters based on parameters set in the given request.
	 * 
	 * @param reportId ID of the report to filter
	 * @param request request containing filter parameters
	 * @return filters from the given request
	 */
	private Collection<? extends Filter<?>> parseFilters(String reportId, HttpServletRequest request) {
		// since we're using checkboxes, every attribute value comes out
		// as a separate request parameter
		// each value is formatted as CODE---VALUE
		Map<String, MultiValueFilter> filters = new HashMap<>();
		for (String param : request.getParameterMap().keySet()) {
			if (param.contains("---")) {
				String[] pieces = param.split("---");
				// first piece is the code
				String code = pieces[0];
				// second piece is the value
				String value = pieces[1];
				if (!filters.containsKey(code)) {
					// we don't have the code yet, create a new filter for it
					filters.put(code, FilterOperation.IN.createFilter(serviceManager.getService(), reportId, code));
				}
				// add the value to the list of filtered values
				filters.get(code).addValue(new FilterValue(value));
			}
		}
		return filters.values();
	}

	/**
	 * Loads the thumbnail image for the report with the given ID.
	 * 
	 * @param id ID of the report
	 * @return the report's thumbnail, or <tt>null</tt> if no thumbnail is found
	 */
	@RequestMapping(value = "/reports/{id}/thumbnail", method = RequestMethod.GET, produces = "image/png")
	@ResponseBody
	public byte[] getThumbnail(@PathVariable String id) {
		try {
			BufferedImage thumbnail = (BufferedImage) serviceManager.getService().loadReportThumbnail(id);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(thumbnail, "png", baos);
			baos.flush();
			byte[] bytes = baos.toByteArray();
			baos.close();
			return bytes;
		} catch (IOException e) {
			logger.log(Level.WARNING, "Error loading image", e);
		}
		return null;
	}

	/**
	 * Posts a comment to the report with the given ID.
	 * 
	 * @param id ID of the report
	 * @param comment text of the comment
	 */
	@RequestMapping(value = "/comment/{id}", method = RequestMethod.POST)
	public ModelAndView createComment(@PathVariable String id, @RequestParam("comment") String comment) {
		serviceManager.getService().postComment(id, comment);
		return new ModelAndView("redirect:/reports/" + id);
	}

	/**
	 * Returns a list of attribute codes used as filters for the given report.
	 * 
	 * @param reportId ID of the report whose filters to find
	 * @return a list of attribute codes used as filters for the given report
	 */
	private List<String> getReportFilters(String reportId) {
		// simple implementation; all reports use the same filters
		return Arrays.asList("L_CITY", "L_PRODUCT");
	}

	/**
	 * Checks whether the given attribute may be used as a filter for the given
	 * report.
	 * 
	 * @param reportId ID of the report whose filters to check
	 * @param attribute attribute that may be used for filtering
	 * @return <tt>true</tt> if the attribute can be used to filter the report
	 */
	private boolean isFilter(String reportId, Attribute attribute) {
		for (String filterCode : getReportFilters(reportId)) {
			if (filterCode.equals(attribute.getCode())) {
				return true;
			}
		}
		return false;
	}
}
