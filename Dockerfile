# -----------------------------
# Builder stage
# -----------------------------
# Uses the official Maven image with Eclipse Temurin Java 21 on Alpine.
# This stage is only used to compile/package the application.
# Maven and build tools stay in this stage and are not included in the final runtime image.
FROM maven:3.9.15-eclipse-temurin-21-alpine AS builder

# Set the application source directory inside the container.
WORKDIR /usr/src/app

# Copy custom Maven settings.
# This is useful if the build depends on internal repositories, mirrors, credentials, or proxy config.
COPY settings-docker.xml /usr/share/maven/ref/settings-docker.xml

# Copy only the source files/modules needed for the Maven build.
# Keeping this explicit avoids copying unnecessary files into the build context.
COPY pom.xml /usr/src/app/
COPY commons/ /usr/src/app/commons/
COPY test-commons/ /usr/src/app/test-commons/
COPY converter/ /usr/src/app/converter/
COPY commandline/ /usr/src/app/commandline/
COPY rest-api/ /usr/src/app/rest-api/
COPY generate/ /usr/src/app/generate/
COPY test-coverage/ /usr/src/app/test-coverage/
COPY tools/docker/docker-artifacts/ /usr/src/app/tools/docker/docker-artifacts/

# Build the application.
# -B runs Maven in batch mode for CI/CD.
# -ntp disables Maven transfer progress logs.
# -s uses the custom Maven settings file copied above.
# Tests, JaCoCo, and generate steps are skipped because this Docker build only packages the runtime artifact.
RUN mvn -B -ntp \
    -s /usr/share/maven/ref/settings-docker.xml \
    clean install \
    -Dmaven.test.skip=true \
    -Djacoco.skip=true \
    -Dskip.generate=true > /dev/null


# -----------------------------
# Final runtime stage
# -----------------------------
# Uses a smaller Alpine-based Java 21 JRE image for runtime.
# This helps reduce image size and avoids the Ubuntu OS package vulnerabilities reported by Snyk.
FROM eclipse-temurin:21.0.10_7-jre-alpine-3.23

# Set the directory where the application will run.
WORKDIR /usr/src/run/

# Copy only the runtime artifacts from the builder stage.
# This keeps Maven, source code, and build dependencies out of the final image.
COPY --from=builder /usr/src/app/tools/docker/docker-artifacts /usr/src/run/
COPY --from=builder /usr/src/app/rest-api/target/rest-api.jar /usr/src/run/

# Patch Alpine packages and make the startup script executable.
# Bash is installed only when qppConverter.sh explicitly uses a bash shebang.
# This keeps the runtime image minimal while still supporting scripts that require /bin/bash.
RUN apk upgrade --no-cache \
    && if head -n 1 /usr/src/run/qppConverter.sh | grep -q "bash"; then apk add --no-cache bash; fi \
    && chmod +x /usr/src/run/qppConverter.sh

# Application listens on 8443.
EXPOSE 8443

# Start the application using the existing startup script.
CMD ["/usr/src/run/qppConverter.sh"]