App.Models.Torrent = Backbone.Collection.extend({

	id: null,
	model: Backbone.Model.extend({
		defaults: {
			name: null,
			detectionDate: null,
			lastUpdateDate: null,
			downloadedBytes: 0,
			uploadedBytes: 0,
			status: null
		},
		urlRoot: function() {
			return globals.rootUrl + '/torrents';
		}
	}),
	url: function() {
		return globals.rootUrl + '/torrents';
	},
	initialized: false,
	comparator: 'order'
});

