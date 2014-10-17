#!/bin/bash
DEPLOY_DIR=$(dirname $0)/..
while [[ $# > 1 ]]
do
key="$1"
shift

case $key in
    -a)
    APP="$1"
    shift
    ;;
    -p)
    PROXY="$1"
    shift
    ;;
    -c)
    CONF="$1"
    shift
    ;;
    -l)
    LOG="$1"
    shift
    ;;
    --default)
    DEFAULT=YES
    shift
    ;;
    *)
            # unknown option
    ;;
esac
done

if [ -z "$APP" ]; then
        APP=$DEPLOY_DIR/conf/queue.properties
fi

if [ -z "$LOG" ]; then
        LOG=$DEPLOY_DIR/conf/log4j.properties
fi

f=$APP
if [ -d "$f" ]; then f=$f/.; fi
absolute=$(cd "$(dirname -- "$f")"; printf %s. "$PWD")
absolute=${absolute%?}
absolute=$absolute/${f##*/}
APP=$absolute

f=$LOG
if [ -d "$f" ]; then f=$f/.; fi
absolute=$(cd "$(dirname -- "$f")"; printf %s. "$PWD")
absolute=${absolute%?}
absolute=$absolute/${f##*/}
LOG=$absolute
 
exec java -Xms1G -Xmx1G -Dlog4j.configuration=file:$LOG -cp $DEPLOY_DIR/lib/http-proxy-1.0-SNAPSHOT.jar com.lunex.httpproxy.QueueLauncher -a $APP
