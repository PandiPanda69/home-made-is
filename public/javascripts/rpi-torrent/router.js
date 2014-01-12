App.Router = Backbone.Router.extend({

	homeView:    null,
	torrentView: null,
	paramView:   null,

	initialize: function() {
	},

	routes: {
		"":     "home",
		"home": "home",
		"torrents": "torrents",
		"queue": "queue",
		"params": "params"
	},

	home: function() {
		if(this.homeView == null) {
			this.homeView = new App.Views.Home;
		}

		App.Menu.activateButton($("#menu-home"));
		this.homeView.render();
	},
	torrents: function() {
		if(this.torrentView == null) {
			this.torrentView = new App.Views.Torrents;
		}

		App.Menu.activateButton($("#menu-torrents"));
		this.torrentView.render();
	},
	queue: function() {
		App.Menu.activateButton($("#menu-queue"));
	},
	params: function() {
		if(this.paramView == null) {
			this.paramView = new App.Views.Parameters;
		}

		App.Menu.activateButton($("#menu-params"));
		this.paramView.render();
	}
})
