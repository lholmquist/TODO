$( function() {
    var projectGet, tagGet;

    // Instantiate our pipeline
    var todo = aerogear.pipeline([
            {
                name: "tasks",
                settings: {
                    url: "/todo-server/rest/task"
                }
            },
            {
                name: "projects",
                settings: {
                    url: "/todo-server/rest/project"
                }
            },
            {
                name: "tags",
                settings: {
                    url: "/todo-server/rest/tag"
                }
            }
        ]),
        Tasks = todo.pipes[ "tasks" ],
        Projects = todo.pipes[ "projects" ],
        Tags = todo.pipes[ "tags" ],
        taskContainer = $( "#task-container" ),
        projectContainer = $( "#project-list" ),
        tagContainer = $( "#tag-list" );

    $( "#task-overlay" ).height( taskContainer.outerHeight() ).width( taskContainer.outerWidth() );
    $( "#project-overlay" ).height( projectContainer.outerHeight() ).width( projectContainer.outerWidth() );
    $( "#tag-overlay" ).height( tagContainer.outerHeight() ).width( tagContainer.outerWidth() );

    projectGet = Projects.read({
        ajax: {
            success: function( data, textStatus, jqXHR ) {
                $( "#project-loader" ).hide();
                updateProjectList( data );
            }
        }
    });

    tagGet = Tags.read({
        ajax: {
            success: function( data, textStatus, jqXHR ) {
                if ( data.length ) {
                    $( "#task-tag-column" ).empty();
                }
                $( "#tag-loader" ).hide();
                updateTagList( data );
            }
        }
    });

    // When both the available projects and available tags have returned, get the task data
    $.when( projectGet, tagGet, Tasks.read() ).done( function( g1, g2, g3 ) {
        $( "#task-loader" ).hide();
        updateTaskList();
    });

    // Initialize Color pickers
    $( ".color-picker" ).miniColors();

    // Initialize masked date input
    $( "#task-date" ).mask( "9999-99-99", { placeholder: " " } );

    // Event Bindings
    $( ".add-project, .add-tag, .add-task" ).on( "click", function( event ) {
        var target = $( event.target );
        target.parent().height( "100%" );
        target.slideUp( "slow" );
        target.next().slideDown( "slow" );
    });

    $( ".form-close" ).on( "click", function( event ) {
        hideForm( $( this ).closest( "div" ) );
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
            // all form id's start with add
            formType = form.attr( "name" ),
            formValid = true,
            isUpdate = false,
            data, hex, tags;
        form.find( "input" ).each( function() {
            if ( !$.trim( $( this ).val() ).length && this.type != "hidden" ) {
                formValid = false;
                return false;
            }
        });

        // Handle invalid form
        if ( !formValid ) {
            // Add some sort of visual feedback
        } else {
            data = form.serializeObject();
            if ( data.id && data.id.length ) {
                isUpdate = parseInt( data.id, 10 );
            }
            if ( data.style ) {
                hex = hex2rgb( data.style );
                data.style = formType + "-" + hex[ "red" ] + "-" + hex[ "green" ] + "-" + hex[ "blue" ];
            }
            switch ( formType ) {
                case "project":
                    Projects.save( data, {
                        ajax: {
                            success: function( data ) {
                                updateProjectList( [ data ], isUpdate );
                            }
                        }
                    } );
                    break;
                case "tag":
                    Tags.save( data, {
                        ajax: {
                            success: function( data ) {
                                updateTagList( [ data ], isUpdate );
                            }
                        }
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
                        ajax: {
                            success: function( data ) {
                                updateTaskList( [ data ], isUpdate );
                            }
                        }
                    });
                    break;
            }

            // Close the add form
            hideForm( $( this ).closest( "div" ) );
        }
    });

    // Delete Buttons
    $( ".todo-app" ).on( "click", ".btn.delete", function( event ) {
        var target = $( event.target ),
            dataTarget = target.closest( ".option-overlay" ),
            toRemove = dataTarget.parent(),
            success = function() {
                toRemove.remove();
            },
            options = {
                record: dataTarget.data( "id" ),
                ajax: {
                    success: success
                }
            };
        switch( dataTarget.data( "type" ) ) {
            case "project":
                Projects.del( options );
                break;
            case "tag":
                Tags.del( options );
                break;
            case "task":
                Tasks.del( options );
                break;
        }
    });

    // Edit Buttons
    $( ".todo-app" ).on( "click", ".btn.edit", function( event ) {
        var target = $( event.target ).closest( ".option-overlay" ),
            toEdit, rgb;
        switch( target.data( "type" ) ) {
            case "project":
                toEdit = findItemToEdit( target, Projects.data );
                rgb = toEdit.style.substr( toEdit.style.indexOf( "-" ) + 1 ).split( "-" );

                $( "#project-id" ).val( toEdit.id );
                $( "#project-name" ).val( toEdit.title );
                $( "#project-color" ).miniColors( "value", rgb2hex( rgb[ 0 ], rgb[ 1 ], rgb[ 2 ] ) );
                $( ".add-project" ).click();
                break;
            case "tag":
                toEdit = findItemToEdit( target, Tags.data );
                rgb = toEdit.style.substr( toEdit.style.indexOf( "-" ) + 1 ).split( "-" );

                $( "#tag-id" ).val( toEdit.id );
                $( "#tag-name" ).val( toEdit.name );
                $( "#tag-color" ).miniColors( "value", rgb2hex( rgb[ 0 ], rgb[ 1 ], rgb[ 2 ] ) );
                $( ".add-tag" ).click();
                break;
            case "task":
                
                break;
        }
    });

    // Helper Functions
    function updateTaskList( data, isUpdate ) {
        var taskList;

        taskList = _.template( $( "#task-tmpl" ).html(), { tasks: data ? data : Tasks.data, tags: Tags.data, projects: Projects.data } );
        $( "#task-loader" ).after( taskList );
        $( "#add-task" )[ 0 ].reset();
    }

    function updateProjectList( data, isUpdate ) {
        var projectList,
            projectSelect,
            styleList;

        styleList = parseClasses( data, "0.4" );
        if ( styleList.length ) {
            $( "head" ).append( "<style id='project-styles'>" + styleList + "</style>" );
        }

        projectList = _.template( $( "#project-tmpl" ).html(), { projects: data } );
        projectSelect = _.template( $( "#project-select-tmpl" ).html(), { projects: data } );
        if ( !isUpdate ) {
            $( "#project-loader" ).after( projectList );
            $( "#task-project-select" ).append( projectSelect );
        } else {
            $( ".project-overlay[data-id='" + isUpdate + "']" ).parent().replaceWith( projectList );
            $( "#task-project-select" ).children( "[value='" + isUpdate + "']" ).replaceWith( projectSelect );
        }
        $( "#add-project" )[ 0 ].reset();
    }

    function updateTagList( data, isUpdate ) {
        var i,
            tagList,
            tagSelect,
            styleList;

        styleList = parseClasses( data, "1" );
        if ( styleList.length ) {
            $( "head" ).append( "<style id='tag-styles'>" + styleList + "</style>" );
        }

        tagList = _.template( $( "#tag-tmpl" ).html(), { tags: data } );
        tagSelect = "";
        if ( data.length ) {
            for ( i = 0; i < data.length; i += 3 ) {
                tagSelect += _.template( $( "#tag-select-tmpl" ).html(), { tags: data.slice( i, i+3 ) } );
            }
        }
        $( "#tag-loader" ).after( tagList );
        if ( tagSelect.length ) {
            $( "#task-tag-column" ).append( tagSelect );
        }
        $( "#add-tag" )[ 0 ].reset();
    }

    function findItemToEdit( idElement, data ) {
        return data.filter( function( element, index ) {
            return element.id == idElement.data( "id" );
        })[ 0 ];
    }

    function hideForm( toHide ) {
        toHide.slideUp( "slow", function() {
            $( this ).hide( 0, function() {
                $( this ).height( "auto" );
            });
        });
        toHide.prev().slideDown( "slow" );
    }

    function parseClasses( data, alpha ) {
        var styleList = "";
        $.each( data, function() {
            var rgb = this.style.substr( this.style.indexOf( "-" ) + 1 ).split( "-" );
            styleList += "#property-container ." + this.style + "," + "#task-container ." + this.style + "{background: rgba(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + "," + alpha + ");}";
        });
        return styleList;
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