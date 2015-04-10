App.Views.Torrents = Backbone.View.extend({

	template: _.template($("#torrents-template").html()),
	_torrentData: null,

	events: {
		"click .torrent-remove": "_remove",
		"click #torrent-filter": "_filter",
		"click #torrent-search": "_search",
		"click .torrent-name":   "_browseFiles"
	},
	initialize: function() {
		this.$el = $("#main-container");
	},
	render: function(data) {
                App.Loading.render();

                $.ajax({
                        url: globals.rootUrl + '/torrents',
                        contentType: 'application/json',
                        type: 'GET'
                })
                .fail($.proxy(function(xhr, error, errorMsg) {
                        this._onError('Une erreur est survenue lors de la récupération des torrents : ' + errorMsg);
                }, this))
                .done($.proxy(function(data) {
                        this._wrapData(data);
			this.$el.html(this.template({torrents: data}));
			this._initGrades();
                        App.Loading.dispose();
                }, this));
	},
	_initGrades: function() {
		$('.grade').each($.proxy(function(id, el) {
			var $el = $(el);
			var torrentId = $el.attr('torrent-id');
			var grade = this._torrentData[torrentId].grade;

			$el.grade(grade, $.proxy(this._onChangeGrade, this), torrentId);
		}, this));
	},
	_wrapData: function(data) {
		this._torrentData = [];

		$.each(data, $.proxy(function(id, el) {
			this._torrentData[el.id] = el;
		}, this));
	},
	_refreshCounter: function() {
		var cpt = 0;

		$.each(this._torrentData, function(id, el) {
			if(el !== null && typeof el !== "undefined") {
				cpt++;
			}
		});

		$('#torrent-count').html(cpt);
	},
	_onError: function(msg) {
		App.ErrorPopup.setMessage(msg);
		App.ErrorPopup.render();

		App.Loading.dispose();
	},
	_refreshListing: function(data) {
		this._wrapData(data);

		this.$el.html(this.template({torrents: data}));

		this._initGrades();
		this._refreshCounter();
	},
	_remove: function(evt) {
		var $target = $(evt.currentTarget);
		var id = $target.attr('torrent-id');

		$.ajax({
                        url: globals.rootUrl + '/torrents/' + id,
                        type: 'DELETE'
                })
                .fail($.proxy(function(xhr, error, errorMsg) {
                        this._onError('Une erreur est survenue lors de la suppression du torrent: ' + errorMsg);
                }, this))
                .done($.proxy(function() {
			// Remove element from array
			this._torrentData.splice(id, 1);

			// Then remove dom node and refresh counter
			$target.closest('tr').remove();

			this._refreshCounter();
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
			this._refreshListing(data);

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
	},
	_search: function() {
		App.Loading.render();

		var value = $('#torrent-search-value').val();
		var status = $('#status').val();

		$.ajax({
			type: 'GET',
			url:  globals.rootUrl + '/torrents/find/' + value,
			data: {status: status},
			cache: false
		})
		.fail($.proxy(function(xhr) {
			var response = JSON.parse(xhr.responseText);
			this._onError('Erreur lors de la recherche : ' + response.message);
		}, this))
		.done($.proxy(function(data) {
                        this._refreshListing(data);

                        App.Loading.dispose();
		}, this));

		return false;
	},
	_browseFiles: function(evt) {
		App.Loading.render();

		var id = $(evt.target).attr('torrent-id');
		App.Router.navigate("torrents/" + id + "/files", {trigger: true});

		return false;
	}
});
