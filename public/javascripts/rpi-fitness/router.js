App.Router = Backbone.Router.extend({

	homeView:    null,

	initialize: function() {
	},

	routes: {
		"":     "home",
		"home": "home",
		"weight": "weight",
		"food": "food"
	},

	home: function() {
		if(this.homeView == null) {
			this.homeView = new App.Views.Home;
		}

		App.Menu.activateButton($("#menu-home"));
		this.homeView.render();
	},
	weight: function() {
		App.Menu.activateButton($("#menu-weight"));
	},
	food: function() {
		App.Menu.activateButton($("#menu-food"));
	}
})
