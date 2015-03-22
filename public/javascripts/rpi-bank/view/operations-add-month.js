App.Views.OperationsAddMonth = Backbone.View.extend({

  initialize: function(header) {
    this.$el = $('#month_input_form');
    this._header = header;

    // Manually bind events...
    $('#month_add').click($.proxy(this._addMonth, this));
    $('#month_cancel').click($.proxy(this._cancelMonth, this));
  },

  render: function() {

     $('#num_month').val('');
     $('#num_year').val('');

     $('#month').prop('disabled', true);
     $('#month_input_form')
        .removeClass('hide')
        .popover('show');

     return this;
  },

  _addMonth: function() {
     var month = $('#num_month').val();
     var year = $('#num_year').val();

     if(isNaN(year) || year.length === 0) {
         this._header._onError('Année non valide.');
         return;
     }

     App.Loading.render();

     App.Models.Month.create({
         mois: month,
         annee: year
     },
     {
         success: $.proxy(function() {
             this._cancelMonth();
             this._header.render(this._header.currentAccountId);
         }, this),
         error: $.proxy(function() {
             this._header._onError("Une erreur est survenue lors de l'ajout du mois.");
         }, this)
     });
  },

  _cancelMonth: function() {
     $('#month_input_form')
        .addClass('hide')
        .popover('hide');

     $('#month')
        .prop('disabled', false)
        .val('');

     $('#operations-list').addClass('hide');
  }
});
