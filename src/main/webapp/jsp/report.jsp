<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/util.tld" prefix="u"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>${reportName}</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/tooltips.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/kpi.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/table.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/pikaday.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/moment.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/pikaday.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/render/raphael.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/render/charts.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/render/kpi.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/render/table.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/render/ViewContextDisplay.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/render/chart-config.js"></script>
<script>
var chartJson = {};
var kpiJson = {};
var tableJson = {};
var image = {};
var textJson = {};
initDatePicker = function(field) {
	return new Pikaday({
		field: field,
		format: "YYYY-MM-DD",
		minDate: new Date('2000-01-01'),
		maxDate: new Date('2020-12-31'),
		yearRange: [2000,2020]
	});
}
loadViews = function() {
	initDatePicker($("#fromDate")[0]);
	initDatePicker($("#toDate")[0]);
	$(".wrapper.chart").each(function() {
		var id = $(this).data("view-id");
		var $container = $("#" + id);
		$container.empty();
		var chart = Charts.create(id, chartJson[id].content);
		chart.resize($container.width(), $container.height());
	});
	$(".wrapper.kpi").each(function() {
		var id = $(this).data("view-id");
		var $container = $("#" + id);
		$container.empty();
		KPI.create(id, kpiJson[id]);
	});
	$(".wrapper.text").each(function() {
		var id = $(this).data("view-id");
		var $container = $("#" + id);
		$container.empty();
		$container.append(textJson[id].content);
		
	});
	$(".wrapper.table").each(function() {
		var id = $(this).data("view-id");
		var $container = $("#" + id);
		$container.empty();
		Table.create(id, tableJson[id]);
	});
	$(".wrapper.image").each(function() {
		var id = $(this).data("view-id");
		var $container = $("#" + id);
		$container.empty();
		$container.append('<img id="theImg" src="data:image/png;base64,' + image[id] +'" />')
	});
	
	
};
</script>
</head>
<body onLoad="loadViews()">
	<header>
		<a href="${pageContext.request.contextPath}/"><img src="${pageContext.request.contextPath}/images/logo.png" /></a>
		<a class="logout button" href="${pageContext.request.contextPath}/logout">Logout</a>
	</header>
	<div id="page-content">
		<section>
		<h1>${reportName}</h1>
		<form action="" method="POST" class="filter">
			<c:forEach var="attribute" items="${viewAttributes}">
				<div class="filter-attribute">
					<span class="filter-title">${attribute.name}</span>
					<div class="filter-items">
						<c:forEach var="value" items="${attribute.values}">
							<div class="filter-item">
								<c:choose>
									<c:when test="${u:contains(attribute.selectedValues, value.value)}">
										<input type="checkbox" id= "${attribute.code}---${value.value}" name="${attribute.code}---${value.value}" checked="checked" />
									</c:when>
									<c:otherwise>
										<input type="checkbox" id= "${attribute.code}---${value.value}" name="${attribute.code}---${value.value}" />
									</c:otherwise>
								</c:choose>
								<label for="${attribute.code}---${value.value}">${value.label}</label>
							</div>
						</c:forEach>
					</div>
				</div>
			</c:forEach>
			<div class="filter-attribute">
				<span class="filter-title">Date Interval</span>
				<div class="filter-items">
					<div class="interval-item">
						<label for="fromDate">From</label> <input type="text" id="fromDate" name="fromDate" placeholder="yyyy-mm-dd" value="${fromDate}" />
					</div>
					<div class="interval-item">
						<label for="toDate">To</label> <input type="text" id="toDate" name="toDate" placeholder="yyyy-mm-dd" value="${toDate}" />
					</div>
				</div>
			</div>
			<div class="buttons">
				<button type="submit" name="action" value="set">Filter</button>
				<button type="submit" name="action" value="clear">Clear</button>
			</div>
		</form>
		<div>
			<c:forEach var="view" items="${views}">
				<script>
					 // the script defined for the view, injects JSON contents
					${view.script}
				</script>
				<div class="wrapper ${view.cssClass}" id="wrapper-${view.id}" data-view-id="${view.id}">
					<span class="title">${view.title}</span>
					<div class="content" id="${view.id}"></div>
					<span><a href="${pageContext.request.contextPath}/views/${view.urlId}/export/pdf">PDF</a></span>										
				</div>
			</c:forEach>
		</div>
		</section><div class="comments">
			<h1>Comments</h1>
			<form action="${pageContext.request.contextPath}/comment/${reportId}"
				method="POST">
				<textarea name="comment"></textarea>
				<input type="submit" value="Add Comment" />
			</form>
			<div class="comment-list">
				<c:forEach var="comment" items="${comments}">
				<span class="comment" title="${comment.dateTime}"><span
					class="comment-text">${u:formatHtml(comment.text)}</span><span
					class="comment-author">by <span>${comment.authorInfo.name}</span></span></span>
				</c:forEach>
			</div>
		</div>
	</div>
</body>
</html>