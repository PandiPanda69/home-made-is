App.Views.AccountAdd = Backbone.View.extend({

        el: $("#main-container"),
        main: $("#main-container"),
        editingAccount: null,

        initialize: function() {
                this.accountAddTemplate = _.template($('#accountadd-template').html());
        },
        events: {
                "click #accountadd-validate":      "validate",
                "click #accountadd-cancel":        "cancel"
        },
        render: function(editingAccountId) {
                if(editingAccountId === null || _.isUndefined(editingAccountId)) {
                        this.editingAccount = null;
                        App.Models.AccountType.fetch({
                                success: $.proxy(function() {
                                        App.Models.AccountType.initialized = true;
                                        this.main.html(this.accountAddTemplate({
                                                isEditing: false,
                                                types: App.Models.AccountType.toJSON()
                                        }));
                                }, this)
                        });
                }
                else {
                        if(this.editingAccount === null || this.editingAccount.get('id') !== editingAccountId) {
                                if(App.Models.Account.isEmpty() && !App.Models.Account.initialized) {
                                        App.Loading.render();

                                        App.Models.Account.fetch({
                                                success: $.proxy(function() {
                                                        App.Models.Account.initialized = true;
                                                        this.editingAccount = App.Models.Account.get(editingAccountId);

                                                        App.Models.AccountType.fetch({
                                                                success: $.proxy(function() {
                                                                        App.Models.AccountType.initialized = true;
                                                                        this.main.html(this.accountAddTemplate({
                                                                                isEditing: true,
                                                                                account: this.editingAccount.toJSON(),
                                                                                types: App.Models.AccountType.toJSON()
                                                                        }));
                                                                        App.Loading.dispose();
                                                                }, this)
                                                        });
                                                }, this)
                                        });

                                        return true;
                                }
                                else {
                                        this.editingAccount = App.Models.Account.get(editingAccountId);
                                }
                        }

                        App.Models.AccountType.fetch({
                                success: $.proxy(function() {
                                        App.Models.AccountType.initialized = true;
                                        this.main.html(this.accountAddTemplate({
                                                isEditing: true,
                                                account: this.editingAccount.toJSON(),
                                                types: App.Models.AccountType.toJSON()
                                        }));
                                }, this)
                        });
                }
        },
        validate: function() {
                App.Loading.render();

                var nom  = $('#nom').val();
                var type = $('#type').val();
                var active = $('#active').is(':checked');

                // Adding account
                if(this.editingAccount === null ) {

                        App.Models.Account.create({
                                nom: nom,
                                active: active,
                                type: {
                                        id: type
                                }
                        },
                        {
                                success: this._onSuccess
                        });
                }
                // Update one
                else {
                        this.editingAccount.unset('typeId');
                        this.editingAccount.unset('category');
                        this.editingAccount.save({
                                nom: nom,
                                active: active,
                                type: {
                                        id: type
                                }
                        },
                        {
                                success: this._onSuccess
                        });
                }
        },
        cancel: function() {
                App.Router.navigate('accounts', {trigger: true});
        },
        _onSuccess: function() {
                App.Loading.dispose();
                App.Router.navigate('accounts', {trigger: true});
        },
        _onError: function(msg) {
                App.ErrorPopup.setMessage(msg);
                App.ErrorPopup.render();

                App.Loading.dispose();
        }
});
