App.Views.Queue = Backbone.View.extend({

	template: _.template($("#queue-template").html()),

	events: {
		"click #torrents-queue td":	"_onRowClick"
	},
	initialize: function() {
		this.$el = $("#main-container");
	},
	render: function() {

		$.when(App.Models.QueueElement.fetch())
		.done($.proxy(function() {
			this.$el.html(this.template({queue: App.Models.QueueElement.toJSON()}));
		}, this))
		.fail($.proxy(function(xhr, error, errorMsg) {
			this._onError('Une erreur est survenue lors du chargement des données: ' + errorMsg);
		}, this));
	},
        _onError: function(msg) {
                App.ErrorPopup.setMessage(msg);
                App.ErrorPopup.render();

                App.Loading.dispose();
        },
	_onRowClick: function(evt) {
		alert('todo');
	}
});
