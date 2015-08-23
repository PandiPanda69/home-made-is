App.Menu = Backbone.View.extend({

    initialize: function() {
        this.$el = $('#menu');
    },
    events: {
        "click #menu-home": "displayHome",
        "click #menu-contacts": "displayContacts",
        "click #menu-messages": "displayMessages"
    },
    activateButton: function($btn) {
        $('#menu li').removeClass("active");
        $btn.parent().addClass("active");
    },
    displayHome: function() {
        App.Router.navigate("", {trigger: true});
    },
    displayContacts: function() {
        App.Router.navigate("contacts", {trigger: true});
    },
    displayMessages: function() {
        App.Router.navigate("messages", {trigger: true});
    }
})
