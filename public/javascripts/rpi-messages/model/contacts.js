App.Models.Contacts = Backbone.Collection.extend({

    id: null,
    model: Backbone.Model.extend({
        defaults: {
            name: null,
            phones: []
        },
        urlRoot: function() {
            return globals.rootUrl + '/contacts';
        }
    }),
    url: function() {
        return globals.rootUrl + '/contacts';
    },
    comparator: 'order'
});
