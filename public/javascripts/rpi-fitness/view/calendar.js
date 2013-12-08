App.Views.Calendar = Backbone.View.extend({

	template: _.template($("#calendar-template").html()),
	_currentMonth: null,
	_currentYear:  null,
	_events:       null,
	_popup:        null,
	_collection:   null,
	_currentEvent: null,

	initialize: function() {
		this.$el = $("#main-container");
	},
	events: {
		"click #prev-month":	"previousMonth",
		"click #next-month":	"nextMonth",
		"click #calendar-table .date": "_onDateClick"
	},
	render: function() {
	
		if(this._currentMonth == null || this._currentYear == null) {
			this._setCurrentDate();
		}

		this._loadEvents();
	},
	previousMonth: function() {
		if(this._currentMonth == 0) {
			this._currentMonth = 11;
			this._currentYear--;
		}
		else {
			this._currentMonth--;
		}

		this.render();
	},
	nextMonth: function() {
		if(this._currentMonth == 11) {
			this._currentMonth = 0;
			this._currentYear++;
		}
		else {
			this._currentMonth++;
		}

		this.render();
	},
	_setCurrentDate: function() {
		var currentDate = new Date();
		this._currentMonth = currentDate.getMonth();
		this._currentYear  = currentDate.getFullYear();
	},
	_loadEvents: function(onSuccess) {
                
		var url = '/calendar/' + this._currentYear + '/' + this._currentMonth;

                $.get(globals.rootUrl + url, $.proxy(function(data) {

			this._computeDateFromTimestamp(data);

			this.$el.html(this.template({month: this._currentMonth, year: this._currentYear, events: data}));

			this._events = data;
                        App.Loading.dispose();
                }, this))

                .fail($.proxy(function(xhr, error, errorMsg) {
			this._onError('Erreur lors du chargement de la page : ' + errorMsg);
                }, this)); 
	},
	_computeDateFromTimestamp: function(data) {
		$.each(data, function(id, element) {
			element.date = new Date(element.timestamp);
		});
	},
	_onDateClick: function(evt) {
		var $cell  = $(evt.currentTarget).find('span');
		var theDay = $cell.html();

		var hasWeight = $cell.hasClass('badge');

		if(!hasWeight) {
			this._showPopup(theDay, null);
			return;
		}

		var calendarEvent = null;
		$.each(this._events, function(id, element) {
			if(element.date.getDate() == theDay) {
				calendarEvent = element;
				return false;
			}
		});

		this._showPopup(theDay, calendarEvent);
	},
	_showPopup: function(theDay, calendarEvent) {

		if(this._popup == null) {
			this._popup = new App.Views.CalendarPopup;
		}

		this._currentEvent = calendarEvent;

		this._popup.setDate(this._currentYear, this._currentMonth, theDay);
		this._popup.setWeight(calendarEvent == null ? null : calendarEvent.weight);
		this._popup.onClose = $.proxy(this._onPopupClose, this);

		this._popup.render();
	},
	_onPopupClose: function(date, weight) {

		if(weight != null && weight.length > 0) {
			weight = parseFloat(weight.replace(',', '.')).toFixed(1);
		}
		else {
			weight = null;
		}

		// No changes
		if(((weight == null || weight.length == 0) && (this._currentEvent == null || this._currentEvent.weight == null)) 
			|| (this._currentEvent != null && this._currentEvent.weight == weight)) {
			return;
		}

		App.Loading.render();

		if(this._collection == null) {
			this._collection = new App.Models.CalendarEvent;
		}

		if(this._currentEvent == null) {
			this._collection.create({
				timestamp: date.getTime(),
				weight:    weight
			}, {
				success: $.proxy(this._onSaveSucceed, this),
				error:   $.proxy(function(model, xhr) {
					this._onError('Erreur lors de la sauvegarde: ' + xhr.statusText);
				}, this)
			});
		}
		else {
			var theEvent = new this._collection.model;
			theEvent.save({
				id: this._currentEvent.id,
				timestamp: this._currentEvent.timestamp,
				weight: weight
			}, {
				success: $.proxy(this._onSaveSucceed, this),
				error:   $.proxy(function(model, xhr) {
					this._onError('Erreur lors de la sauvegarde: ' + xhr.statusText);
				}, this)
			})
		}
	},
	_onSaveSucceed: function() {
		this.render();

		App.Loading.dispose();
	},
	_onError: function(msg) {
		App.ErrorPopup.setMessage(msg);
		App.ErrorPopup.render();

		App.Loading.dispose();
	}
	/*
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
	}*/
});
