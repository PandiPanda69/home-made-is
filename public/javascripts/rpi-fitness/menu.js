App.Menu = Backbone.View.extend({

	events: {
		"click #menu-home":     "displayHome",
		"click #menu-calendar": "displayCalendar",
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
	displayCalendar: function(e) {
		App.Router.navigate("calendar", {trigger: true});
	}
});

