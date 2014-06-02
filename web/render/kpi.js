window.KPI = {};
KPI.create = function(id, data) {
	var content = document.getElementById(id);
	for ( var i = 0; i < data.values.length; i++) {
		var value = data.values[i];
		// entry element containing everything
		var entry = document.createElement("div");
		// element class includes the size, if one is set
		entry.className = "kpiEntry" + (value.size ? " " + value.size : "");
		content.appendChild(entry);
		
		// the entry's value
		var entryValue = document.createElement("div");
		entryValue.className = "kpiEntryValue";
		if (value.style) {
			entryValue.setAttribute("style", value.style);
		}
		entry.appendChild(entryValue);
		
		// if we have a symbol, append it to the value
		if (value.symbolValue) {
			var symbol = document.createElement("span");
			symbol.innerHTML = value.symbolValue;
			entryValue.appendChild(symbol);
		}

		// followed by the numerical value
		if (value.numberValue) {
			entryValue.innerHTML = value.numberValue;
		}
		
		// if we have a percentage, round to two digits and append
		if (value.percent) {
			var percent = document.createElement("span");
			percent.className = "percent";
			percent.innerHTML = (Math.round(value.percent * 100) / 100)
					.toFixed(2);
			entryValue.appendChild(percent);
		}
		
		// the entry's caption
		var caption = document.createElement("div");
		caption.className = "kpiEntryCaption";
		caption.innerHTML = value.caption;
		entry.appendChild(caption);
	}
}