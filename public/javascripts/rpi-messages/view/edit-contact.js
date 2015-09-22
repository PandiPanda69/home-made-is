App.Views.EditContact = Backbone.View.extend({

    template: _.template($('#show-contact-template').html()),
    _popup: null,

    initialize: function() {
        this.$el = $('#main-container');
    },

    events: {
        "click #edit-contact": "_onEdit",
        "click #save-contact": "_onSave",
        "click #cancel-contact": "_onView",
        "click #delete-contact": "_onDeleteContact",
        "click #view-messages": "_onViewMessages"
    },

    render: function(id, action) {

        App.Loading.render();

        this._getContactById(id, $.proxy(function(contact) {
            
            this.currentContact = contact;

            this.$el.html(this.template({
                contact: this.currentContact.toJSON(),
                isEditing: (action === 'EDIT')
            }));

            $.getJSON([globals.rootUrl, 'stats/contact', this.currentContact.id].join('/'))
           .done($.proxy(function(data) {
                if(action !== 'EDIT') {
                    this._setupGraphic(data);
                }
                App.Loading.dispose();
            }, this))
            .fail($.proxy(function() {
                this._onError('Impossible de charger les statistiques.');
            }, this))
        }, this));
    },

    _getContactById: function(id, callback) {
        if(App.Models.Contacts.length > 0) {
            callback(App.Models.Contacts.get(id));
            return;
        }

        $.when(App.Models.Contacts.fetch())
        .done(function() {
            callback(App.Models.Contacts.get(id));
        })
        .fail($.proxy(function() {
            this._onError('Impossible de charger la liste des contacts.');
        }, this));
    },

    _setupGraphic: function(data) {

        $('#messages-volume-chart').highcharts({
            title: {
                text: 'Messages échangés'
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
                    lineWidth: 1
                }
            },
            series: [{
                type: 'area',
                name: 'Messages par jour',
                data: data.elements 
            }]
        });
    },

    _onError: function(msg) {
        App.ErrorPopup.setMessage(msg);
        App.ErrorPopup.render();

        App.Loading.dispose();
    },

    _onEdit: function() {
       this.render(this.currentContact.get('id'), 'EDIT'); 
    },

    _onView: function() {
       this.render(this.currentContact.get('id'), 'VIEW'); 
    },

    _onSave: function() {

        App.Loading.render();

        var name = $('#name').val();
        if(name.length < 3) {
            this._onError('Le nom du contact doit contenir au moins 3 caractères.');
            return;
        }
        
        this.currentContact.save({
            name: name
        })
        .done($.proxy(function() {
            this._onView();
        }, this))
        .error($.proxy(function() {
            this._onError('Impossible de sauvegarder le contact.');
        }, this));
    },

    _onDeleteContact: function() {
        if(this._popup === null) {
            this._popup = new App.Views.DeleteContactPopup();
            this._popup.setOwner(this);
        }
        this._popup.render();
    },

    _onViewMessages: function() {
        App.Router.navigate('contacts/' + this.currentContact.id + '/messages', {trigger: true});
    }
});
