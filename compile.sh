#!/bin/sh
# executes compilation
main () {
	local dir="$(dirname "$0")";
	cd "$dir";
	#mvn package;
	mvn eclipse:clean;
	mvn eclipse:eclipse;
	mvn;
}
set -u -e;
set -x;
main "$@";