App.Models.Operation = Backbone.Collection.extend({

	model: Backbone.Model.extend({
		initialized: false,
    	id: null,
    	defaults: function() {
    		return {
            	nom: null,
				type: null,
				montant: 0.00,
				nomComplet: null
			};
    	},
		urlRoot: function() {
			return globals.rootUrl + '/accounts/' + App.Models.Operation.compte + '/months/' + App.Models.Operation.month + '/operations';
		}
	}),
	initialized: false,
	comparator: 'order',
	compte: null,
	month: null,

	url: function() {
		return globals.rootUrl + '/accounts/' + this.compte + '/months/' + this.month + '/operations';
	}
});