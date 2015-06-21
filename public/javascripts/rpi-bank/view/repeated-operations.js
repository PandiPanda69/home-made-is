App.Views.RepeatedOperations = Backbone.View.extend({

    template: _.template($('#repeatedop-template').html()),

    initialize: function() {
         this.setElement($('#main-container'));
    },
    events: {
        "change #account":           "_onChangeAccount",
        "click  .remove-repetition": "_onClickRemove"
    },
    render: function() {
        App.Menu.activateButton($('#menu-params'));
        App.Loading.render();

        App.Models.Account.fetch({
           success: $.proxy(function() {
              this.$el.html(this.template({
                 accounts: App.Models.Account.toJSON()
              }));

              this.delegateEvents();
              $('#account').click();
              $('#repeatedop-list').hide();
              App.Loading.dispose();
           }, this),
           error: $.proxy(function() {
              this._onError('Impossible de charger les comptes.');
           }, this)
        });

        return this;
    },
    _onError: function(msg) {
        App.ErrorPopup.setMessage(msg);
        App.ErrorPopup.render();

        App.Loading.dispose();
    },
    _onChangeAccount: function(e) {
        var val = $(e.currentTarget).val();
        if(val.length === 0) {
           $('#repeatedop-list').hide();
           return;
        }

        App.Loading.render();

        App.Models.Repetition.compte = val;
        App.Models.Repetition.fetch({
           success: $.proxy(function() {

              var $body = $('#repeatedop-list tbody');

              var buffer = '';
              var sum = .0;
              App.Models.Repetition.each($.proxy(function(el) {
                 buffer += this._buildLine(el);
                 sum += parseFloat(el.get('montant'));
              }, this));

              
              $body.html(buffer + this._buildTotalLine(sum));
              $('#repeatedop-list').show();
              this.delegateEvents();

	      App.Loading.dispose();
           }, this),
           error: $.proxy(function() {
              this._onError('Impossible de charger les opérations répétées pour ce compte.');
           }, this)
        });
    },
    _buildLine: function(el) {
       return '<tr id="' + el.id + '"><td>'
                         + el.get('nom') + '</td><td>'
                         + parseFloat(el.get('montant')).toFixed(2) + ' €</td><td>'
                         + (el.get('type') != null ? el.get('type') : '') + '</td>'
                         + '<td><div class="pull-center">'
                         + '<a class="remove-repetition" href=""><i class="icon-remove"></i></a>'
                         + '</div></td></tr>';
    },
    _buildTotalLine: function(totalAmount) {
       return '<tr><td><b>Total</b></td><td><b>' + totalAmount.toFixed(2) + ' €</b></td><td></td><td></td></tr>';
    },
    _onClickRemove: function(e) {
       App.Loading.render();

       var id = $(e.currentTarget).parents('tr').prop('id');
       var model = App.Models.Repetition.get(id);

       model.destroy({
          success: function() {
             $('#' + id).remove();
             App.Loading.dispose();
          },
          error: $.proxy(function() {
             this._onError("Impossible de supprimer l'opération répétée. Une erreur est survenue.");
          }, this)
       });

       return false;
    }
});
