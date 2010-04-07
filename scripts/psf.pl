#!/usr/bin/perl -w

use strict;
use Properties;
#
# create a team project set file from the current contents of the repo
#

# svn repository url
# temporary manifest file
my $MANIFEST="manifest";
# psf version
my $PSFREF="0.9.3";
my $props = Properties->new();
$props->parse("build.properties");
my $repo = $props->get("svn.repo");

# write header
print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
print("<psf version=\"2.0\">\n");
print("\t<provider id=\"org.tigris.subversion.subclipse.core.svnnature\">\n");

process("plugins");
process("fragments");
process("features");

print("\t</provider>\n");
print("</psf>\n");

unlink($MANIFEST);

sub process {
	my $cat = $_[0];

	system("svn ls ${repo}/${cat} > ${MANIFEST}");
	open(MANIFEST, "<${MANIFEST}");

	while(<MANIFEST>) {
		chomp;
		$_ = substr($_,0,length($_)-1);
	
		add($cat,$_);
	}
	close(MANIFEST);
}

sub add {
	my $cat = $_[0];
	my $id	= $_[1];
	my $handle = $_[2];

	print("\t\t<project reference=\"${PSFREF},${repo}/${cat}/${id},${id}\"/>\n");
}




