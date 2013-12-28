App.Views.Home = Backbone.View.extend({

	template: _.template($("#home-template").html()),

	initialize: function() {
		this.$el = $("#main-container");
	},

	render: function() {
	
		App.Loading.render();

		$.get(globals.rootUrl + "/stats", $.proxy(function(data) {
			this.$el.html(this.template({stats: data}));
			this._setupGraphic(data);

			App.Loading.dispose();
		}, this))

		.fail(function(xhr, error, errorMsg) {
			App.ErrorPopup.setMessage('Erreur lors du chargement de la page : ' + errorMsg);
			App.ErrorPopup.render();

			App.Loading.dispose();
		});
	},
	_setupGraphic: function(data) {

		var xLabels = new Array;
		var values  = new Array;

		var previousDate = null;
		$.each(data.elements, $.proxy(function(index, element) {
			var theDate = new Date(element.timestamp);

			// If dates are not consecutive, then fill array to have an 'understable' X scale
			if(previousDate != null && !App.Utils.DateUtil.checkDaysBetweenDates(theDate, previousDate, 1)) {

				// Compute weight average to smooth the curve
				var currentWeight = data.elements[index-1].val;

				var dayCount = App.Utils.DateUtil.getDaysBetweenDates(theDate, previousDate);
				var step = this._computeAverage(currentWeight, element.val, dayCount);

				while(!App.Utils.DateUtil.checkDaysBetweenDates(theDate, previousDate, 1)) {
					// Get next day and push data
					previousDate = new Date(App.Utils.DateUtil.DAY_IN_MS + previousDate.getTime())
					
					currentWeight += step;

					xLabels.push(this._formatDate(previousDate));
					values.push(currentWeight);
				}
			}

			xLabels.push(this._formatDate(theDate));
			values.push(element.val);

			previousDate = theDate;
		}, this));

		$('#fitness-abstract-chart').highcharts({
                        title: {
                                text: 'Courbe de poids',
                                x: -20
                        },
			xAxis: {
                                categories: xLabels,
				minTickInterval: 10,
				type: 'datetime'
                        },
                        yAxis: {
                                title: {
                                        text: 'Poids (kg)'
                                },
                                plotLines: [{
                                        value: 0,
                                        width: 1,
                                        color: '#808080'
                                }]
                        },
			tooltip: {
			    pointFormat: '{series.name}: <b>{point.y}</b><br/>',
			    valueSuffix: ' kg'
			},
                        series: [{
                                name: 'Poids',
                                data: values
                        }]

		});
	},
	_formatDate: function(theDate) {
		var day = theDate.getDate();
		var month = theDate.getMonth() + 1;
		var year = theDate.getFullYear();

		if(day < 10)   day   = '0' + day;
		if(month < 10) month = '0' + month;

		return day + '/' + month + '/' + year;
	},
	_computeAverage: function(weight1, weight2, days) {
		var step = weight2 - weight1;
		return (step/days);
	}
	
});
