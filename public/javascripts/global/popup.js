App.Popup = Backbone.View.extend({

	initialize: function() {
        this.$dialog = $('#popup-dialog').clone();

		if(this._init != null) {
			this._init(this.$dialog);
		}
	},
	render: function() {
		if(this._preRender != null) {
			this._preRender();
		}

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
