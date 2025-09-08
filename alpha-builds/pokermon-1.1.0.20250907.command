#!/bin/bash
echo "Starting Pokermon v1.1.0.20250907..."
cd "$(dirname "$0")"
if [ -f "pokermon.jar" ]; then
    java -jar pokermon.jar "$@"
else
    echo "Error: pokermon.jar not found in the same directory"
    exit 1
fi
