App.Views.Home = Backbone.View.extend({

    template: _.template($("#home-template").html()),

    initialize: function() {
        this.$el = $("#main-container");
    },

    events: {
    },

    render: function() {
        App.Loading.render();

        $.getJSON([globals.rootUrl, 'stats'].join('/'))
       .done($.proxy(function(data) {
            this.$el.html(this.template(data));
            this._setupGraphic(data);

            App.Loading.dispose();
       }, this))
       .fail(function() {
            App.ErrorPopup.setMessage('Erreur lors du chargement des statistiques.');
            App.ErrorPopup.render();

            App.Loading.dispose();
       });
    },

    _setupGraphic: function(data) {

        $('#messages-abstract-chart').highcharts({
            title: {
                text: 'Volume de messages par jour'
            },
            xAxis: {
                type: 'datetime',
                maxZoom: 7 * 24 * 60 * 60 * 1000,
                title: null
            },
            yAxis: {
                title: null,
                min: 0
            },
            chart: {
                zoomType: 'x'
            },
            plotOptions: {
                area: {
                    marker: {
                        enabled: false
                    },
                    lineWidth: 1,
                    threshold: null,
                    fillColor: {
                        linearGradient: {
                            x1: 0,
                            y1: 0,
                            x2: 0,
                            y2: 1
                        },
                        stops: [
                            [0, Highcharts.getOptions().colors[0]],
                            [1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                        ]
                    }
                }
            },
            series: [{
                type: 'area',
                name: 'Messages par jour',
                data: data.graphData.elements
            }]
        });

    }
});
