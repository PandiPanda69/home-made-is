	// jeudi 18 juillet 2013, 21:34:32 (UTC+0200)

$(function() {

	var TypeComptes = new App.Models.AccountType;
        App.Models.AccountType = TypeComptes;

	// models/heuristicpattern.js
        var HeuristicPattern = Backbone.Model.extend({

		initialized: false,
                id: null,
                defaults: function() {
                        return {
                                motif: null
                        };
                },
		urlRoot: function() {

			// Used to persist model.
			return globals.rootUrl + '/heuristics/name/pattern';
		}
        });

	// models/heuristicstype.js
        var HeuristicsType = Backbone.Model.extend({

                urlRoot: globals.rootUrl + '/heuristics/type',
		initialized: false,
                id: null,
                defaults: function() {
                        return {
                                nom: null,
				type: null,
				threshold: 0.00
                        };
                }
        });

	// models/month.js
        var Month = Backbone.Model.extend({

		initialized: false,
                id: null,
                defaults: function() {
                        return {
                                mois: null,
				annee: null
                        };
                },
		urlRoot: function() {

			// Used to persist model.
			return globals.rootUrl + '/accounts/' + Months.compte + '/months';
		}
        });        
		
	// models/operationtype.js
        var OperationType = Backbone.Model.extend({

                urlRoot: globals.rootUrl + '/optypes',
		initialized: false,
                id: null,
                defaults: function() {
                        return {
                                name: "None",
                                icon: 0
                        };
                }
        });

        var Accounts = new App.Models.Account;
	App.Models.Account = Accounts;

	// dao/heuristicpattern.js
        /*******************************************
        * DAO
        *******************************************/
        var HeuristicPatternList = Backbone.Collection.extend({

                model: HeuristicPattern,
                url: globals.rootUrl + '/heuristics/name/pattern',
                initialized: false,
                comparator: 'order'
        });

        var HeuristicPatterns = new HeuristicPatternList;

	// dao/heuristicstype.js
        /*******************************************
        * DAO
        *******************************************/
        var HeuristicsTypeList = Backbone.Collection.extend({

                model: HeuristicsType,
                url: globals.rootUrl + '/heuristics/type',
                initialized: false,
                comparator: 'order'
        });

        var HeuristicsTypes = new HeuristicsTypeList;
	// dao/month.js
        /*******************************************
        * DAO
        *******************************************/
        var MonthList = Backbone.Collection.extend({

                model: Month,
                initialized: false,
		compte: null,

		url: function() {
			return globals.rootUrl + '/accounts/' + this.compte + '/months';
		},
		comparator: function(model) {
			var m = model.get('mois');
			var a = model.get('annee');
			return (a * a) + m;
		}
        });

        var Months = new MonthList;
	App.Models.Month = Months;

	// dao/operation.js
        /*******************************************
        * DAO
        *******************************************/
        var OperationList = App.Models.Operation;
        App.Models.Operation = new OperationList;
		
	// dao/operationtype.js
        /*******************************************
        * DAO
        *******************************************/
        var OperationTypeList = Backbone.Collection.extend({

                model: OperationType,
                url: globals.rootUrl + '/optypes',
                initialized: false,
                comparator: 'order'
        });

        var OperationTypes = new OperationTypeList;
        App.Models.OperationType = OperationTypes;
	// views/accountadd.js
        /*******************************************
        * View
        *******************************************/
        var AccountAddView = App.Views.AccountAdd;

	// views/heuristics.js
	/*******************************************
        * View
        *******************************************/
        var HeuristicsView = Backbone.View.extend({

                el: $("#main-container"),
                main: $("#main-container"),
                heuristicsTemplate: _.template($('#heuristics-template').html()),

                events: {
			"click #heuristics-compute":	"computeHeuristics",
			"click #pattern-add":		"addPattern",
			"click .pattern-del":		"delPattern"
                },
                render: function() {
                        Menu.activateButton($('#menu-params'));
			LoadingDialog.render();

			if(HeuristicsTypes.initialized == false) {

				HeuristicsTypes.fetch({
					success: _.bind(function() {
						HeuristicsTypes.initialized = true;
						this.render();

					}, this),
					error: function() {
						alert('Une erreur est survenue lors du chargement des heuristiques sur les types.');
					}
				});

				return;
			}

			if(HeuristicPatterns.initialized == false) {
				HeuristicPatterns.fetch({
					success: _.bind(function() {
						HeuristicPatterns.initialized = true;
						this.render();
					}, this),
					error: function() {
						alert('Une erreur est survenue lors du chargement des motifs.');
					}
				});
		
				return;			
			}

			this.main.html(this.heuristicsTemplate({
				optypes: HeuristicsTypes.toJSON(),
				opnamepatterns: HeuristicPatterns.toJSON()
			}));

			LoadingDialog.dispose();
                },
		computeHeuristics: function() {
			
			LoadingDialog.render();

			$.post('heuristics/type/compute', _.bind(function() {
					// Force to reload heuristics listing
					HeuristicsTypes.initialized = false;
					this.render();

					LoadingDialog.dispose();
				}, this));

			return false;
		},
		addPattern: function() {

			LoadingDialog.render();

			var pattern = $('#pattern').val();

			HeuristicPatterns.create({motif: pattern}, {
				success: _.bind(function() {
					this.onSuccess();
				}, this),
				error: function() {
					alert('Une erreur est survenue.');
					LoadingDialog.dispose();
				}
			});

			return false;
		},
		delPattern: function(e) {

			LoadingDialog.render();

			var patternId = $(e.currentTarget).attr('pattern');
			
			HeuristicPatterns.get(patternId).destroy({
				success: _.bind(function() {
					this.onSuccess();
				}, this),
				error: function() {
					alert('Une erreur est survenue.');
					LoadingDialog.dispose();
				}
			});

			return false;
		},
		onSuccess: function() {
			// Remember the activated tab and associated button(s).
			var tabContentId = $('div.active').attr('id');

			var tabBtnId = [];
			$("#main-container li.active").each(function(i, target) { 
				tabBtnId.push($(target).attr('id'));
			});

			// Re-render page
			this.render();

			// Desactive default tab
			$('#main-container li.active').removeClass('active');
			$('div.active').removeClass('active in');

			// And re-enable the previous ones
			$('#' + tabContentId).addClass('active in');

			_.each(tabBtnId, function(id) {
				$('#' + id).addClass('active');
			});

			LoadingDialog.dispose();
		}
	});

	// views/importinput.js
	/*******************************************
        * View
        *******************************************/
        var ImportInputView = Backbone.View.extend({

                el: $("#main-container"),
                main: $("#main-container"),
                importTemplate: _.template($('#import-input-template').html()),
		selectedAccount: null,
		selectedMonth: null,

                events: {
			"click #import-analyse":	"analyseData",
			"blur #import-delimiter":	"blurDelimiter"
                },
                render: function(idAccount, idMonth) {
                        Menu.activateButton($('#menu-params'));

			this.selectedAccount = idAccount;
			this.selectedMonth   = idMonth;

			this.main.html(this.importTemplate);
                },
		blurDelimiter: function(e) {
			var target = $(e.currentTarget);

			if(target.val().length > 0) {
				target.parent().removeClass('error');
				target.parent().addClass('success');
			}
			else {
				target.parent().removeClass('success');
				target.parent().addClass('error');
			}
		},
		analyseData: function(e) {
			var data  = $('#import-raw-data').val();
			var delim = $('#import-delimiter').val();

			if(data.length == 0 || delim.length == 0) {
				alert('Donnée manquante !');
				return;
			}

			data  = encode64(data);
			delim = encode64(delim);
			Routes.navigate("params/import/account=" + this.selectedAccount + "&month=" + this.selectedMonth + "/preview/data=" + data + "&delim=" + delim, {trigger: true});
		}
        });

	// views/import.js
	/*******************************************
        * View
        *******************************************/
        var ImportView = Backbone.View.extend({

                el: $("#main-container"),
                main: $("#main-container"),
                importTemplate: _.template($('#import-home-template').html()),

                events: {
			"click  #import-continue":	"continueImport",
			"change #import-account":	"onAccountChange"
                },
                render: function() {
                        Menu.activateButton($('#menu-params'));

			if(this.checkAccountsAreLoaded() == false) {
				return;
			}

			this.main.html(this.importTemplate({accounts: Accounts.toJSON(), months: null}));
                },
		checkAccountsAreLoaded: function() {

                        if(Accounts.isEmpty() && Accounts.initialized == false) {
                                Accounts.fetch({
                                        success: _.bind( function() {
                                                Accounts.initialized = true;
                                                this.render();
                                        }, this),
					error: _.bind( function() {
						alert('Impossible de récupérer les comptes.');
					}, this)
                                });

                                return false;
                        }

                        return true;
                },
		onAccountChange: function(e) {
			var selectedAccount = $(e.currentTarget).val();

			if(parseInt(selectedAccount) > 0) {
				LoadingDialog.render();

				Months.compte = selectedAccount;
				Months.fetch({
					success: _.bind( function() {
						this.main.html(this.importTemplate({
							accounts: Accounts.toJSON(),
							months: Months.toJSON(),
							month_names: MonthNames.months
						}));

						$("#import-account").val(selectedAccount);
						LoadingDialog.dispose();
					}, this),
					error: _.bind( function() {
						alert('Impossible de charger les mois.');
						LoadingDialog.dispose();
					}, this)
				});
			}
			else {
				this.main.html(this.importTemplate({accounts: Accounts.toJSON(), months: null}));
			}
		},
		continueImport: function() {
			var selectedAccount = $("#import-account").val();
			var selectedMonth   = $("#import-month").val();

			if(parseInt(selectedAccount) > 0 && parseInt(selectedMonth) > 0) {
				Routes.navigate("params/import/account=" + selectedAccount + "&month=" + selectedMonth + "/input", {trigger: true});
			}
			else {
				alert('Vérifier les champs saisis.');
			}
		}
        });

	// views/importpreview.js
	/*******************************************
        * View
        *******************************************/
        var ImportPreviewView = Backbone.View.extend({

                el: $("#main-container"),
                main: $("#main-container"),
                importPreviewTemplate: _.template($('#import-preview-template').html()),
		selectedAccount: null,
		selectedMonth: null,
		rawData: null,
		delimiter: null,

                events: {
                       "click #import-do":     "importDo",
                       "click #import-cancel": "importCancel",
                },
                render: function(idAccount, idMonth, data, delim) {
                        Menu.activateButton($('#menu-params'));

			this.rawData   = decode64(data);
			this.delimiter = decode64(delim);

			this.selectedAccount = idAccount;
			this.selectedMonth   = idMonth;

			this.main.html(this.importPreviewTemplate({data: this.rawData, delim: this.delimiter}));
                },
		importDo: function() {

			LoadingDialog.render();

			try {
				var fieldsType = this.getFieldsType();
			
				// Check wether a field has not been selected several times
				if(this.checkFieldsAreValid(fieldsType) == false) {
					throw 'Une colonne est présente plusieurs fois.';
				}

				var data = this.prepareData();
				data = this.formatData(data, fieldsType);

				// Send data to server for addition...
				$.ajax({
					url: 'accounts/' + this.selectedAccount + '/months/' + this.selectedMonth + '/import',
					type: 'POST',
					async: false,
					contentType: 'application/json',
					data: JSON.stringify(data),
					success: _.bind(function() {
						LoadingDialog.dispose();
						Routes.navigate("/params/import", {trigger: true});
					}, this),
					error: _.bind(function() {
						throw "Une erreur est survenue lors de l'import des données.";
					}, this)
				});
			}
			catch(err) {
				alert(err);
				LoadingDialog.dispose();
			}
		},
		importCancel: function() {
			Routes.navigate("params/import", {trigger: true});
		},
		getFieldsType: function() {
			var selects = $("select[id^=field_]");

                        var fieldsType = [];
			var tmp;
                        for(var i = 0; i < selects.length; i++) {
				tmp = $(selects[i]).val();
                                switch(tmp) {
                                        case "1":
                                        case "2":
                                        case "3":
                                                fieldsType.push(parseInt(tmp));
                                                break;
                                        default:
                                                fieldsType.push(null);
                                }
                        }

			return fieldsType;
		},
		checkFieldsAreValid: function(fields) {

			var valid = true;

			_.each(fields, function(first, i) {
				if(first != null) {
					_.each(fields, function(second, j) {
						// Indexes must be different
						if(second != null && i != j) {
							if(first == second) {
								valid = false;
							}
						}
					});
				}
			});

			return valid;
		},
		prepareData: function() {

			var regexLine  = new RegExp("[\n]+", "g");
			var regexDelim = new RegExp("[" + this.delimiter + "]+", "g");

			var finalLines = [];
			var lines = this.rawData.split(regexLine);
			for(var i in lines) {

				if(lines[i].length == 0) {
					continue;
				}

				var preparedLine = [];
				var fields = lines[i].split(regexDelim);
				for(var j in fields) {
					preparedLine.push(fields[j]);
				}

				finalLines.push(preparedLine);
			}

			return finalLines;
		},
		formatData: function(data, fields) {
		
			var finalData = [];

			for(var i = 0; i < data.length; i++) {

				var lineData = {};
				for(var j = 0; j < data[i].length; j++) {

					switch(fields[j]) {
						case 1:
							lineData['nom'] = data[i][j];
							break;
						case 2:
							lineData['nomComplet'] = data[i][j];
							break;
						case 3:
							lineData['montant'] = this.formatAmount(data[i][j]);
							break;
					}
				}

				finalData.push(lineData);
			}

			return finalData;
		},
		formatAmount: function(amount) {

			// Replace coma by dot and remove any space.
			amount = amount.replace(",", ".").replace(' ', '');
			if(isNaN(parseFloat(amount))) {
				amount = amount.replace(/[^0-9.]/g, "");

				if(isNaN(parseFloat(amount))) {
					throw "Impossible de formater le montant...";
				}

				amount = amount;
			}

			return parseFloat(amount);
		}
        });

	// views/loading.js
        /*******************************************
        * View
        *******************************************/
        var LoadingView = Backbone.View.extend({

                dialog: $('#loading-dialog'),

                render: function() {
                        this.dialog.modal({
                                show:     true,
                                backdrop: false,
                                keyboard: false
                        });
                },
                dispose: function() {
                        this.dialog.modal('hide');
                }
        });

        var LoadingDialog = new LoadingView;

	// views/operationheader.js
	/*******************************************
        * View
        *******************************************/
        var OperationHeaderView = App.Views.OperationsHeader;

	
	// views/operationtypeadd.js
        /*******************************************
        * View
        *******************************************/
        var OperationTypeAddView = Backbone.View.extend({

                el: $("#main-container"),
                main: $("#main-container"),
                optypeAddTemplate: _.template($('#optypeadd-template').html()),
                editingOptype: null,

                events: {
                        "click #optypeadd-validate":      "validate",
                        "click #optypeadd-cancel":        "cancel"
                },
                render: function(editingOptypeId) {
                        if(editingOptypeId == null) {
				this.editingOptype = null;
                                this.main.html(this.optypeAddTemplate({isEditing: false}));
                        }
                        else {
                                if( this.editingOptype == null ) {
                                        if(OperationTypes.isEmpty() && !OperationTypes.initialized) {
                                                LoadingDialog.render();

                                                var self = this;
                                                OperationTypes.fetch({
                                                        success: function() {
                                                                OperationTypes.initialized = true;
                                                                self.editingOptype = OperationTypes.get(editingOptypeId);
                                                                self.main.html(self.optypeAddTemplate({isEditing: true, optype: self.editingOptype.toJSON()}));
                                                                LoadingDialog.dispose();
                                                        }
                                                });

                                                return true;
                                        }
                                        else {
                                                this.editingOptype = OperationTypes.get(editingOptypeId);
                                        }
                                }

                                this.main.html(this.optypeAddTemplate({isEditing: true, optype: this.editingOptype.toJSON()}));
                        }
                },
                validate: function() {
                        LoadingDialog.render();

                        var name = $('#name').val();
                        var icon = $('#icon').val();

                        // Adding operation type
                        if(this.editingOptype == null ) {

                                OperationTypes.create({
                                        name: name,
                                        icon: icon
                                },
                                {
                                        success: this.onSuccess
                                });
                        }
                        // Update one
                        else {
                                this.editingOptype.save({
                                        name: name,
                                        icon: icon
                                },
                                {
                                        success: this.onSuccess
                                });
                        }
                },
                cancel: function() {
                        Routes.navigate('params/optypes', {trigger: true});
                },
                onSuccess: function() {
                        LoadingDialog.dispose();
                        Routes.navigate('params/optypes', {trigger: true});
                }		
        });

	// views/operationtypelisting.js
	/*******************************************
        * View
        *******************************************/
        var OperationTypeView = Backbone.View.extend({

                el: $("#main-container"),
                main: $("#main-container"),
                optypeTemplate: _.template($('#optypes-template').html()),

                events: {
                        "click #optypes-add":     "addOptype",
                        "click #optypes-edit":    "editOptype",
                        "click #optypes-del":     "delOptype"
                },
                render: function() {
                        Menu.activateButton($('#menu-params'));

                        //if(Users.isEmpty()) {
                                LoadingDialog.render();

                                var self = this;
                                OperationTypes.fetch({
                                        success: function() {
                                                LoadingDialog.dispose();
                                                self.main.html(self.optypeTemplate({optypes: OperationTypes.toJSON()}));
                                        }
                                });
                        //}
                        //else {
                        //      this.main.html(this.userTemplate({users: Users.toJSON()}));
                        //}
                },
                addOptype: function() {
                        Routes.navigate('params/optypes/add', {trigger: true});
                },
                editOptype: function(e) {
                        var id = $(e.currentTarget).attr('optype');
                        Routes.navigate('params/optypes/edit/' + id, {trigger: true});
                        return false;
                },
                delOptype: function(e) {
			LoadingDialog.render();

                        var id = $(e.currentTarget).attr('optype');
                        OperationTypes.get(id).destroy({
				success: _.bind(function() {
					this.render();
				}, this)
			});
                        return false;
                }

        });

	var AccountTypeView = Backbone.View.extend({
		
		events: {
			"click #accounttypes-add":  "addType",
			"click .accounttypes-edit": "editType",
			"click .accounttypes-del":  "removeType"
		},
		initialize: function() {
			this.$el = $('#main-container');
			this.template = _.template($('#accounttypes-template').html());
		},
		render: function() {
			Menu.activateButton($('#menu-params'));

			$.when(TypeComptes.fetch())
			.done($.proxy(function() {
				TypeComptes.initialized = true;
				this.$el.html(this.template({accounttypes: TypeComptes.toJSON()}));
			}, this));
		},
		addType: function() {
			Routes.navigate('params/accounttypes/add', {trigger: true});
		},
		editType: function(evt) {
			var typeId = $(evt.currentTarget).attr('accounttype');
			Routes.navigate('params/accounttypes/edit/' + typeId, {trigger: true});
			return false;
		},
		removeType: function(evt) {
			LoadingDialog.render();

			var typeId = $(evt.currentTarget).attr('accounttype');
                        TypeComptes.get(typeId).destroy({
                                success: $.proxy(function() {
					LoadingDialog.dispose();
                                        this.render();
                                }, this),
				error: function(model, xhr, error) {
					var data = eval("(" + xhr.responseText + ")");
					alert(data.msg);
					LoadingDialog.dispose();
				}
                        });

			return false;
		}
	});

	var AccountTypeAddView = Backbone.View.extend({
		events: {
			"click #accounttypeadd-validate":	"validate",
			"click #accounttypeadd-cancel":		"cancel",

			"change #type":				"onTypeChange",
			"click  .accounttypeadd-addrate":	"onAddRate",
			"click  .accounttypeadd-delrate":	"onDeleteRate"
		},
		initialize: function() {
			this.$el = $('#main-container');
			this.template = _.template($('#accounttypeadd-template').html());
		},
		render: function(editingAccountTypeId) {
			Menu.activateButton($('#menu-params'));
			
			if(editingAccountTypeId == null) {
				this.$el.html(this.template({isEditing: false}));	
				this.editingType = null;
				$('#accounttypeadd-saving-div').hide();
			}
			else {
				if(TypeComptes.initialized == true) {
					this.editingType = TypeComptes.get(editingAccountTypeId);
					this.$el.html(this.template({isEditing: true, accounttype: this.editingType.toJSON()}));
					$('#type').val(this.editingType.get('type'));
				}
				else {
					LoadingDialog.render();
					$.when(TypeComptes.fetch()).done($.proxy(function() {
						TypeComptes.initialized = true;
						this.editingType = TypeComptes.get(editingAccountTypeId);
						this.$el.html(this.template({isEditing: true, accounttype: this.editingType.toJSON()}));
						$('#type').val(this.editingType.get('type'));
						if(this.editingType.get('type') != 'SAVING') {
							$('#accounttypeadd-saving-div').hide();
						}
						LoadingDialog.dispose();
					}, this));
				}
			}
		},
		validate: function() {
                        LoadingDialog.render();

                        var libelle = $('#libelle').val();
                        var type    = $('#type').val();

                        if(this.editingType == null ) {

                                TypeComptes.create({
                                        libelle: libelle,
                                        type: type,
					taux: this.taux
                                },
                                {
                                        success: this.onSuccess
                                });
                        }
                        // Update one
                        else {
                                this.editingType.save({
                                        libelle: libelle,
                                        type: type,
					taux: this.taux
                                },
                                {
                                        success: this.onSuccess
                                });
                        }
		},
		cancel: function() {
			Routes.navigate('/params/accounttypes', {trigger: true});
			this.taux = null;
		},
		onSuccess: function() {
			Routes.navigate('/params/accounttypes', {trigger: true});
			this.taux = null;
			LoadingDialog.dispose();
		},
		onTypeChange: function(evt) {
			var value = $(evt.currentTarget).val();
			if(value == 'SAVING') {
				$('#accounttypeadd-saving-div').show();
			}
			else {
				$('#accounttypeadd-saving-div').hide();
			}

			this.taux = null;
		},
		onAddRate: function(evt) {
			var date = $('#rate-date').val();
			var rate = $('#rate').val();

			if(date == null || date.length == 0) {
				$('#rate-date').addClass('error');
				return false;
			}

			if(rate == null || rate.length == 0) {
				$('#rate').addClass('error');
				return false;
			}

			if(this.taux == null) {
				this._initializeTaux();
			}

			this.taux.push({date: date, rate: rate});

			var $thisLine = $('#accounttypeadd-rate-template');
			$thisLine.after($('<tr id="accounttypeadd-rate-template">').html($thisLine.html()));

			$thisLine.removeAttr('id');
			$thisLine.find('#rate').removeAttr('id').addClass('rate').prop('disabled', true);
			$thisLine.find('#rate-date').removeAttr('id').addClass('rate-date').prop('disabled', true);
			$thisLine.find('i.accounttypeadd-addrate').remove();
			$thisLine.find('i.accounttypeadd-delrate').removeClass('hide');

			console.log(this.taux);
		},
		onDeleteRate: function(evt) {
			var $targetLine = $(evt.currentTarget).parents('tr');

			var date = $targetLine.find('.rate-date').val();
			var rate = $targetLine.find('.rate').val();

			if(this.taux == null) {
				this._initializeTaux();
			}

			$.each(this.taux, $.proxy(function(id, element) {
				if(element.date == date && element.rate == rate) {
					this.taux.splice(id, 1);
					return false;
				}
			}, this));

			console.log(this.taux);

			$targetLine.remove();
		},
		_initializeTaux: function() {
			if(this.editingType != null) {
				this.taux = this.editingType.get('taux');
			}
			else {
				this.taux = new Array;
			}
		}
	});

	// views/params.js
	/*******************************************
        * View
        *******************************************/
        var ParamsView = App.Views.Parameters;


	// router/router.js
        /*******************************************
        * Router
        *******************************************/
        var Router = Backbone.Router.extend({

                currentView: null,

		paramsView:   new ParamsView,
		optypeListView:  new OperationTypeView,
		optypeAddView:   new OperationTypeAddView,
		accountListView: null,
		accountAddView:  new AccountAddView,
		operationHeaderView:	 new OperationHeaderView,
		importView:	 new ImportView,
		importInputView:	 new ImportInputView,
		importPreviewView:	 new ImportPreviewView,
		heuristicsView:	new HeuristicsView,
		accounttypeListView: new AccountTypeView,
		accounttypeAddView:  new AccountTypeAddView,
        repeatedOperationsView: new App.Views.RepeatedOperations,

        routes: {
            "":                     	"home",
			"params":			"paramsHome",
			"params/optypes":		"optypesHome",
			"params/optypes/add":		"optypesAdd",
			"params/optypes/edit/:id":	"optypesEdit",
			"params/accounttypes":		"accounttypesHome",
			"params/accounttypes/add":	"accounttypeAdd",
			"params/accounttypes/edit/:id": "accounttypeEdit",
			"accounts":			"accountsHome",
			"accounts/add":			"accountsAdd",
			"accounts/edit/:id":		"accountsEdit",
			"accounts/:idAccount/operations":	"accountOperationsHeader",
			"params/import":		"importHome",
			"params/import/account=:idAccount&month=:idMonth/input":		"importInput",
			"params/import/account=:idAccount&month=:idMonth/preview/data=:data&delim=:delim":	"importPreview",
			"params/heuristics":		"heuristicsHome",
            "params/repetition":    "repeatedOperations"
                },

		initialize: function() {
			this.homeView = new App.Views.Home;
		},
                render: function(view, opt1, opt2, opt3, opt4) {
                        this.currentView = view;
                        this.currentView.render(opt1, opt2, opt3, opt4);
                },
                home: function() {
                        this.render(this.homeView);
                },
		paramsHome: function() {
			this.render(this.paramsView);
		},
		optypesHome: function() {
			this.render(this.optypeListView);
		},
		optypesAdd: function() {
			this.render(this.optypeAddView);
		},
		optypesEdit: function(id) {
			this.render(this.optypeAddView, id);
		},
		accountsHome: function() {
			if(!this.accountListView) {
				this.accountListView = new App.Views.Account;
			}

			this.render(this.accountListView);
		},
		accountsAdd: function() {
			this.render(this.accountAddView);
		},
		accountsEdit: function(id) {
			this.render(this.accountAddView, id);
		},
		accountOperationsHeader: function(idAccount) {
			this.render(this.operationHeaderView, idAccount);
		},
		importHome: function() {
			this.render(this.importView);
		},
		importInput: function(idAccount, idMonth) {
			this.render(this.importInputView, idAccount, idMonth);
		},
		importPreview: function(idAccount, idMonth, data, delim) {
			this.render(this.importPreviewView, idAccount, idMonth, data, delim);
		},
		heuristicsHome: function() {
			this.render(this.heuristicsView);
		},
		accounttypesHome: function() {
			this.render(this.accounttypeListView);
		},
		accounttypeAdd: function() {
			this.render(this.accounttypeAddView);
		},
		accounttypeEdit: function(idType) {
			this.render(this.accounttypeAddView, idType);
		},
        repeatedOperations: function() {
            this.render(this.repeatedOperationsView);
        }
    });

        var Menu = App.Menu;

	App.Router = new Router;
    var Routes = App.Router;

	Backbone.history.start({root: globals.rootUrl});

});
