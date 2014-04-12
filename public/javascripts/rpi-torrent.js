var App = {
	Views: {},
	Models: {},

	init: function() {
		this.Models.QueueElement = new App.Models.QueueElement;

		this.Popup = new App.Popup;
		this.ErrorPopup = new App.ErrorPopup;
		this.Loading = new App.Loading;
		this.Menu = new App.Menu;
		this.Router = new App.Router;

		Backbone.history.start({root: globals.rootUrl});
	}
}
