/**
 * Defining every backbone mecanisms
 */
$(function() {

		/**
		 * Definition of module model.
		 */
   		var Module = Backbone.Model.extend({
   		
   			urlRoot: globals.rootUrl + 'modules',
   			id: null,
   			defaults: function() {
   				return {
   					name: "",
   					desc: "",
   					route: "",
   					active: false
   				};
   			}
   		});
	    
   		/**
   		 * Definition of module collection.
   		 */
   		var ModuleList = Backbone.Collection.extend({

   			model: Module,
   			url: globals.rootUrl + 'modules',
   			initialized: false,
   			comparator: 'order',
   			loadModules: function(arg) {
   				this.initialized = true;
   				this.fetch(arg);
   			}
   		});
	
   		/**
   		 * Instantiate module list.
   		 */
	    var Modules = new ModuleList;
	    
	    /**
	     * Module listing view.
	     */
		var ModuleView = Backbone.View.extend({
			el: $('#main-container'),
			moduleTemplate: _.template($('#modules-template').html()),
			
			events: {
				"click #modules-add":	"addModule",
				"click #modules-edit":	"editModule",
				"click #modules-del":	"deleteModule"
			},
			
			render: function() {
				Menu.activateButton($('#menu-modules'));
				
				if(Modules.initialized == false) {
					LoadingDialog.render();
					
					Modules.loadModules({
						success: $.proxy(function() {
							$(this.el).html(this.moduleTemplate({modules: Modules.toJSON()}));
							LoadingDialog.dispose();
						}, this)
					});
				}
				else {
					$(this.el).html(this.moduleTemplate({modules: Modules.toJSON()}));
					LoadingDialog.dispose();
				}
			},
			addModule: function() {
				Routes.navigate("modules/add", {trigger: true});
			},
			editModule: function(e) {
				var id = $(e.currentTarget).attr('module');
				Routes.navigate('modules/edit/' + id, {trigger: true});
                return false;
			},
			deleteModule: function(e) {
				LoadingDialog.render();
				
				var id = $(e.currentTarget).attr('module');
				Modules.get(id).destroy({
                	success: _.bind(this.render, this),
                	error:   _.bind(this.render, this)
                });
				
				return false;
			}
		});
		
		var ModuleAddView = Backbone.View.extend({
			el: $('#main-container'),
			moduleAddTemplate: _.template($('#moduleadd-template').html()),
            editingModule: null,

            events: {
                    "click #moduleadd-validate":      "validate",
                    "click #moduleadd-cancel":        "cancel"
            },
            render: function(editingModuleId) {
            		Menu.activateButton($('#menu-modules'));
                    if(editingModuleId == null) {
                    		this.editingModule = null;
                            $(this.el).html(this.moduleAddTemplate({isEditing: false}));
                    }
                    else {
                            if(this.editingModule == null) {
                                    if(Modules.isEmpty() && !Modules.initialized) {
                                            LoadingDialog.render();

                                            Modules.loadModules({
                                                    success: $.proxy(function() {
                                                            this.editingModule = Modules.get(editingModuleId);
                                                            $(this.el).html(this.moduleAddTemplate({isEditing: true, module: this.editingModule.toJSON()}));
                                                            LoadingDialog.dispose();
                                                    }, this)
                                            });

                                            return true;
                                    }
                                    else {
                                            this.editingModule = Modules.get(editingModuleId);
                                    }
                            }

                            $(this.el).html(this.moduleAddTemplate({isEditing: true, module: this.editingModule.toJSON()}));
                    }
            },
            validate: function() {
                    LoadingDialog.render();

                    var data = $('#main-container form').serializeObject();
                    
                    // Adding user
                    if(this.editingModule == null ) {

	                        Modules.create(data,                    	
                            {
                                    success: this.onSuccess
                            });
                    }
                    // Update one
                    else {
                            this.editingModule.save(data,
                            {
                                    success: this.onSuccess
                            });
                    }
            },
            cancel: function() {
                    Routes.navigate('modules', {trigger: true});
            },
            onSuccess: function(model) {
                    LoadingDialog.dispose();
                    Routes.navigate('modules', {trigger: true});
            }
		});
	
	
		var User = Backbone.Model.extend({

        	urlRoot: globals.rootUrl + 'users',
        	initialized: false,
        	id: null,
        	defaults: function() {
                        return {
                                username: "Utilisateur",
                                password: "",
                                firstName: "Pr�nom",
                                isAdmin: false,
                                modules: []
                        };
        	}
		});

        var UserList = Backbone.Collection.extend({

        	model: User,
        	url: globals.rootUrl + 'users',
        	initialized: false,
        	comparator: 'order',
        	loadUsers: function(arg) {
        		this.initialized = true;
        		this.fetch(arg);
        	}
        });
        
        var Users = new UserList;
        
        var UserView = Backbone.View.extend({

            el: $("#main-container"),
            userTemplate: _.template($('#users-template').html()),

            events: {
                    "click #users-add":     "addUser",
                    "click #users-edit":    "editUser",
                    "click #users-del":     "delUser"
            },
            render: function() {
                    Menu.activateButton($('#menu-users'));

                    LoadingDialog.render();

                    Users.fetch({
                    	success: $.proxy(function() {
                                 LoadingDialog.dispose();
                                 $(this.el).html(this.userTemplate({users: Users.toJSON()}));
                        }, this)
                     });
            },
            addUser: function() {
                    Routes.navigate('users/add', {trigger: true});
            },
            editUser: function(e) {
                    var id = $(e.currentTarget).attr('user');
                    Routes.navigate('users/edit/' + id, {trigger: true});
                    return false;
            },
            delUser: function(e) {

            		LoadingDialog.render();

                    var id = $(e.currentTarget).attr('user');
                    Users.get(id).destroy({
                    	success: _.bind(function() {
                    		this.render();
                    	}, this)
                    });
                    return false;
            }
        });
	
        var HomeView = Backbone.View.extend({

        	el: $("#main-container"),
            homeTemplate: _.template($('#home-template').html()),

            events: {
            	"click .box":	"clickModule"
            },
            render: function() {
            	Menu.activateButton($('#menu-home'));
                        
                if(Modules.initialized == false) {
                        	
                	LoadingDialog.render();
                        	
                	Modules.loadModules({
                		success: _.bind(function() {
                			$(this.el).html(this.homeTemplate({modules: Modules.toJSON()}));
                        	LoadingDialog.dispose();
                		}, this)
                	});
                }
                else {
                	$(this.el).html(this.homeTemplate({modules: Modules.toJSON()}));
                }
            },
        	clickModule: function(e) {
        		var id = $(e.currentTarget).attr('module');
        		window.location.href = Modules.get(id).get('route');
        	}
        });

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
// views/menu.js
        /*******************************************
        * View of the menu
        *******************************************/
        var MenuView = Backbone.View.extend({

                el: $("#menu"),

                events: {
                        "click #menu-home":   	"displayHome",
                        "click #menu-users":  	"displayUsers",
                        "click #menu-modules": 	"displayModules"
                        
                },
                initialize: function() {
                },
                activateButton: function(btn) {
                        $("#menu li").removeClass("active");
                        btn.parent().addClass("active");
                },
                displayHome: function(e) {
                        Routes.navigate("", {trigger: true});
                },
                displayUsers: function(e) {
                        Routes.navigate("users", {trigger: true});
                },
                displayModules: function(e) {
                		Routes.navigate("modules", {trigger: true});
                }
        });

        var Menu = new MenuView;


        /*******************************************
        * View
        *******************************************/
        var UserAddView = Backbone.View.extend({

                el: $("#main-container"),
                userAddTemplate: _.template($('#useradd-template').html()),
                editingUser: null,

                events: {
                        "click #useradd-validate":      "validate",
                        "click #useradd-cancel":        "cancel"
                },
                render: function(editingUserId) {
                	
                		// Check wether modules have been loaded
                		if(Modules.initialized == false) {
                			LoadingDialog.render();
                			Modules.loadModules({
                				success: _.bind(function() {
                					this.render(editingUserId);
                					LoadingDialog.dispose();
                				}, this)
                			});
                			return false;
                		}
                	
                		// Does one edit an existing user?
                        if(editingUserId == null) {
				this.editingUser = null;
                                $(this.el).html(this.userAddTemplate({
                                	isEditing: false,
					user: this.editingUser,
                                	modules: Modules.toJSON()
                                }));
                        }
                        else {
                                if(this.editingUser == null) {
                                        if(Users.isEmpty() && !Users.initialized) {
                                                LoadingDialog.render();

                                                Users.loadUsers({
                                                        success: $.proxy(function() {
                                                                this.editingUser = Users.get(editingUserId);
                                                                $(this.el).html(this.userAddTemplate({
                                                                	isEditing: true, 
                                                                	user: this.editingUser.toJSON(),
                                                                	modules: Modules.toJSON()
                                                                }));
                                                                LoadingDialog.dispose();
                                                        }, this)
                                                });

                                                return true;
                                        }
                                        else {
                                                this.editingUser = Users.get(editingUserId);
                                        }
                                }

                                $(this.el).html(this.userAddTemplate({
                                	isEditing: true, 
                                	user: this.editingUser.toJSON(),
                                	modules: Modules.toJSON()
                                }));
                        }
                },
                validate: function() {
                        LoadingDialog.render();

                        var username = $('#username').val();
                        var password = $('#password').val();
                        var firstName = $('#firstName').val();
                        var isAdmin = $('#isAdmin').is(':checked');
                        
                        var modules = [];
                        
                        $('.user_rights[module]:checked').each(function() {
                        	modules.push($(this).attr('module'));
                        });

                        console.log(modules);

                        // Adding user
                        if(this.editingUser == null ) {

                                Users.create({
                                        username: username,
                                        password: password,
                                        firstName: firstName,
                                        isAdmin: isAdmin,
                                        modules: modules
                                },
                                {
                                        success: this.onSuccess
                                });
                        }
                        // Update one
                        else {
                                if(password.length == 0) {
                                        password = this.editingUser.toJSON().password;
                                }

                                this.editingUser.save({
                                        username: username,
                                        password: password,
                                        firstName: firstName,
                                        isAdmin: isAdmin,
                                        modules: modules
                                },
                                {
                                        success: this.onSuccess
                                });
                        }
                },
                cancel: function() {
                        Routes.navigate('users', {trigger: true});
                },
                onSuccess: function(model) {
                        console.log(model.toJSON());
                        LoadingDialog.dispose();
                        Routes.navigate('users', {trigger: true});
                }
        });

        
        
        

// router/router.js
        /*******************************************
        * Router
        *******************************************/
        var Router = Backbone.Router.extend({

                currentView: null,

                homeView:     new HomeView,
                userListView: new UserView,
                userAddView:  new UserAddView,
                moduleListView:	new ModuleView,
                moduleAddView:	new ModuleAddView,

                routes: {
                        "":                     	"home",
                        "users":          	        "usersHome",
                        "users/add":            	"usersAdd",
                        "users/edit/:id":	        "usersEdit",		
                        "modules":					"modulesHome",
                        "modules/add":				"modulesAdd",
                        "modules/edit/:id":			"modulesEdit",
                },

                render: function(view, opt1, opt2, opt3, opt4) {
                        this.currentView = view;
                        this.currentView.render(opt1, opt2, opt3, opt4);
                },
                home: function() {
                        this.render(this.homeView);
                },
                usersHome: function() {
                        this.render(this.userListView);
                },
                usersAdd: function() {
                        this.render(this.userAddView);
                },
                usersEdit: function(id) {
                        this.render(this.userAddView, id);
                },
                modulesHome: function() {
                	this.render(this.moduleListView);
                },
                modulesAdd: function() {
                	this.render(this.moduleAddView);
                },
                modulesEdit: function(id) {
                	this.render(this.moduleAddView, id);
                }
        });
        
        var Routes = new Router;
        Backbone.history.start({root: globals.rootUrl});        
});
