#!/bin/bash

case $1 in

    install)
        echo "Installing dependancies"
        npm i -g @angular/cli@13.1.3
        echo "Angular added to global npm"
        npm install
        echo "Npm Installed"
        ;;
    
    run)
        echo "Starting development Server"
        ng serve
        echo "Server Stopped"
        ;;
    
    build)
        echo "Building Frontend project"
        ng build
        echo "Build complete"
        ;;
    
    *)
        echo "Usage: sh GameSiteClient.sh <install|run|build>"
        ;;
esac