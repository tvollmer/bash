#!/bin/bash

diskutil erasevolume HFS+ "ramdisk" `hdiutil attach -nomount ram://2097152`

