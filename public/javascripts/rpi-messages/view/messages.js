App.Views.Messages = Backbone.View.extend({

    template: _.template($("#messages-template").html()),

    initialize: function() {
        this.$el = $("#main-container");
    },

    events: {
        "click .display-messages": "_displayMessages"
    },

    render: function() {
        App.Loading.render();

        App.Models.ContactMessages.fetch()
        .done($.proxy(function() {
            this.$el.html(this.template({
                gridData: App.Models.ContactMessages.toJSON()
            }));
            App.Loading.dispose();
        }, this))
        .error($.proxy(function() {
            this._onError('Impossible de récupérer la présentation des messages par contact.');
        }, this));
    },

    _onError: function(msg) {
        App.ErrorPopup.setMessage(msg);
        App.ErrorPopup.render();

        App.Loading.dispose();
    },

    _displayMessages: function(e) {
        var id = $(e.target).attr('contact-id');
        App.Router.navigate("contacts/" + id + "/messages", {trigger: true});
        return false;
    }
    
});
