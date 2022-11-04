### builder

FROM azul/zulu-openjdk-debian:19-latest AS builder

# Set working directory of builder
WORKDIR /build/fairu

# Copy over all files.
COPY . .

#
RUN chmod +x ./gradlew

#
RUN ./gradlew installDist --stacktrace --no-daemon

### RUNNER

# Use
FROM azul/zulu-openjdk-debian:19-latest

# Install argon2 natives & fontconfig
RUN apt-get -qq update
RUN apt-get -qq -y --no-install-recommends install libargon2-dev fontconfig

# Set working directory.
WORKDIR /app

# Copy Fairu Backend
COPY --from=builder '/build/fairu/build/install/fairu' .

# Set User for Security Reasons
USER 1001

ENTRYPOINT [ "/app/bin/fairu" ]
