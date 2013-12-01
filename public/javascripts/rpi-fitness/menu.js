App.Menu = Backbone.View.extend({

	events: {
		"click #menu-home":     "displayHome",
		"click #menu-weight": "displayWeight",
		"click #menu-food":	"displayFood",
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
	displayWeight: function(e) {
		App.Router.navigate("weight", {trigger: true});
	},
	displayFood: function(e) {
		App.Router.navigate("food", {trigger: true});
	}
});

