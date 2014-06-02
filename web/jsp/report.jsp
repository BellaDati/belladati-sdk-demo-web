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
<script type="text/javascript" src="${pageContext.request.contextPath}/render/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/render/raphael.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/render/charts.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/render/kpi.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/render/ViewContextDisplay.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/render/chart-config.js"></script>
<script>
chartJson = {};
kpiJson = {};
loadViews = function() {
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
};
</script>
</head>
<body onLoad="loadViews()">
	<header>
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