<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/util.tld" prefix="u"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>${reportName}</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css" />
</head>
<body>
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
			<script>
				chartJson = {};
				chartLoaded = function(chart) {
					// before this is called, JSON needs to be injected into chartJson
					document.getElementById(chart).contentWindow.init(chartJson[chart]);
				};
				kpiJson = {};
				kpiLoaded = function(kpi) {
					// before this is called, JSON needs to be injected into kpiJson
					document.getElementById(kpi).contentWindow.init(kpiJson[kpi]);
				};
			</script>
			<c:forEach var="view" items="${views}">
				<script>
					 // the script defined for the view, injects JSON contents
					${view.script}
				</script>
				<div class="wrapper">
					<span class="title">${view.title}</span>
					<iframe class="content ${view.cssClass}" id="${view.id}"
						src="${pageContext.request.contextPath}${view.frameSrc}"
						onload="${view.onLoad}"></iframe>
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