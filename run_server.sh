#!/usr/bin/env bash

/usr/bin/env /usr/lib/jvm/java-22-openjdk/bin/java --enable-preview -XX:+ShowCodeDetailsInExceptionMessages -cp ./bin server.Main $1
