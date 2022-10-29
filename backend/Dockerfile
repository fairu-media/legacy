FROM azul/zulu-openjdk-debian:19-latest

RUN apt-get -qq update
RUN apt-get -qq -y --no-install-recommends install libargon2-dev

WORKDIR /app

COPY backend/bin /app/bin
COPY backend/lib /app/lib

ENTRYPOINT [ "/app/bin/backend" ]

