App.Menu = Backbone.View.extend({

	events: {
		"click #menu-home":     "displayHome",
		"click #menu-accounts": "displayAccounts",
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
	displayAccounts: function(e) {
		App.Router.navigate("accounts", {trigger: true});
	},
	displayParams: function(e) {
		App.Router.navigate("params", {trigger: true});
	}
});

