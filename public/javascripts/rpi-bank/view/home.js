App.Views.Home = Backbone.View.extend({

	main: $("#main-container"),
	statsPerMonths: null,

	_initialized: false,
	_selectedAccountId: null,

	initialize: function() {
		this.homeTemplate =  _.template($('#home-template').html());

		this.statsPerMonths = new App.Models.StatPerMonth;
	},
	events: {
	},
	_prepareData: function() {

		App.Models.Account.fetch({
			success: $.proxy(function() {
				this._selectedAccountId = App.Models.Account.at(0).id;
				this.statsPerMonths.compte = this._selectedAccountId;

				this.statsPerMonths.fetch({
					success: $.proxy(function() {
						App.Models.Month.compte = this._selectedAccountId
						App.Models.Month.fetch({
							success: $.proxy(function() {
								this._initialized = true;
								this.render();
							}, this),
							error: this._onError
						});
					}, this),
					error: this._onError
				});
			}, this),
			error: this._onError
		});

		return false;
	},
	_onError: function() {
		alert('Impossible de charger les informations requises.');
		LoadingDialog.dispose();

		return true;
	},
	_getXLabels: function() {
		var labels = [];
		var lastMonth = 0;
		var lastYear = 0;

		App.Models.Month.each(function(element) {
			var month = MonthNames.months[element.get('mois')];
			var year  = element.get('annee');

			labels.push(month[0] + ' ' + year);

			lastMonth = element.get('mois');
			lastYear  = element.get('annee');
		});

		// Push at the end month N+1 (for trend)
		if(lastMonth == 11) {
			lastYear++;
			lastMonth = 0;
		}
		else {
			lastMonth++;
		}

		labels.push(MonthNames.months[lastMonth][0] + ' ' + lastYear);

		return _.last(labels, 13);
	}, 
	_getSeries: function() {
		var data = [];

		this.statsPerMonths.each($.proxy(function(element) {

			if(element.get('accountId') == this._selectedAccountId ) {
				_.each(element.get('elements'), function(current) {
					data.push(current.val);
				});
				return false;
			}
		}, this));

		return _.last(data, 12);
	},
       _getTrend: function() {

		var data = [];
		var serie = this._getSeries();

		var avg = 0;
		_.each(serie, function(current, index) {

			if(index > 0) {
				avg += (current - serie[index-1]) / 2;
			}
		});

		avg /= serie.length;

		data.push(serie[0]);
		for(var i = 0; i < serie.length; i++) {
			data.push(data[i] + avg);
		}

		return data;

	},
	render: function() {

		App.Menu.activateButton($('#menu-home'));

		if(!this._initialized) {
			this._prepareData();
			return;
		}

		this.main.html(this.homeTemplate({accounts: App.Models.Account.toJSON()}));
		// Register event
		$('.bank-graph-selection').click($.proxy(this._onAccountClick, this));

		$('#bank-abstract-chart').highcharts({
			title: {
				text: 'Historique sur 1 an',
				x: -20
			},
			subtitle: {
				text: App.Models.Account.get(this._selectedAccountId).get('nom'),
				x: -20
			},
			xAxis: {
				categories: this._getXLabels()
			},
			yAxis: {
				title: {
					text: 'Solde (€)'
				},
				plotLines: [{
					value: 0,
					width: 1,
					color: '#808080'
				}]
			},
			series: [{
				name: 'Solde',
				data: this._getSeries()
			},
			{
				name: 'Tendance',
				data: this._getTrend()
			}]
		});
	},
	_onAccountClick: function(evt) {
		this._selectedAccountId = $(evt.currentTarget).attr('account');

		this.statsPerMonths.compte = this._selectedAccountId;
		this.statsPerMonths.fetch({
			success: $.proxy(function() {
				App.Models.Month.compte = this._selectedAccountId;
				App.Models.Month.fetch({
					success: $.proxy(function() {
						this._initialized = true;
						this.render();
					}, this),
					error: this._onError
				});
			}, this),
			error: this._onError
		});
	}
});


