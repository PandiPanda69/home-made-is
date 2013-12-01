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
	displayHome: function(e) {
		App.Router.navigate("", {trigger: true});
	},
	displayTorrents: function(e) {
		App.Router.navigate("torrents", {trigger: true});
	},
	displayQueue: function(e) {
		App.Router.navigate("queue", {trigger: true});
	},
	displayParams: function(e) {
		App.Router.navigate("params", {trigger: true});
	}
});

