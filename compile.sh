#!/bin/sh
# executes compilation
main () {
	local dir="$(dirname "$0")";
	cd "$dir";
	mvn eclipse:clean;
	mvn eclipse:eclipse;
	mvn package;
}
set -u -e;
set -x;
main "$@";