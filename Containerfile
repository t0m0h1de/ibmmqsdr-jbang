ARG jdk_version=21

FROM quay.io/quarkus/ubi-quarkus-mandrel-builder-image:jdk-${jdk_version}

ARG jbang_version=0.106.1

USER 0

WORKDIR /tmp/jbang-${jbang_version}-installation

RUN curl -Ls "https://github.com/jbangdev/jbang/releases/download/v${jbang_version}/jbang-${jbang_version}.zip" --output jbang-${jbang_version}.zip && \
    unzip jbang-${jbang_version}.zip && \
    rm jbang-${jbang_version}.zip && \
    chmod +x jbang-${jbang_version}/bin/jbang && \
    mv jbang-${jbang_version}/bin/* /usr/local/bin/

WORKDIR /project

ENTRYPOINT ["jbang"]