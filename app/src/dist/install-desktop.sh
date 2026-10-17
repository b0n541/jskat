#!/bin/sh
set -eu

DIST_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd -P)
APPLICATIONS_DIR="${XDG_DATA_HOME:-$HOME/.local/share}/applications"
DESKTOP_FILE="$APPLICATIONS_DIR/jskat.desktop"

mkdir -p "$APPLICATIONS_DIR"

EXEC_PATH=$(printf '%s' "$DIST_DIR/bin/JSkat" | sed 's/\\/\\\\/g; s/"/\\"/g')
ICON_PATH="$DIST_DIR/jskat.png"

cat > "$DESKTOP_FILE" <<EOF
[Desktop Entry]
Type=Application
Name=JSkat
Comment=Play Skat
Exec="$EXEC_PATH"
Icon=$ICON_PATH
Terminal=false
Categories=Game;CardGame;
StartupWMClass=org.jskat.JSkatFX
EOF

chmod 644 "$DESKTOP_FILE"
printf 'Installed desktop entry: %s\n' "$DESKTOP_FILE"
