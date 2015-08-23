App.Views.OperationSuggestions = Backbone.View.extend({

    myParent: null,

    initialize: function(myParent, el) {
        this.setElement(el);

        this._myParent = myParent;

        this._operationSuggestionTemplate = _.template($('#operationsuggestion-template').html());
    },
    _bindEvents: function() {
         $('.suggestion').click($.proxy(this._onSuggestionClicked, this));
    },
    render: function() {

        App.Loading.render();

        App.Models.Repetition.compte = this._myParent.currentAccountId;
        App.Models.Repetition.fetch({
           success: $.proxy(function() {
              this.$el.html(this._operationSuggestionTemplate({
                 repetitions: App.Models.Repetition.toJSON()
              }));

             this._bindEvents();
             App.Loading.dispose();
           }, this),
           error: $.proxy(function() {
             this._myParent._header._onError("Une erreur est survenue lors de la récupération des suggestions.");
           }, this)
        });

    },
    _onSuggestionClicked: function(e) {
        var id = $(e.target).attr('suggestion');
        var model = App.Models.Repetition.get(id);

        $('#nom, #montant, #type').val('');

        if(!model) {
            this._myParent._header._onError("La suggestion choisie ne semble plus exister.");
        } else {
            $('#nom').val(model.get('nom'));
            $('#montant').val(model.get('montant'));
            $("#type option:contains(" + model.get('type') + ")").attr('selected', true);
        }

        return false;
    }
});
