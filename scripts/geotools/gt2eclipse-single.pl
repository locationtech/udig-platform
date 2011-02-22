#!/usr/bin/perl -w

use strict;
use File::Find;
use Cwd;

($#ARGV > -1) || die("usage: gt2eclipse.pl <gt root>\n");

my $gt = $ARGV[0];
my $scripts = getcwd;

(-e $gt) || die("No such directory: $gt\n");

# 1. find all modules, and generate the plugin.xml and build.xml fragments, and
#  generate a build.properties
open(PROPS, ">build.properties");
print(PROPS "source.geotools.jar = ");

my $root = "module";
process();

$root = "ext";
process();

$root = "plugin";
process();

print(PROPS "\t dummy\n");
print(PROPS "output.geotools.jar = bin/");
close(PROPS);

# 2. generate the plugin.xml file

# write the header
open(OUT,">plugin.xml");
print(OUT "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
print(OUT "<?eclipse version=\"3.0\"?>\n");
print(OUT "<plugin\n");
print(OUT "\tid=\"org.geotools.gt2\"\n");
print(OUT "\tname=\"geotools\"\n");
print(OUT "\tversion=\"2.1.0\">\n");

# the body
print(OUT "\t<runtime>\n");
print(OUT "\t\t<library name=\"geotools.jar\">\n");
print(OUT "\t\t\t<export name=\"*\"/>\n");
print(OUT "\t\t</library>\n");

# merge in the fragments
my $part = "plugin.xml.part";
find(\&gather,"$gt/module");
find(\&gather,"$gt/plugin");
find(\&gather,"$gt/ext");

# write the footer
print(OUT "\t</runtime>\n");
print(OUT "</plugin>");
close(OUT);

# 3. merge the build.xml files

# write the header
open(OUT,">build.xml");
print(OUT "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
print(OUT "<project name=\"geotools\" basedir=\".\" default=\"build\">\n");
print(OUT "\t<target name=\"build\">\n");

# merge in incomplete
$part = "build.xml.part";
find(\&gather,"$gt/module");
find(\&gather,"$gt/plugin");
find(\&gather,"$gt/ext");

# write the footer
print(OUT "\t</target>\n");
print(OUT "</project>");
close(OUT);

sub process {
	if (-e "$gt/$root") {
		find(\&project, "$gt/$root");
	}
}

sub project {
  if ($_ =~ /^project.xml/ && !($_ =~ /\.svn/)) {
		my $path = $File::Find::name;
		$path = substr($path,0,rindex($path,"/"));
		my $name = substr($path,rindex($path,"/")+1);

		# call the ant script to generate the plugin and build xml fragments
		system("ant -f $scripts/gt2eclipse-single.xml -Dmodule.dir=$path\n");

		# add a source location to build.properties
		print(PROPS "\t$root/$name/src,\\\n");
		print(PROPS "\t$root/$name/test,\\\n");
  }
}

sub gather {
	if ($_ =~ /^$part/) {
		my $file = $File::Find::name;
		open(IN, "<$file");
		while(<IN>) {
			print(OUT $_);
		}
		close(IN);
		unlink($file);
	}
}
