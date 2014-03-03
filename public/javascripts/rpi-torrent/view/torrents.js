App.Views.Torrents = Backbone.View.extend({

	template: _.template($("#torrents-template").html()),

	events: {
		"click .torrent-remove": "_remove",
		"click #torrent-filter": "_filter"
	},
	initialize: function() {
		this.$el = $("#main-container");

		App.Loading.render();
		if(App.Models.Torrent.initialized == false) {
			$.when(App.Models.Torrent.fetch())
			.fail( $.proxy(function(xhr, error, errorMsg) { 
				this._onError('Une erreur est survenue lors du chargement des données : ' + errorMsg);
			}, this))
			.done( $.proxy(function() { 
				this.render();
				App.Loading.dispose();
			}, this));
		}
	},
	render: function() {
		this.$el.html(this.template({torrents: App.Models.Torrent.toJSON()}));

		$('.grade').each($.proxy(function(id, el) {
			var $el = $(el);
			var torrentId = $el.attr('torrent-id');
			var grade = App.Models.Torrent.get(torrentId).get('grade');

			$el.grade(grade, $.proxy(this._onChangeGrade, this), torrentId);
		}, this));
	},
	_onError: function(msg) {
		App.ErrorPopup.setMessage(msg);
		App.ErrorPopup.render();

		App.Loading.dispose();
	},
	_remove: function(evt) {
		var $target = $(evt.currentTarget);
		var id = $target.attr('torrent-id');

		$.when(App.Models.Torrent.get(id).destroy())
		.fail($.proxy(function(xhr, error, errorMsg) {
			this._onError('Une erreur est survenue lors de la suppression du torrent #' + id + ' : ' + errorMsg);
		}, this))
		.done($.proxy(function() {
			this.render();
		}, this));
	},
	_filter: function(evt) {
		App.Loading.render();

		var status = $('#status').val();
		var timeUnit = $('#timeUnit').val();
		var timeValue = $('#timeValue').val();

		$.ajax({
			url: globals.rootUrl + '/torrents/filter',
			data: JSON.stringify({
				status: status,
				timeValue: timeValue,
				timeUnit: timeUnit
			}),
			contentType: 'application/json',
			type: 'POST'
		})
		.fail($.proxy(function(xhr, error, errorMsg) {
			this._onError('Une erreur est survenue lors du filtrage des torrents : ' + errorMsg);
		}, this))
		.done($.proxy(function(data) {
			this.$el.html(this.template({torrents: data}));

			$('#status').val(status);
			$('#timeUnit').val(timeUnit);
			$('#timeValue').val(timeValue);

			App.Loading.dispose();
		}, this));
	},
	_onChangeGrade: function(newGrade, torrentId) {
		$.ajax({
			type: 'PUT',
			url: globals.rootUrl + '/torrents/' + torrentId,
			async: false,
			contentType: 'application/json',
			data: JSON.stringify({grade: newGrade}),
			error: $.proxy(function(model, response, options) {
				this._onError('Erreur lors de la mise à jour de la note : ' + response.statusText);
			}, this)
		});
	}
});
