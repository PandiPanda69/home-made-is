App.Models.AccountType = Backbone.Collection.extend({

	model: Backbone.Model.extend({
                id: null,
                defaults: {
                        libelle: null,
                        type: null
                },
                urlRoot: function() {
                        return globals.rootUrl + '/accounts/type';
                }
        }),

	comparator: 'order',
	initialized: false,

	url: function() {
		return globals.rootUrl + '/accounts/type';
	}
});

