# Use a modern image with JDK 21 and SBT
FROM sbtscala/scala-sbt:eclipse-temurin-jammy-21.0.2_13_1.9.9_2.13.13

# Install dependencies for JavaFX (Graphics, Audio, Input)
RUN apt-get update && apt-get install -y \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libgtk-3-0 \
    libglu1-mesa \
    libxxf86vm1 \
    libasound2 \
    && rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /app

# Copy the project files
ADD . /app

# Default command
CMD ["sbt"]
