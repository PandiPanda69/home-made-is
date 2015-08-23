App.Router = Backbone.Router.extend({

    _homeView:  null,
    _contactsView: null,
    _unknownContactsView: null,
    _messagesView: null,
    _editContactView: null,
    _contactMessagesView: null,

    initialize: function() {
    },

    routes: {
        "": "home",
        "home": "home",
        "contacts": "contacts",
        "contacts/:id": "viewContact",
        "contacts/:id/edit": "editContact",
        "contacts/:id/messages": "contactMessages",
        "contacts/inconnu": "unknownContacts",
        "messages": "messages"
    },

    home: function() {
        if(this._homeView === null) {
            this._homeView = new App.Views.Home;
        }

        App.Menu.activateButton($("#menu-home"));
        this._homeView.render();
    },

    contacts: function() {
        if(this._contactsView === null) {
            this._contactsView = new App.Views.Contacts;
        }

        App.Menu.activateButton($("#menu-contacts"));
        this._contactsView.render();
    },

    unknownContacts: function() {
        if(this._unknownContactsView === null) {
            this._unknownContactsView = new App.Views.UnknownContacts;
        }

        App.Menu.activateButton($("#menu-contacts"));
        this._unknownContactsView.render();
    },

    messages: function() {
        if(this._messagesView === null) {
            this._messagesView = new App.Views.Messages;
        }

        App.Menu.activateButton($("#menu-messages"));
        this._messagesView.render();
    },

    viewContact: function(id) {
       if(this._editContactView === null) {
           this._editContactView = new App.Views.EditContact;
       }

       App.Menu.activateButton($("#menu-contacts"));
       this._editContactView.render(id, 'VIEW');
    },

    editContact: function(id) {
       if(this._editContactView === null) {
           this._editContactView = new App.Views.EditContact;
       }

       App.Menu.activateButton($("#menu-contacts"));
       this._editContactView.render(id, 'EDIT');
    },

    contactMessages: function(id) {
       if(this._contactMessagesView === null) {
           this._contactMessagesView = new App.Views.ContactMessages;
       }

       App.Menu.activateButton($("#menu-messages"));
       this._contactMessagesView.render(id);
    }
});
