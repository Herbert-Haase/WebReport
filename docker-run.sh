#!/bin/bash

# Build the image
sudo docker build -t webreport:v1 .

# Setup X11 permissions
xhost +local:root

# Remove existing container if it's already running
echo "Cleaning up old containers..."
sudo docker rm -f webreport-instance 2>/dev/null

# Run the container
echo "Starting WebReport..."
sudo docker run -it \
  --env="DISPLAY" \
  --env="QT_X11_NO_MITSHM=1" \
  --volume="/tmp/.X11-unix:/tmp/.X11-unix:rw" \
  --name webreport-instance \
  webreport:v1
