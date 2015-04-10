App.Views.TorrentBrowser = Backbone.View.extend({

   template: _.template($('#torrent-browser-template').html()),

   initialize: function() {
      this.$el = $('#main-container');
   },
   render: function(id) {
      if(id === null || typeof id === undefined) {
         this._onError("Aucun torrent spécifié.");
      }

      $.ajax({
         type: 'GET',
         url:  globals.rootUrl + '/torrents/' + id + '/files'
      })
      .fail($.proxy(function(xhr) {
         if(xhr.status === 400) {
            this._onError("Le torrent n'est pas référencé.");
         } else if(xhr.status === 404) {
            this._onError("Les fichiers associés au torrent sont introuvables.");
         } else {
            this._onError("Erreur inconnue (" + xhr.status + ").");
         }
      }, this))
      .done($.proxy(function(data) {
         this.$el.html(this.template(data));
         App.Loading.dispose();
      }, this));
   },
   _onError: function(msg) {
      App.ErrorPopup.setMessage(msg);
      App.ErrorPopup.render();

      App.Loading.dispose();
   }
});
