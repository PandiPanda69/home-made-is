App.Views.ContactMessages = Backbone.View.extend({

    template: _.template($("#contact-messages-template").html()),

    initialize: function() {
        this.$el = $("#main-container");
    },

    events: {
        "click #view-contact": "_onViewContact"
    },

    render: function(id) {
        App.Loading.render();
        
        this._getContactById(id, $.proxy(function(contact) {
            this._getMessages(id, $.proxy(function() {
                this.$el.html(this.template({
                    contact: contact.toJSON(),
                    messages: App.Models.Messages.toJSON()
                }));

                this._contactId = id;
                App.Loading.dispose();
            }, this));
        }, this));
    },

    _getContactById: function(id, success) {

        if(App.Models.Contacts.length > 0) {
            success(App.Models.Contacts.get(id));
            return;
        }

        App.Models.Contacts.fetch()
        .done(function() {
            success(App.Models.Contacts.get(id));
        })
        .error($.proxy(function() {
            this._onError('Impossible de charger la liste des contacts.');
        }, this));
    },

    _getMessages: function(id, success) {

        App.Models.Messages.contact_id = id;
        App.Models.Messages.fetch()
        .done(success)
        .error($.proxy(function() {
            this._onError('Impossible de charger la liste des messages pour ce contact.');
        }, this));
    },

    _onError: function(msg) {
        App.ErrorPopup.setMessage(msg);
        App.ErrorPopup.render();

        App.Loading.dispose();
    },

    _onViewContact: function() {
        App.Router.navigate('contacts/' + this._contactId, {trigger: true});
    }
});
