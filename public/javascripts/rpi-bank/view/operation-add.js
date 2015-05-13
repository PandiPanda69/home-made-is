App.Views.OperationAdd = Backbone.View.extend({

    el: $("#main-container"),
    main: $("#operation-add"),

    editingOperationId: null,
    myParent: null,

    initialize: function(myParent, el) {
        this.el = el;
        this.main = el;

        this.myParent = myParent;
        
        this.operationAddTemplate = _.template($('#operationadd-template').html());
    },
    bindEvents: function() {
        this.main.unbind();
        $("#operationadd-validate").unbind();
        $("#operationadd-cancel").unbind();
        $("#montant").unbind();

        _.bindAll(this);
        this.main.bind('keypress',          this.keypressOperation);
        $("#operationadd-validate").bind('click',  this.validate);
        $("#operationadd-cancel").bind('click',    this.cancel);
        $("#montant").bind('blur',          this.formatAmount);
    },
    render: function(editingOperationId) {

        App.Loading.render();

        if(this.checkOperationTypesAreLoaded() !== true) {
            return;
        }

        var currentOperation = null;
        if(editingOperationId != null) {
            this.editingOperationId = editingOperationId;
            currentOperation = App.Models.Operation.get(editingOperationId).toJSON();
        }

        this.main.html(this.operationAddTemplate({
            isEditing: (currentOperation != null),
            types: App.Models.OperationType.toJSON(), 
            operation: currentOperation
        }));
        this.bindEvents();

        App.Loading.dispose();
    },
    checkOperationTypesAreLoaded: function() {

        if(App.Models.OperationType.isEmpty() && App.Models.OperationType.initialized == false) {
            App.Models.OperationType.fetch({
                success: $.proxy( function() {
                    App.Models.OperationType.initialized = true;
                    this.render(this.editingOperationId);
                }, this)
            });

            return false;
        }

        return true;
    },
    formatAmount: function(e) {
        var val = $('#montant').val();
        val.replace(',', '.');

        if(isNaN(parseFloat(val))) {
            val = 0;
        }

        val = parseFloat(val).toFixed(2);
        $('#montant').val(val);
    },
    keypressOperation: function(e) {
        if(e.keyCode == 13) {
            this.validate();
        }
    },
    validate: function() {
        App.Loading.render();

        this.formatAmount();

        var nom = $('#nom').val();
        var nomComplet = $('#nomComplet').val();
        var montant = $('#montant').val();
        var type = $('#type').val();
        var date = $('#date').val();

        type = type.length === 0 ? null : type;

        if(this.editingOperationId == null) {
            App.Models.Operation.create({
                nom: nom,
                nomComplet: nomComplet,
                montant: montant,
                type: type,
                date: date
            },
            {
                success: this.onSuccess
            });
        }
        else {
            App.Models.Operation.get(this.editingOperationId).save({
                nom: nom,
                nomComplet: nomComplet,
                montant: montant,
                type: type,
                date: date
            },
            {
                success: this.onSuccess
            });
        }

        return false;
    },
    cancel: function() {
        this.onSuccess();
        return false;
    },
    onSuccess: function() {
        this.myParent.render(this.myParent.currentAccountId, this.myParent.currentMonthId);
    }
});