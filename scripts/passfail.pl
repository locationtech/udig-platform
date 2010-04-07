#!/usr/bin/perl -w

use strict;
use Properties;

my $props = Properties->new();
$props->parse("build.properties");
my $log = $props->get("build.log");
(-e $log) || die("No build log found: $log\n");

open(LOG, "<$log");
my @log = <LOG>;
close(LOG);

# if string ERROR found, fail
if (grep (/ERROR/, @log)) {
	# exit with an error code to signal fail to caller
	exit(-1);
}

