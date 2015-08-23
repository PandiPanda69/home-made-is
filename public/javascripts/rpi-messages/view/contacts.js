App.Views.Contacts = Backbone.View.extend({

    template: _.template($("#contacts-template").html()),

    initialize: function() {
        this.$el = $("#main-container");
    },

    events: {
        "click .view-contact": "_onClickViewContact",
        "click .edit-contact": "_onClickEditContact"
    },

    render: function() {
        App.Loading.render();

        this._getContacts($.proxy(function(contacts) {
            this.$el.html(this.template({
                contacts: contacts.toJSON()
            }));
            App.Loading.dispose();
        }, this))
    },

    _onError: function(msg) {
        App.ErrorPopup.setMessage(msg);
        App.ErrorPopup.render();

        App.Loading.dispose();
    },

    _getContacts: function(callback) {
        if(App.Models.Contacts.length > 0) {
            callback(App.Models.Contacts);
            return;
        }

        $.when(App.Models.Contacts.fetch())
        .done(function() {
            callback(App.Models.Contacts);
        })
        .fail($.proxy(function() {
            this._onError('Impossible de charger les contacts.');
        }, this));
    },

    _onClickViewContact: function(e) {
       var id = $(e.target).parents('tr').attr('contact-id');
       App.Router.navigate("contacts/" + id, {trigger: true});
       return false;
    },

    _onClickEditContact: function(e) {
       var id = $(e.target).parents('tr').attr('contact-id');
       App.Router.navigate("contacts/" + id + "/edit", {trigger: true});
       return false;
    }
});
