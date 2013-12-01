var App = {
	Views: {},
	Models: {},

	init: function() {
		// this.Models.Torrent = new App.Models.Torrent;

		this.Popup = new App.Popup;
		this.ErrorPopup = new App.ErrorPopup;
		this.Loading = new App.Loading;
		this.Menu = new App.Menu;
		this.Router = new App.Router;

		Backbone.history.start({root: globals.rootUrl});
	}
}
