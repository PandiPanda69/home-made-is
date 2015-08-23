App.Models.Phones = Backbone.Collection.extend({

    id: null,
    model: Backbone.Model.extend({
        defaults: {
            phone: []
        },
        urlRoot: function() {
            return globals.rootUrl + '/phones';
        }
    }),
    url: function() {
        return globals.rootUrl + '/phones';
    },
    comparator: 'order'
});
