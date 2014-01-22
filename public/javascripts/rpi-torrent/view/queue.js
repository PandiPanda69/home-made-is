App.Views.Queue = Backbone.View.extend({

	//template: _.template($("#parameters-template").html()),

	events: {
	},
	initialize: function() {
		this.$el = $("#main-container");

		alert('Not available yet.');
	},
	render: function() {

		//this.$el.html(this.template({params: this._data}));
	},
        _onError: function(msg) {
                App.ErrorPopup.setMessage(msg);
                App.ErrorPopup.render();

                App.Loading.dispose();
        }
});
