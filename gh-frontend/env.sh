#!/bin/sh

# Ensure the script fails on any error
set -e

# Get the directory containing the built Angular files
ROOT_DIR=/usr/share/nginx/html

# Replace environment variables in the built Angular files
envsubst < ${ROOT_DIR}/assets/env.template.js > ${ROOT_DIR}/assets/env.js

# Remove the template file as it's no longer needed
rm ${ROOT_DIR}/assets/env.template.js

# Make sure nginx can read the files
chmod -R 755 ${ROOT_DIR}
