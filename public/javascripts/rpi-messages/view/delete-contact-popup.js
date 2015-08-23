App.Views.DeleteContactPopup = App.Popup.extend({

    template: _.template($('#delete-contact-popup-template').html()),

    _init: function($dialog) {
        this.$el = $dialog;
    },

    events: {
        'click #ok-btn': '_onValidate',
        'click #cancel-btn': 'dispose'
    },

    setOwner: function(owner) {
        this._parent = owner;
    },

    _preRender: function() {

        App.Loading.render();

        this.$el.html(this.template({
            contacts: App.Models.Contacts.toJSON(),
            current_id: this._parent.currentContact.id
        }));

        App.Loading.dispose();
    },

    _onValidate: function() {
        App.Loading.render();

        var id = $('#target-contact').val();
        var target = App.Models.Contacts.get(id);

        target.save({
            phones: target.get('phones').concat(this._parent.currentContact.get('phones'))
        })
        .done($.proxy(function() {

            this._parent.currentContact.destroy()
            .done($.proxy(function() {
                this.dispose();
                // Navigate does not trigger... Why? :-(
                App.Router.navigate('contacts', {trigger:true});
                App.Router.contacts();
            }, this))
            .error($.proxy(function() {
                this._parent._onError('Une erreur est survenue lors de la suppression du contact.');
            }, this));

        }, this))
        .error($.proxy(function() {
            this._parent._onError('Une erreur est survenue lors du transfert des numéros.');
        }, this));
    }
});
