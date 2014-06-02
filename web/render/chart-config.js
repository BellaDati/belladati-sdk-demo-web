var removeTooltips = function() {
	$('.chart-tooltip-part').remove()
}

var onShowTooltips = function(tooltips, canvas) {
	var contents = []

	for ( var i = 0; i < tooltips.length; i++) {
		var tooltip = tooltips[i];
		var values = tooltip.text.split("<br>");
		if (values.length >= 3 && values[2] && values[2].indexOf("%") > -1) {
			// in pie charts, don't show the absolute value
			values[1] = null;
		}
		contents.push({
			dataId : tooltip.dataId,
			values : values,
			background : tooltip.color,
			foreground : "#FFF",
			target : {
				x : parseFloat(tooltip.x),
				y : parseFloat(tooltip.y)
			}
		})
	}
	new ViewContextGroup(undefined, contents).show();
}

Charts.options.showsTouchLine = false
Charts.options.onShowTooltips = onShowTooltips;
Charts.options.onHideTooltip = removeTooltips;
Charts.options.font = "Helvetica";
Charts.options.fontSize = 14;
Charts.options.fontHeight = 14;

Charts.options.padding.top = 0;
Charts.options.padding.bottom = 0;
Charts.options.padding.left = 0;
Charts.options.padding.right = 0;