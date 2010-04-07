#!/usr/bin/perl -w

use strict;
use Properties;
use File::Find;

#
# Ensure that all entries in the repository are included in a feature.xml
#
my $props = Properties->new();
$props->parse("build.properties");

#my $repo = "http://svn.geotools.org/udig/trunk";
my $repo = $props->get("svn.repo");
my $manifest = "manifest";

# create repository manifest
system("svn ls ${repo}/plugins > $manifest");
system("svn ls ${repo}/fragments >> $manifest");

# checkout features
if (-e "features") {
	system("svn update features");
}
else {
	system("svn checkout ${repo}/features");
}

my @features = ();
find(\&ffind, "features");

my $nfeatures = @features;
open(MANIFEST, "<$manifest");
while(<MANIFEST>) {
	chomp;	
	my $plugin = substr($_,0,length($_)-1);	
	my $found = 0;	

	# look for the plugin in the features
	for (my $i = 0; $i < $nfeatures; $i++) {
		
		open(FEATURE, "<".$features[$i]);
		my @feature = <FEATURE>;	
		if (grep(/id="($plugin)"/, @feature)) {
			$found = 1;
		}
		close(FEATURE);
	}

	if (!$found) {
		print $plugin . "\n";
	}

}

unlink($manifest);

sub ffind {
	if ($_ =~ /^feature.xml/ && !($_ =~ /\.svn/)) {
		push(@features, $File::Find::name);
	}
}


