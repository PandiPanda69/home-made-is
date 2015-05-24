App.Models.Repetition = Backbone.Collection.extend({

	model: Backbone.Model.extend({
                urlRoot: function() {
                   return globals.rootUrl + '/repetitions/' + App.Models.Repetition.compte;
                },
                id: null,
                defaults: {
                   nom: null,
                   montant: .0,
                   active: true,
                   compte: null,
                   type: null
                }
        }),

        compte: null,
	url: function() {
                return globals.rootUrl + '/repetitions/' + this.compte;
        },
	comparator: 'order'
});

App.Models.Repetition = new App.Models.Repetition;
