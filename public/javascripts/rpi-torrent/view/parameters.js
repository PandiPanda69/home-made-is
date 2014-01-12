App.Views.Parameters = Backbone.View.extend({

	template: _.template($("#parameters-template").html()),

	events: {
		// "click #torrent-filter": "_filter"
	},
	initialize: function() {
		this.$el = $("#main-container");
	},

	render: function() {

		this.$el.html(this.template({}));
	}
});
