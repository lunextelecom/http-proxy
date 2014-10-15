#!/bin/bash
DEPLOY_DIR=$(dirname $0)/..
PARGS=$@
APP=$(echo "$PARGS" | sed -r 's/.*-a ([^ ]+).*/\1/g')
CONF=$(echo "$PARGS" | sed -r 's/.*-c ([^ ]+).*/\1/g')


if [ -z "$APP" ]; then
        APP=$DEPLOY_DIR/conf/app.properties
fi
if [ -z "$CONF" ]; then
        CONF=$DEPLOY_DIR/conf/configuration.yaml
fi


f=$APP
if [ -d "$f" ]; then f=$f/.; fi
absolute=$(cd "$(dirname -- "$f")"; printf %s. "$PWD")
absolute=${absolute%?}
absolute=$absolute/${f##*/}
APP=$absolute

f=$CONF
if [ -d "$f" ]; then f=$f/.; fi
absolute=$(cd "$(dirname -- "$f")"; printf %s. "$PWD")
absolute=${absolute%?}
absolute=$absolute/${f##*/}
CONF=$absolute

java -jar -Xms2500m -Xmx2500m $DEPLOY_DIR/lib/http-proxy-1.0-SNAPSHOT.jar -a $APP -c $CONF
