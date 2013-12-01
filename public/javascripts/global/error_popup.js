App.ErrorPopup = Backbone.View.extend({

	initialize: function() {
	      	this.$dialog = $('#popup-dialog');
		this.errorMsg = '';
	},
	render: function() {
		var currentDialog = this.$dialog.clone();

		currentDialog.modal({
			show:     true,
			backdrop: false,
			keyboard: false
		});

		var $icon = $("<i>", {
			'class': 'icon-remove-sign',
			html: ' &nbsp; '
		});
		var $msg = $("<span>", {
			'class': 'error',
			html: ' ' + this.errorMsg
		});

		var $finalMsg = $('<p>', {'class': 'alert alert-error pull-center'}).append($icon).append($msg);

		var $btn = $('<button>', {'class': 'btn btn-info pull-right', html: 'Ok'}).click(this._onClickOk);

		currentDialog.find('.modal-body').append($finalMsg).append($btn);

		// Reset error message
		this.errorMsg = '';
	},
	setMessage: function(errorMsg) {
		this.errorMsg = errorMsg;
	},
	_onClickOk: function(evt) {
		$(evt.target).closest('.modal').modal('hide');
	}
});
