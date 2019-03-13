#!/bin/bash

set -e

OLD_FOLDER="$1"
NEW_FOLDER="$1-hg"

echo "OLD_FOLDER : $OLD_FOLDER"
echo "NEW_FOLDER : $NEW_FOLDER"

mv "$OLD_FOLDER" "$NEW_FOLDER"
tar -czvf "$NEW_FOLDER.tar.gz" "$NEW_FOLDER"
git-hg clone "$(pwd)/$NEW_FOLDER" "$OLD_FOLDER"
rm -rf "$NEW_FOLDER"

