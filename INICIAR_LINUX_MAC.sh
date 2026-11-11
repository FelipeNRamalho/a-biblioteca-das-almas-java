#!/usr/bin/env sh
set -e
cd "$(dirname "$0")"
if [ -f "dist/BibliotecaDasAlmas-Menu.jar" ]; then
  exec java -jar dist/BibliotecaDasAlmas-Menu.jar
elif [ -f "dist/biblioteca-das-almas-menu-1.0.0.jar" ]; then
  exec java -jar dist/biblioteca-das-almas-menu-1.0.0.jar
fi
