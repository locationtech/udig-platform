#!/usr/bin/perl -w

# this deletes all build.xml files, even those under version control, ok
# because we update before running the next build which will recover them

use strict;
use File::Find;

(-e "build/plugins") || die("No plugins found, nothing to do.\n"); 

my $props = Properties->new();
print($props->get("buildDirectory")."\n");

find(\&clean, "build/plugins");
find(\&clean, "build/features");

sub clean () {
	if ($_ =~ /^build.xml$/) {
		unlink($File::Find::name);
	}
}

