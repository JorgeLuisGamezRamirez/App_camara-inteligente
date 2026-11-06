FROM mcr.microsoft.com/devcontainers/java:1-17-bookworm

USER root
RUN apt-get update && apt-get install -y --no-install-recommends unzip wget && rm -rf /var/lib/apt/lists/*
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=$PATH:/opt/android-sdk/cmdline-tools/latest/bin:/opt/android-sdk/platform-tools
RUN mkdir -p /opt/android-sdk/cmdline-tools && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O /tmp/android-tools.zip && \
    unzip -q /tmp/android-tools.zip -d /tmp/android && \
    mv /tmp/android/cmdline-tools /opt/android-sdk/cmdline-tools/latest && \
    yes | sdkmanager --licenses >/dev/null && \
    sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0" && \
    chown -R vscode:vscode /opt/android-sdk && rm /tmp/android-tools.zip
USER vscode
