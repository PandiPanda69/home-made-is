App.Router = Backbone.Router.extend({

	homeView:     null,
	calendarView: null,

	initialize: function() {
	},

	routes: {
		"":     "home",
		"home": "home",
		"calendar": "calendar",
	},

	home: function() {
		if(this.homeView == null) {
			this.homeView = new App.Views.Home;
		}

		App.Menu.activateButton($("#menu-home"));
		this.homeView.render();
	},
	calendar: function() {
		if(this.calendarView == null) {
			this.calendarView = new App.Views.Calendar;
		}

		App.Menu.activateButton($("#menu-calendar"));
		this.calendarView.render();
	}
})
