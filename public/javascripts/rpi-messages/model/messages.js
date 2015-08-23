App.Models.Messages = Backbone.Collection.extend({

    id: null,
    contact_id: null,
    model: Backbone.Model.extend({
        defaults: {
            phone: {},
            direction: null,
            status: null,
            body: null,
            date: null
        },
        urlRoot: function() {
            return globals.rootUrl + '/contacts/' + this.contact_id + '/messages';
        }
    }),
    url: function() {
        return globals.rootUrl + '/contacts/' + this.contact_id + '/messages';
    }
});
