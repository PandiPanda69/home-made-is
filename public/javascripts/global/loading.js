App.Loading = Backbone.View.extend({

	initialize: function() {
		this.$el = $('#loading-dialog');
	},

	render: function() {
		this.$el.modal({
			show:     true,
			backdrop: false,
			keyboard: false
		});
	},
	dispose: function() {
		this.$el.modal('hide');
	}
});

