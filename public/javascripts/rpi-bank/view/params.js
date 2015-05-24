App.Views.Parameters = Backbone.View.extend({

    main: $("#main-container"),

    initialize: function() {
        this.paramsTemplate = _.template($('#params-template').html());
    },
    events: {
        "click #params-optypes":	  "displayOptypes",
        "click #params-import":		  "displayImport",
        "click #params-heuristics":	  "displayHeuristics",
        "click #params-repeated":     "displayRepeatedOp"
    },
    render: function() {
        App.Menu.activateButton($('#menu-params'));

        this.main.html(this.paramsTemplate);
    },
    displayOptypes: function() {
        App.Router.navigate('params/optypes', {trigger: true});
        return false;
    },
    displayImport: function() {
        App.Router.navigate('params/import', {trigger: true});
        return false;
    },
    displayHeuristics: function() {
        App.Router.navigate('params/heuristics', {trigger: true});
        return false;
    },
    displayRepeatedOp: function() {
        App.Router.navigate('params/repetition', {trigger: true});
        return false;
    }
});