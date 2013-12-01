App.Popup = Backbone.View.extend({

	initialize: function() {
	      	this.$dialog = $('#popup-dialog');
	},
	render: function() {
		this.$dialog.modal({
			show:     true,
			backdrop: false,
			keyboard: false
		});
	},
	dispose: function() {
		this.$dialog.modal('hide');
	}
});
