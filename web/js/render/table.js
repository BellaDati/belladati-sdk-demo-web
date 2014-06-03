window.Table = {};
Table.create = function(id, data) {
	var buildTile = function(id) {
		var $tile = $("<div class='tile'>");
		var $inner = $("<div id='" + id + "' class='inner'>");
		var $table = $("<table>");
		$tile.append($inner);
		$inner.append($table);
		return $tile;
	};
	// build table structure
	$parent = $("#" + id);
	$parent.empty();
	var $container = $("<div class='table-layout'>");
	$parent.append($container);
	var totalWidth = $parent.innerWidth();
	var totalHeight = $parent.innerHeight();
	var $headerRow = $("<div class='row'>").appendTo($container);
	var $dataRow = $("<div class='row'>").appendTo($container);
	$headerRow.append($("<div id='top-left' class='tile'>"));
	$headerRow.append(buildTile("top"));
	$dataRow.append(buildTile("left"));
	$dataRow.append(buildTile("data"));

	// fill table with content
	var $topTable = $container.find("#top table");
	var $leftTable = $container.find("#left table");
	var $dataTable = $container.find("#data table");
	$topTable.html(data.top);
	$leftTable.html(data.left);
	$dataTable.html(data.data);
	
	// adjust table sizing
	var leftWidth = Math.min(Math.floor(totalWidth / 3), 240);
	var rightWidth = totalWidth - leftWidth;
	$("#left").css("width", leftWidth);
	$("#top, #data").css("width", rightWidth);

	var columnWidth = 0;
	var columnCount = 0;
	$topTable.find("tr:last-child th").each(function() {
		columnWidth = Math.max(columnWidth, $(this).outerWidth());
		columnCount++;
	});

	var rightInnerWidth = columnWidth * columnCount;
	$topTable.css("width", rightInnerWidth);
	$dataTable.css("width", rightInnerWidth);
	// table-layout needs to be set after the width for Chrome
	$topTable.css("table-layout", "fixed");
	$dataTable.css("table-layout", "fixed");

	var topHeight = $topTable.height();
	var dataHeight = totalHeight - topHeight;
	$("#left, #data").css("max-height", dataHeight);

	$("#data").scroll(function() {
		$("#top").scrollLeft($(this).scrollLeft());
		$("#left").scrollTop($(this).scrollTop());
	});

	$(".ddlevel1").click(function() {
		if (window.parent.loadProduct) {
			window.parent.loadProduct($(this).text());
		}
	});
};