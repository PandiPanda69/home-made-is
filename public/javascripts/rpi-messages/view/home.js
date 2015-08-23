App.Views.Home = Backbone.View.extend({

    template: _.template($("#home-template").html()),

    initialize: function() {
        this.$el = $("#main-container");
    },

    events: {
        "click #unknown-contacts": "_onUnknownContactsClick"
    },

    render: function() {
        App.Loading.render();

        this.$el.html(this.template());

        App.Loading.dispose();
    },

    _onUnknownContactsClick: function() {
        App.Router.navigate("contacts/inconnu", {trigger: true});
        return false;
    }
});
