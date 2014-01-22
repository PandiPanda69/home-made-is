App.Router = Backbone.Router.extend({

	homeView:    null,
	torrentView: null,
	queueView:   null,
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
		if(this.queueView == null) {
			this.queueView = new App.Views.Queue;
		}

		App.Menu.activateButton($("#menu-queue"));
		this.queueView.render();
	},
	params: function() {
		if(this.paramView == null) {
			this.paramView = new App.Views.Parameters;
		}

		App.Menu.activateButton($("#menu-params"));
		this.paramView.render();
	}
})
