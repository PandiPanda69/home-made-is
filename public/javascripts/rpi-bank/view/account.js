App.Views.Account = Backbone.View.extend({

	el: $("#main-container"),
	main: $("#main-container"),

	initialize: function() {
                this.accountTemplate = _.template($('#accounts-template').html());
	},
	events: {
		"click #accounts-add":     "addAccount",
		"click #accounts-edit":    "editAccount",
		"click #accounts-del":     "delAccount",
		"click #accounts-view":    "viewAccount"
	},
	render: function() {
		App.Loading.render();

		App.Menu.activateButton($('#menu-accounts'));

		App.Models.Account.fetch({
			success: $.proxy(function() {
				App.Loading.dispose();
				this.main.html(this.accountTemplate({accounts: App.Models.Account.toJSON()}));
			}, this)
		});
	},
	addAccount: function() {
		App.Router.navigate('accounts/add', {trigger: true});
	},
	editAccount: function(e) {
		var id = $(e.currentTarget).attr('account');
		App.Router.navigate('accounts/edit/' + id, {trigger: true});
		return false;
	},
	delAccount: function(e) {
		var id = $(e.currentTarget).attr('account');
		App.Models.Account.get(id).destroy({success: $.proxy(this.render, this)});
		return false;
	},
	viewAccount: function(e) {
		var id = $(e.currentTarget).attr('account');
		App.Router.navigate('accounts/' + id + '/operations', {trigger: true});
		return false;
	}
});

