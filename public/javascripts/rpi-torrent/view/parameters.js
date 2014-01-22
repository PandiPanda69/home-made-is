App.Views.Parameters = Backbone.View.extend({

	template: _.template($("#parameters-template").html()),
	_data:    null,

	events: {
	},
	initialize: function() {
		this.$el = $("#main-container");

		$.ajax({
                        url: globals.rootUrl + '/params',
                        type: 'GET',
			async: false
                })
		.done($.proxy(function(data) {
			this._data = data;
			this.render();
		}, this))
		.fail($.proxy(function(xhr, error, errorMsg) {
			this._onError('Une erreur est survenue lors du chargement des données: ' + errorMsg);
		}, this));

	},
	render: function() {

		if(this._data == null) {
			return;
		}

		this.$el.html(this.template({params: this._data}));
	},
        _onError: function(msg) {
                App.ErrorPopup.setMessage(msg);
                App.ErrorPopup.render();

                App.Loading.dispose();
        }
});
