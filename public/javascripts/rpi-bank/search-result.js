var SearchResult = {

   init: function(data) {
      this._data = data;

      var buffer = "";
      var callback = null;
      for(var i = 0; i < this._data.length; i++) {
        if(callback === null) {
           callback = $.proxy(function() {
              return this._newCell(this._data[i].nom + " " + this._newIcon(this._data[i].nomComplet)) +
                this._newCell(this._data[i].montant) +
                this._newCell(this._data[i].type) +
                this._newCell(this._data[i].moisAnnee);
           }, this);
        }

        buffer += this._newRow(callback);
      }

      $('tbody').html(buffer);
   },

   _newRow: function(content) {
      const ROW_BEGIN = "<tr>";
      const ROW_END = "</tr>";

      if(typeof content === "function") {
         return ROW_BEGIN + content() + ROW_END;
      }

      return ROW_BEGIN + content + ROW_END;
   },

   _newCell: function(content) {
     const CELL_BEGIN = "<td>";
     const CELL_END = "</td>";

     if(typeof content === "function") {
         return CELL_BEGIN + content() + ROW_END;
     }

     return CELL_BEGIN + content + CELL_END;
   },

   _newIcon: function(content) {
     const ICON_BEGIN = '<i class="icon-info-sign" data-toggle="tooltip" data-placement="top" title="';
     const ICON_END = '"></i>';

     return ICON_BEGIN + content + ICON_END;
   }
};
