App.Models.Account = Backbone.Collection.extend({

	model: Backbone.Model.extend({

                urlRoot: globals.rootUrl + '/accounts',
                initialized: false,
                id: null,
                defaults: {
			nom: null,
			type: null,
			lastUpdate: null,
			solde: 0.00
                }
        }),

	url: globals.rootUrl + '/accounts',
	initialized: false,
	comparator: 'order'
});
