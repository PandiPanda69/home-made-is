App.Models.CalendarEvent = Backbone.Collection.extend({

	id: null,
	model: Backbone.Model.extend({
		defaults: {
			id: null,
			timestamp: null,
			weight: null
		},
		urlRoot: function() {
			return globals.rootUrl + '/calendar';
		}
	}),
	url: function() {
		return globals.rootUrl + '/calendar/' + year + '/' + month;
	},

	initialized: false,
	month: null,
	year:  null,

	comparator: 'order'
});

