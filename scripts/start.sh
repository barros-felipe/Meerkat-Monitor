#!/bin/bash
# Meerkat-Monitor startup script

echo ""
echo "Meerkat-Monitor - [http://meerkat-monitor.org]"
java -jar Meerkat-Monitor-daemon.jar &
echo "$!" > meerkat-monitor.pid

echo "Application started."
echo "[PID: "$!"]"
echo ""
