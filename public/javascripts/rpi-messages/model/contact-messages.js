App.Models.ContactMessages = Backbone.Collection.extend({

    id: null,
    model: Backbone.Model.extend({
        defaults: {
            name: null,
            count: 0,
            lastReception: null
        },
        urlRoot: function() {
            return globals.rootUrl + '/messages';
        }
    }),
    url: function() {
        return globals.rootUrl + '/messages';
    }
});
