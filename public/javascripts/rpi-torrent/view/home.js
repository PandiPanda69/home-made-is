App.Views.Home = Backbone.View.extend({

	template: _.template($("#home-template").html()),

	initialize: function() {
		this.$el = $("#main-container");
	},

	render: function() {
	
		App.Loading.render();

		$.get(globals.rootUrl + "/stats/home", $.proxy(function(data) {
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

		var splittedDate = data.graph.pointStart.split('/');
		var startingDate = new Date(splittedDate[2], splittedDate[1] - 1, splittedDate[0]);

		$('#torrents-abstract-chart').highcharts({
			chart: {
				zoomType: 'x',
				spacingRight: 20,
				type: 'spline'
			},
			title: {
				text: 'Activité des torrents sur les 3 derniers mois'
			},
			xAxis: {
				type: 'datetime',
				maxZoom: 7 * 24 * 60 * 60 * 1000,
				title: null
			},
			yAxis: {
				title: null
			},
			plotOptions: {
				spline: {
					marker: {
						enabled: false
					}
				}
			},
			tooltip: {
                                pointFormat: '{series.name}: <b>{point.y} bytes</b>'
			},
			series: [{
				name: 'Downloaded',
				pointInterval: data.graph.pointInterval,
				pointStart: startingDate.getTime(),
				data: [
				]
			}, {
				name: 'Uploaded',
				pointInterval: data.graph.pointInterval,
				pointStart: startingDate.getTime(),
				data: data.graph.elements
			}]
		});
	}
});
