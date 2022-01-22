#!/bin/bash

case $1 in
    
    run)
        echo "Starting development Server"
        gradle bootRun
        echo "Server Stopped"
        ;;
    
    build)
        echo "Building Backend project"
        gradle clean build
        echo "Build complete"
        ;;
    
    *)
        echo "Usage: sh BackendServer.sh <run|build>"
        ;;
esac