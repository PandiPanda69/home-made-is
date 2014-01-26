App.Models.QueueElement = Backbone.Collection.extend({

	id: null,
	model: Backbone.Model.extend({
		defaults: {
			name: null,
			size: null,
			creationDate: null,
			status: null
		},
		urlRoot: function() {
			return globals.rootUrl + '/queue';
		}
	}),
	url: function() {
		return globals.rootUrl + '/queue';
	},
	initialized: false,
	comparator: 'order'
});

