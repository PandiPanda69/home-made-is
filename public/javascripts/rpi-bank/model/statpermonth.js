App.Models.StatPerMonth = Backbone.Collection.extend({

	model: Backbone.Model.extend({
		defaults: {
			accountId: null,
			months: []
		},
		urlRoot: function() {
			console.log('No');
		}
	}),
	comparator: 'order',
	compte: null,

	url: function() {
		return globals.rootUrl + '/accounts/' + this.compte + '/stats/month';
	}
});

