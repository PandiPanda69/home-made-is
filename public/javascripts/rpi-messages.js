var App = {
	Views: {},
	Models: {},

	init: function() {

		this.Popup = new App.Popup;
		this.ErrorPopup = new App.ErrorPopup;
		this.Loading = new App.Loading;
		this.Menu = new App.Menu;
		this.Router = new App.Router;

        this.Models.Contacts = new App.Models.Contacts;
        this.Models.Phones = new App.Models.Phones;
        this.Models.ContactMessages = new App.Models.ContactMessages;
        this.Models.Messages = new App.Models.Messages;

		Backbone.history.start({root: globals.rootUrl});
	}
}
