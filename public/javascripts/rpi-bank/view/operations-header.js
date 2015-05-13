App.Views.OperationsHeader = Backbone.View.extend({

   _operationView: null,
   _accountSynth: null,
   _monthForm: null,

   initialize: function() {
      this.$el = $('#main-container');
      this.template = _.template($('#operationshdr-template').html());
   },

   events: {
      "change #month":            "_changeMonth",
      "click  #operation-search": "_searchOperation"
   },

   render: function(accountId) {
      App.Loading.render();

      App.Menu.activateButton($('#menu-accounts'));

      this.currentAccountId = accountId;
      if(!this._checkAccountsAreLoaded()) {
         return;
      }

      this.currentAccount = App.Models.Account.get(this.currentAccountId);

      if(!this.currentAccount) {
         this._onError('Le compte est introuvable');
         App.Router.navigate('accounts', {trigger: true});
      }

      if(!this._checkMonthsAreLoaded()) {
         return;
      }

      this.$el.html(this.template({
            account: this.currentAccount.toJSON(),
            months: App.Models.Month.toJSON(),
            month_names: MonthNames.months
      }));

      this._accountSynth = new App.Views.AccountSynth;
      this._accountSynth.render(this.currentAccount);

      this._monthForm = new App.Views.OperationsAddMonth(this);

      App.Loading.dispose();

      return this;
   },

   _checkAccountsAreLoaded: function() {
      if(App.Models.Account.isEmpty() && !App.Models.Account.initialized) {
         App.Models.Account.fetch({
              success: $.proxy(function() {
                 App.Models.Account.initialized = true;
                 this.render(this.currentAccountId);
              }, this),
              error: $.proxy(function() {
                 this._onError('Erreur en chargeant les comptes.');
              }, this)
         });

         return false;
      }

      return true;
  },

  _checkMonthsAreLoaded: function() {
     if(App.Models.Month.compte === this.currentAccountId) {
        return true;
     }

     App.Models.Month.compte = this.currentAccountId;
     App.Models.Month.fetch({
           success: $.proxy(function() {
               this.render(this.currentAccountId);
           }, this),
           error: $.proxy(function() {
               this._onError('Une erreur est survenue pendant le chargement des mois.');
           }, this)
     });

     return false;
  },

  _changeMonth: function(e) {
     var value = $(e.currentTarget).val();
     if(value === 'ADD_MONTH') {
        this._monthForm.render();
     }
     else if(value !== 'NO_MONTH') {
        App.Loading.render();

        if(this._operationView === null) {
           this._operationView = new App.Views.Operation(this);
        } else {
           this._operationView.initialize();
        }

        this._operationView.render(this.currentAccountId, value);
     }
     else {
        this._accountSynth.render(this.currentAccount);
     }
  },

  _searchOperation: function() {
      var toSearch = $('#operation-search-value').val();
      if(toSearch.length === 0) {
         return false;
      }

      window.open(globals.rootUrl + '/operations/search/' + toSearch, '_blank', 'location=no,height=570,width=580,scrollbars=yes,status=yes');

      return false;
  },

  _onError: function(msg) {
      App.ErrorPopup.setMessage(msg);
      App.ErrorPopup.render();

      App.Loading.dispose();
  }

});
