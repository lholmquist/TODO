$( function() {
    var overlayTimer;

    // Instantiate our authenticator
    var restAuth = AeroGear.Auth({
        name: "auth",
        settings: {
            agAuth: true,
            baseURL: "/todo-server/"
        }
    }).modules.auth;

    // Instantiate our pipeline
    var todo = AeroGear.Pipeline([
            {
                name: "tasks",
                settings: {
                    baseURL: "/todo-server/",
                    authenticator: restAuth
                }
            },
            {
                name: "projects",
                settings: {
                    baseURL: "/todo-server/",
                    authenticator: restAuth
                }
            },
            {
                name: "tags",
                settings: {
                    baseURL: "/todo-server/",
                    authenticator: restAuth
                }
            }
        ]),
        Tasks = todo.pipes[ "tasks" ],
        Projects = todo.pipes[ "projects" ],
        Tags = todo.pipes[ "tags" ],
        taskContainer = $( "#task-container" ),
        projectContainer = $( "#project-list" ),
        tagContainer = $( "#tag-list" );

        //Creating the DataManagers:
        var dm = aerogear.dataManager([ "tasks", "tags", "projects", "filtered" ]),
        FilteredStore = dm.valves[ "filtered" ],
        TasksStore = dm.stores[ "tasks" ],
        ProjectsStore = dm.stores[ "projects" ],
        TagsStore = dm.stores[ "tags" ];

    // Loading overlays
    $( "#task-overlay" ).height( taskContainer.outerHeight() ).width( taskContainer.outerWidth() );
    $( "#project-overlay" ).height( projectContainer.outerHeight() ).width( projectContainer.outerWidth() );
    $( "#tag-overlay" ).height( tagContainer.outerHeight() ).width( tagContainer.outerWidth() );

    // Initializes all sections of the TODO app
    loadAllData();

    // Initialize Color pickers
    $( ".color-picker" ).miniColors();

    // Initialize masked date input
    $( "#task-date" ).mask( "9999-99-99", { placeholder: " " } );

    // Event Bindings
    $( ".add-project, .add-tag, .add-task" ).on( "click", function( event ) {
        var isAdmin = sessionStorage.getItem( "access" ) === "1" ? true : false;
        if ( restAuth.isAuthenticated() && ( isAdmin || $( this ).is( ".add-task" ) ) ) {
            var target = $( event.currentTarget );

            target.parent().height( "100%" );
            target.slideUp( "slow" );
            target.next().slideDown( "slow" );
        } else if ( restAuth.isAuthenticated() && !isAdmin ) {
            $( "#auth-error-box" ).modal();
        } else {
            loadAllData();
        }
    });

    $( ".form-close" ).on( "click", function( event ) {
        var $this = $( this ),
            form = $this.closest( "form" ),
            btn = form.find( ".submit-btn" ),
            plus = '<i class="icon-plus-sign"></i>';
        switch( form.attr( "name" ) ) {
            case "project":
                btn.html( plus + " Add Project" );
                break;
            case "tag":
                btn.html( plus + " Add Tag" );
                break;
            case "task":
                btn.html( plus + " Add Task" );
                break;
        }
        hideForm( $this.closest( "div" ) );
    });

    // Handle form submissions
    $( ".submit-btn" ).on( "click", function( event ) {
        event.preventDefault();
        $( this ).closest( "form" ).submit();
    });

    $( "form" ).on( "submit", function( event ) {
        event.preventDefault();

        // Validate form
        var form = $( this ),
            formType = form.attr( "name" ),
            formValid = true,
            // 0 - add items
            isUpdate = false,
            plus = '<i class="icon-plus-sign"></i>',
            filteredData = [],
            data, hex, tags, errorElement;

        form.find( "input" ).each( function() {
            if ( !$.trim( $( this ).val() ).length && this.type != "hidden" && this.name != "date") {
                    formValid = false;
                    errorElement = $( this );
                    return false;
            }
        });

        // Handle invalid form
        if ( !formValid ) {
            errorElement.addClass( "form-error" ).val( "Field may not be empty" );
        } else {
            data = form.serializeObject();
            if ( data.id && data.id.length ) {
                // 1 - replace items in list
                isUpdate = true;
            } else {
                delete data.id;
            }
            if ( data.style ) {
                hex = hex2rgb( data.style );
                data.style = formType + "-" + hex[ "red" ] + "-" + hex[ "green" ] + "-" + hex[ "blue" ];
            }
            if ( data.project && data.project.length ) {
                data.project = parseInt( data.project, 10 );
            } else {
                delete data.project;
            }
            switch ( formType ) {
                case "project":
                    Projects.save( data, {
                        success: function( data ) {
                            updateProjectList();
                            if ( isUpdate ) {
                                updateTaskList();
                            }

                            $( "#add-project" ).find( ".submit-btn" ).html( plus + " Add Project" );
                        },
                        statusCode: {
                            401: function( jqXHR ) {
                                $( "#auth-error-box" ).modal();
                            }
                        },
                        stores: ProjectsStore
                    } );
                    break;
                case "tag":
                    Tags.save( data, {
                        success: function( data ) {
                            updateTagList();
                            if ( isUpdate ) {
                                updateTaskList();
                            }
                            $( "#add-tag" ).find( ".submit-btn" ).html( plus + " Add Tag" );
                        },
                        statusCode: {
                            401: function( jqXHR ) {
                                $( "#auth-error-box" ).modal();
                            }
                        },
                        stores: TagsStore
                    } );
                    break;
                case "task":
                    tags = $.map( data, function( value, key ) {
                        if ( key.indexOf( "tag-" ) >= 0 ) {
                            delete data[ key ];
                            return parseInt( value, 10 );
                        } else {
                            return null;
                        }
                    });
                    data.tags = tags;
                    Tasks.save( data, {
                        success: function( data ) {
                            updateTaskList();
                            $( "#add-task" ).find( ".submit-btn" ).html( plus + " Add Task" );
                        },
                        statusCode: {
                            401: function( jqXHR ) {
                                $( "#auth-error-box" ).modal();
                            }
                        },
                        stores: TasksStore
                    });
                    break;
                case "login":
                    // Reset session storage
                    sessionStorage.removeItem( "username" );
                    sessionStorage.removeItem( "access" );

                    restAuth.login( JSON.stringify( data ), {
                        contentType: "application/json",
                        dataType: "json",
                        success: function( data ) {
                            var role = $.inArray( "admin", data.roles ) >= 0 ? 1 : 0;
                            sessionStorage.setItem( "username", data.username );
                            sessionStorage.setItem( "access", role );

                            $( "#login-box" ).modal( "hide" );
                            loadAllData();
                        },
                        error: function( data ) {
                            $( "#login-msg" ).text( "Login failed, please try again" );
                        }
                    });
                    break;
                case "register":
                    // Reset session storage
                    sessionStorage.removeItem( "username" );
                    sessionStorage.removeItem( "access" );

                    restAuth.enroll( JSON.stringify( data ), {
                        contentType: "application/json",
                        dataType: "json",
                        success: function( data ) {
                            var role = $.inArray( "admin", data.roles ) >= 0 ? 1 : 0;
                            sessionStorage.setItem( "username", data.username );
                            sessionStorage.setItem( "access", role );

                            $( "#register-box" ).modal( "hide" );
                            $( "#login-btn" ).show();
                            loadAllData();
                        },
                        error: function( data ) {
                            console.log( data );
                        }
                    });
                    break;
            }

            // Close the add form
            if ( !form.is( ".auth-form" ) ) {
                hideForm( form.closest( "div" ) );
            }
        }
    });

    // Clear error fields
    $( "form" ).on( "focus", ".form-error", function( event ) {
        $( this ).removeClass( "form-error" ).val( "" );
    });

    // Item Hover Menus
    $( ".todo-app" )
        .on( "mouseenter", ".project, .tag, .task", function( event ) {
            if ( sessionStorage.getItem( "access" ) === "1" || $( this ).is( ".task" ) ) {
                var overlay = $( event.target ).children( ".option-overlay" ).eq( 0 );

                // Delay clicking of buttons in the overlay to prevent accidental clicks on touch devices
                overlay.data( "clickable", false );
                setTimeout( function() { overlay.data( "clickable", true ); }, 500 );

                // Show the overlay if not already visible
                if ( !overlay.is( ":visible" ) ) {
                    overlay.show();
                }
            }
        })
        .on( "mouseleave", ".project, .tag, .task", function( event ) {
            if ( sessionStorage.getItem( "access" ) === "1" || $( this ).is( ".task" ) ) {
                var overlay = $( event.target ).closest( ".project, .tag, .task" ).children( ".option-overlay" ).eq( 0 );
                // Add a delay for touch devices to allow clicking of buttons before the overlay disappears
                if ( Modernizr.touch ) {
                    setTimeout( function() { overlay.hide(); }, 500 );
                } else {
                    overlay.hide();
                }
            }
        });

    // Delete Buttons
    $( ".todo-app" ).on( "click", ".btn.delete", function( event ) {
        var target = $( event.target ),
            dataTarget = target.closest( ".option-overlay" ),
            type = dataTarget.data( "type" ),
            toRemove = dataTarget.parent(),
            current,
            success = function( data ) {
                for ( var item in data ) {
                    current = filterData( data[ item ], TasksStore.getData() )[ 0 ];
                    if ( type == "project" ) {
                        current.project = null;
                    } else if ( type == "tag" ) {
                        current.tags.splice( item, 1 );
                    }
                }
                if ( type == "project" ) {
                    updateProjectList();
                } else if ( type == "tag" ) {
                    updateTagList();
                }
                updateTaskList();
            },
            statusCode = {
                401: function( jqXHR ) {
                    $( "#auth-error-box" ).modal();
                }
            },
            options = {
                success: success,
                statusCode: statusCode
            };
        if ( !dataTarget.data( "clickable" ) ) {
            event.preventDefault();
            return;
        }
        switch( type ) {
            case "project":
                options.stores = ProjectsStore;
                Projects.remove( dataTarget.data( "id" ), options );
                break;
            case "tag":
                options.stores = TagsStore;
                Tags.remove( dataTarget.data( "id" ), options );
                break;
            case "task":
                options.stores = TasksStore;
                Tasks.remove( dataTarget.data( "id" ), options );
                break;
        }
    });

    // Edit Buttons
    $( ".todo-app" ).on( "click", ".btn.edit", function( event ) {
        var target = $( event.target ).closest( ".option-overlay" ),
            plus = '<i class="icon-plus-sign"></i>',
            toEdit, rgb;

        if ( !target.data( "clickable" ) ) {
            event.preventDefault();
            return;
        }

        switch( target.data( "type" ) ) {
            case "project":
                toEdit = filterData( target, ProjectsStore.getData() )[ 0 ];
                if ( toEdit.style ) {
                    rgb = toEdit.style.substr( toEdit.style.indexOf( "-" ) + 1 ).split( "-" );
                } else {
                    rgb = [ 255, 255, 255 ];
                }

                $( "#project-id" ).val( toEdit.id );
                $( "#project-title" ).val( toEdit.title );
                $( "#project-color" ).miniColors( "value", rgb2hex( rgb[ 0 ], rgb[ 1 ], rgb[ 2 ] ) );
                $( "#add-project" ).find( ".submit-btn" ).html( plus + " Update Project" );
                $( ".add-project" ).click();
                break;
            case "tag":
                toEdit = filterData( target, TagsStore.getData() )[ 0 ];
                if ( toEdit.style ) {
                    rgb = toEdit.style.substr( toEdit.style.indexOf( "-" ) + 1 ).split( "-" );
                } else {
                    rgb = [ 255, 255, 255 ];
                }

                $( "#tag-id" ).val( toEdit.id );
                $( "#tag-title" ).val( toEdit.title );
                $( "#tag-color" ).miniColors( "value", rgb2hex( rgb[ 0 ], rgb[ 1 ], rgb[ 2 ] ) );
                $( "#add-tag" ).find( ".submit-btn" ).html( plus + " Update Tag" );
                $( ".add-tag" ).click();
                break;
            case "task":
                toEdit = filterData( target, TasksStore.getData() )[ 0 ];

                $( "#task-id" ).val( toEdit.id );
                $( "#task-title" ).val( toEdit.title );
                $( "#task-date" ).val( toEdit.date );
                $( "#task-desc" ).val( toEdit.description );
                if ( toEdit.project ) {
                    $( "#task-project-select" ).val( toEdit.project );
                }

                // Reset tag checkboxes
                $( "#add-task input:checkbox" ).prop( "checked", false );

                if ( toEdit.tags && toEdit.tags.length ) {
                    $.each( toEdit.tags, function( index, value ) {
                        $( "input[name='tag-" + value + "']" ).prop( "checked", true );
                    });
                }
                $( "#add-task" ).find( ".submit-btn" ).html( plus + " Update Task" );
                $( ".add-task" ).click();
                break;
        }
    });

    //Filter Buttons
    $( ".todo-app" ).on( "click", " .btn.sort ", function( event ) {
        var target = $( event.target ).closest( ".option-overlay" ),
        filteredData;

        switch( target.data( "type" ) ){
            case "project":
                filteredData = TasksValve.filter( {  "project" : target.data( "id" ) } );
                break;
            case "tag":
                //filteredData = TasksValve.filter( { "tags" : { data : [ target.data( "id" ) ] } } );
                break;
        }
        
        FilteredValve.save( filteredData, true );
        updateTaskList( FilteredValve.data );

    });

    // Show all tags and projects on touch devices
    if ( Modernizr.touch ) {
        $( "#project-list, #tag-list" ).css( "max-height", "none" );
    }

    // Register Button
    $( "#register-button" ).click( function( event ) {
        event.preventDefault();

        var regBox = $( "#register-box" );

        $( "#login-box" ).modal( "hide" );
        regBox.find( "form" )[ 0 ].reset();
        regBox.modal({
            backdrop: "static",
            keyboard: false
        });
        $( "#login-btn" ).show();
    });

    // Logout button
    $( "#userinfo-msg" ).on( "click", ".btn", function( event ) {
        event.preventDefault();

        restAuth.logout({
            success: function() {
                $( "#userinfo-msg" ).hide();
                $( ".form-close:visible" ).click();
                loadAllData();
            },
            error: function() {
                console.log('logout error');
            }
        });
    });

    // Login Cancel
    $( "#login-cancel" ).click( function( event ) {
        event.preventDefault();

        $( ".loader" ).hide();
        $( "#login-btn" ).show();
        $( "#login-box" ).modal( "hide" );
    });

    // Login Cancel
    $( "#register-cancel" ).click( function( event ) {
        event.preventDefault();

        $( ".loader" ).hide();
        $( "#login-btn" ).show();
        $( "#register-box" ).modal( "hide" );
    });

    // Login button
    $( "#login-btn" ).click( function( event ) {
        loadAllData();
    });

    // Auth Error Close button
    $( "#auth-error-box" ).on( "click", ".close", function( event ) {
        event.preventDefault();

        $( "#auth-error-box" ).modal( "hide" );
    });

    // Helper Functions
    function loadAllData() {
        var projectGet, tagGet;

        projectGet = Projects.read({
            complete: function() {
                $( "#project-loader" ).hide();
                updateProjectList();
            },
            //setting the store
            stores: ProjectsStore
        });

        tagGet = Tags.read({
            complete: function( data, textStatus, jqXHR ) {
                if ( data && data.length ) {
                    $( "#task-tag-column" ).empty();
                }
                $( "#tag-loader" ).hide();
                updateTagList();
            },
            //setting the store
            stores: TagsStore
        });

        // When both the available projects and available tags have returned, get the task data
        $.when( projectGet, tagGet, Tasks.read( { stores: [ TasksStore, FilteredValve ] } ) ).done( function( g1, g2, g3 ) {

            $( "#userinfo-name" ).text( sessionStorage.getItem( "username" ) );
            $( "#userinfo-msg" ).show();
        })
        .fail( function() {
            restAuth.deauthorize();
            $( "#login-box" ).modal({
                backdrop: "static",
                keyboard: false
            });
        })
        .always( function() {
            $( "#task-loader, #login-btn" ).hide();
            updateTaskList();
        });
    }


    function updateTaskList( filtered ) {
        var taskList = _.template( $( "#task-tmpl" ).html(), { tasks: filtered || TasksStore.data, tags: TagsStore.data, projects: ProjectsStore.data } );


        $( "#task-list-container" ).html( taskList );

        // Enable tooltips
        $( "#task-list-container .swatch" ).tooltip();
    }

    function updateProjectList() {
        var projectList = "",
            projectSelect = "",
            styleList = "";

        styleList = parseClasses( ProjectsStore.getData() );
        $( "#project-styles" ).html( styleList );

        projectList = _.template( $( "#project-tmpl" ).html(), { projects: ProjectsStore.getData() } );
        projectSelect = _.template( $( "#project-select-tmpl" ).html(), { projects: ProjectsStore.getData() } );
        $( "#project-container" ).html( projectList );
        projectSelect = '<option value="">No Project</option>' + projectSelect;
        $( "#task-project-select" ).html( projectSelect );
    }

    function updateTagList() {
        var i,
            tagList = "",
            tagSelect = "",
            styleList = "";

        styleList = parseClasses( TagsStore.getData(), "1" );
        $( "#tag-styles" ).html( styleList );

        tagList = _.template( $( "#tag-tmpl" ).html(), { tags: TagsStore.getData()} );
        tagSelect = "";
        if ( TagsStore.getData() && TagsStore.getData().length ) {
            for ( i = 0; i < TagsStore.getData().length; i += 3 ) {
                tagSelect += _.template( $( "#tag-select-tmpl" ).html(), { tags: TagsStore.getData().slice( i, i+3 ) } );
            }
        }
        $( "#tag-container" ).html( tagList );
        if ( !$.trim( tagSelect ).length ) {
            tagSelect = "<strong>No Tags Available</strong>";
        }
        $( "#task-tag-column" ).html( tagSelect );
    }

    function filterData( idElement, data ) {
        var checkId;
        return data.filter( function( element, index ) {
            checkId = idElement.data ? idElement.data( "id" ) : idElement;
            return element.id === checkId;
        });
    }

    function hideForm( toHide ) {
        var form = toHide.find( "form" );
        toHide.slideUp( "slow", function() {
            $( this ).hide( 0, function() {
                $( this ).height( "auto" );
            });
        });
        toHide.prev().slideDown( "slow" );
        form
            .find( "input[name='id']" ).val( "" ).end()
            .find( ".color-picker" ).miniColors( "value", "#ffffff" );

        form[ 0 ].reset();
    }

    function parseClasses( data ) {
        var styleList = "",
            rgb,
            fontColor;
        if ( data ) {
            $.each( data, function() {
                if ( this.style ) {
                    rgb = this.style.substr( this.style.indexOf( "-" ) + 1 ).split( "-" );
                    fontColor = calcBrightness( rgb ) < 130 ? "#EEE" : "#222" ;
                    styleList += "#property-container ." + this.style + "," + "#task-container ." + this.style + "{background: rgba(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ",1); color: " + fontColor + ";}";
                }
            });
        }
        return styleList;
    }

    function calcBrightness( rgb ) {
        return Math.round( Math.sqrt(
            ( parseInt( rgb[ 0 ], 10 ) * parseInt( rgb[ 0 ], 10 ) * 0.241 ) +
            ( parseInt( rgb[ 1 ], 10 ) * parseInt( rgb[ 1 ], 10 ) * 0.691 ) +
            ( parseInt( rgb[ 2 ], 10 ) * parseInt( rgb[ 2 ], 10 ) * 0.068 )
        ));
    }

    function hex2rgb(hex) {
        if ( hex[0] == "#" ) {
            hex = hex.substr( 1 );
        }
        if ( hex.length == 3 ) {
            var temp = hex; hex = "";
            temp = /^([a-f0-9])([a-f0-9])([a-f0-9])$/i.exec( temp ).slice( 1 );
            for ( var i=0; i < 3; i++ ) {
                hex += temp[ i ] + temp[ i ];
            }
        }
        if ( hex.length != 6 ) {
            return {
                red: 255,
                green: 255,
                blue: 255
            };
        }
        var triplets = /^([a-f0-9]{2})([a-f0-9]{2})([a-f0-9]{2})$/i.exec( hex ).slice( 1 );
        return {
            red:   parseInt( triplets[ 0 ], 16 ),
            green: parseInt( triplets[ 1 ], 16 ),
            blue:  parseInt( triplets[ 2 ], 16 )
        };
    }

    function rgb2hex( r, g, b ) {
        return toHex( r ) + toHex( g ) + toHex( b );
    }
    function toHex( n ) {
        if ( n === null ) {
            return "00";
        }
        n = parseInt( n, 10 );
        if ( n === 0 || isNaN( n ) ) {
            return "00";
        }
        n = Math.max( 0, n );
        n = Math.min( n, 255 );
        n = Math.round( n );
        return "0123456789ABCDEF".charAt( ( n - n % 16 ) / 16 ) + "0123456789ABCDEF".charAt( n % 16 );
    }

    function isArray( obj ) {
        return ({}).toString.call( obj ) === "[object Array]";
    }

    // Serializes a form to a JavaScript Object
    $.fn.serializeObject = function() {
        var o = {};
        var a = this.serializeArray();
        $.each( a, function() {
            if ( o[ this.name ] ) {
                if ( !o[ this.name ].push ) {
                    o[ this.name ] = [ o[ this.name ] ];
                }
                o[ this.name ].push( this.value || '' );
            } else {
                o[ this.name ] = this.value || '';
            }
        });
        return o;
    };
});
