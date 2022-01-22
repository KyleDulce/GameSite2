#!/bin/bash

case $1 in

    setup)
        echo "Checking requirements"

        requireMet=true
        
        if ! command -v node &> /dev/null 
        then
            echo "Node is not found. Did you add it to PATH?"
            requireMet=false
        fi

        if ! command -v gradle &> /dev/null 
        then
            echo "Gradle is not found. Did you add it to PATH?"
            requireMet=false
        fi

        if [ "$requireMet" = false ]
        then 
            echo "Requirements not met, shutting down"
            exit 1
        fi

        echo "Setting up enviroment"
        (cd GameSiteClient; sh GameSiteClient.sh install)
        echo "Setup Complete"
        ;;
    
    start-dev-server)
        echo "Starting up development servers"
        if [[ "$OSTYPE" == "msys" ]]
        then
            echo "Windows not supported"
            exit 1
        fi

        start cmd /k "cd GameSiteClient && bash GameSiteClient.sh run"
        start cmd /k "cd Server && bash BackendServer.sh run"
        echo "Development server started"
        ;;

    start-qa-server)
        echo "Starting up qa server"
        (cd GameSiteClient; sh GameSiteClient.sh build)
        echo "Copying files"
        cp -r GameSiteClient/dist/game-site-client/* Server/src/main/resources/static
        (cd Server; sh BackendServer.sh run)
        echo "Qa server ended"
        ;;
    
    build)
        echo "Buildering Server"
        (cd GameSiteClient; sh GameSiteClient.sh build)
        echo "Copying files"
        cp -r GameSiteClient/dist/game-site-client/* Server/src/main/resources/static
        (cd Server; sh BackendServer.sh build)
        echo "Server built"
        ;;

    *)
        echo "Usage: sh Gamesite.sh <setup|start-dev-server|start-qa-server|build>"
        ;;

esac




#Setup front end enviroment
