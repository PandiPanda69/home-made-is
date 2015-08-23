App.Menu = Backbone.View.extend({

	events: {
		"click #menu-home":     "displayHome",
		"click #menu-torrents": "displayTorrents",
		"click #menu-queue":	"displayQueue",
		"click #menu-params":   "displayParams"
	},
	initialize: function() {
		this.$el = $("#menu");
	},
	activateButton: function($btn) {
		$("#menu li").removeClass("active");
		$btn.parent().addClass("active");
	},
	displayHome: function() {
		App.Router.navigate("", {trigger: true});
	},
	displayTorrents: function() {
		App.Router.navigate("torrents", {trigger: true});
	},
	displayQueue: function() {
		App.Router.navigate("queue", {trigger: true});
	},
	displayParams: function() {
		App.Router.navigate("params", {trigger: true});
	}
});

