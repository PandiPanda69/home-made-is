App.Views.Operation = Backbone.View.extend({

    el: $("#operations-container"),
    main: $("#operations-container"),

	currentAccountId: null,
	currentMonthId: null,
	currentMonthBalance: 0,
	operationAddView: null,
	operationEditView: null,

	initialize: function(header) {
        this._header = header;
        
		this.el = $("#operations-container");
		this.main = $("#operations-container");
        
        this.operationTemplate = _.template($('#operations-template').html());
	},
	bindEvents: function() {
		$(".operations-edit").unbind();
		$(".operations-del").unbind();

		_.bindAll(this);
		$(".operations-edit").bind('click', this.editOperation);
		$(".operations-del").bind('click',  this.delOperation);
    },
    render: function(accountId, monthId) {
        App.Menu.activateButton($('#menu-accounts'));

		this.currentAccountId = accountId;
		this.currentMonthId = monthId;

		// Load operations for selected month
		App.Models.Operation.month  = this.currentMonthId;
		App.Models.Operation.compte = this.currentAccountId;

		// Fetch month balance
		$.ajax({
			url: globals.rootUrl + '/accounts/' + this.currentAccountId + '/months/' + this.currentMonthId + '/balance',
			async: false,
			success: $.proxy(function(result) {
				this.currentMonthBalance = result.solde;
			}, this),
			error: function() {
				this._header._onError('Erreur lors du calcul du solde initial.');
			}
		});

		App.Models.Operation.fetch({
			success: $.proxy( function() {
				this.refreshListing();

				App.Loading.dispose();
			}, this)
		});
    },
	refreshListing: function() {

		var stats = this.computeStatistics();

		this.main.html(this.operationTemplate({
			types: App.Models.OperationType.toJSON(),
			operations: App.Models.Operation.toJSON(),
			stats: stats
		}));

		this.bindEvents();
		this.calculateCells();
		this.refreshStatisticsChart(stats);

		// Initialize Add operation view
		this.operationAddView = new App.Views.OperationAdd(this, $('#operation-add'));
		this.operationAddView.render(null);
	},
	calculateCells: function() {
		
		// For each operation, calculate cumulative amounts and balances
		var balance = this.currentMonthBalance;
		var total = 0;

		App.Models.Operation.each(function(current) {

			total += current.get('montant');
			balance += current.get('montant');

			var cumulCell = $('tr[operation=' + current.get('id') + ']').children('#cumul');
			cumulCell.html(parseFloat(total).toFixed(2) + ' €'); 
			cumulCell.addClass(total >= 0 ? 'alert-success' : 'alert-error');

			var balanceCell = $('tr[operation=' + current.get('id') + ']').children('#balance');
			balanceCell.html(parseFloat(balance).toFixed(2) + ' €'); 
			balanceCell.addClass(balance >= 0 ? 'alert-success' : 'alert-error');
		});
	},
    computeStatistics: function() {
        var stats = [];
        var totalAmount = 0.00;

        // First, sum all amounts (except them without any type)
        App.Models.Operation.each(function(element, index) {
           if(element.attributes.type != null) {
                totalAmount += element.attributes.montant;
            }
        }, this);

        // Make sum for each operation type
        App.Models.Operation.each(function(element, index) {
            if(element.attributes.type == null) return;

            for(var i = 0; i < stats.length; i++) {
                if((element.attributes.type != null && stats[i].type == element.attributes.type.name)) {
                    stats[i].montant += element.attributes.montant;
                    stats[i].percent = parseFloat(stats[i].montant * 100 / totalAmount);
                    return;
                }
            }

            stats.push({
                type: element.attributes.type.name,
			    montant: parseFloat(element.attributes.montant),
                percent: parseFloat(element.attributes.montant * 100 / totalAmount)
            });
        }, this);

        return stats;
    },
    refreshStatisticsChart: function(stats) {

        var chartData = [];
        _.each(stats, function(element, index) {
            chartData.push({name: element.type, y: element.percent});
        });

        $('#operations-abstract-chart').highcharts({
            chart: {
                plotBackgroundColor: null,
                plotBorderWidth: null,
                plotShadow: false
	        },
    	    title: {
                text: 'Répartition des dépenses sur le mois.'
            },
            tooltip: {
       		   pointFormat: '{series.name}: <b>{point.percentage}%</b>',
		       percentageDecimals: 2
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: true,
                        color: '#000000',
                        connectorColor: '#000000',
                        formatter: function() {
                            return '<b>'+ this.point.name +'</b>: '+ this.percentage.toFixed(2) +' %';
                        }
                    }
                }
	       },
	       series: [{
                type: 'pie',
                name: 'Répartition',
                data: chartData
	       }]
        });
    },
	editOperation: function(e) {

		var row = $(e.currentTarget).parents('tr');
		var id = row.attr('operation');

		if(this.operationEditView != null) {
			this.refreshListing();
		}

		row = $('tr[operation=' + id + ']');

		this.operationEditView = new App.Views.OperationAdd(this, row);
		this.operationEditView.render(id);

		return false;
	},
	delOperation: function(e) {
		
		App.Loading.render();

		var id = $(e.currentTarget).parents('tr').attr('operation');

		App.Models.Operation.get(id).destroy({
			success: _.bind(function() {
				this.refreshListing();
			}, this)
		});

		return false;
	},
});