#!/usr/bin/perl

#looks at every plugin.xml and creates an nl1 fragment if one doesn't already exist.
#ignores paths that have "test" in them, so make sure you are not working on something

#this script is hacky and only works when udig is checked out at : ~/udig
#alternatively, changed the hack in moveProperties()

#author rgould

#note that this does not do build.properties..

#
# NOTE THIS WILL OVER RIDE PROPERTIES FILES IN THE FRAGMENTS
#

use File::Find;

find (\&makeFragment, "../plugins");
find (\&moveProperties, "../plugins");

sub makeFragment() {
    if ($_ =~ /^plugin.xml$/ && !($File::Find::name =~ /test/)) {
	my $id;
	my $plugin;
	open (FILE, $_);
	while (defined($line = <FILE>) && !$id) {
	    if ($line =~ /id[\s]*=[\s]*\"(.*)\"/) {
		$plugin = $1;
		$id = $1.".nl1";
	    }
	}
	close(FILE);

	my $dir = "../../fragments/".$id;

	if (opendir (FRAG_DIR, $dir)) {

	} else {
	    mkdir ($dir, 0777) || die "cannot make dir $dir: $!";
	    opendir (FRAG_DIR, $dir);
#	    print "Fragment $id created.\n";
#	    print "svn add -N $dir\n";
	    system("svn add -N $dir");
	}

	if (open (FRAG_XML, $dir."/fragment.xml")) {

	} else {
#	    print "Fragment.xml for $dir not found.. making\n";
	    open (FRAG_XML, ">$dir/fragment.xml") || die "Cannot write to $dir/fragment.xml: $!";

#	    print "svn add $dir/fragment.xml\n";
	    system("svn add $dir/fragment.xml");

	    print FRAG_XML <<END_of_fragment;
<?xml version="1.0" encoding="UTF-8"?>
<?eclipse version="3.0"?>
<fragment
   id="$id"
   name="$plugin Nl1 Fragment"
   version="1.0.0"
   provider-name="Refractions Research, Inc."
   plugin-id="$plugin"
   plugin-version="1.0.0"
   match="greaterOrEqual">

   <runtime>
      <library name="nl1.jar" type="resource">
         <export name="*"/>
      </library>
      <library name="\$nl\$/">
         <export name="*"/>
      </library>
   </runtime>

</fragment>
END_of_fragment

	}

	close(FRAG_XML);
	my $project = $dir."/.project";
	    if (open(PROJECT, $project)) {
	    } else {
		open (PROJECT, ">$project") || die "Cannot write to $project: $!";

#		print "svn add $dir/.project\n";
		system("svn add $dir/.project");

		print PROJECT <<END_of_project;
<?xml version="1.0" encoding="UTF-8"?>
<projectDescription>
   <name>$id</name>
   <comment></comment>
   <projects>
   </projects>
   <buildSpec>
      <buildCommand>
        <name>org.eclipse.jdt.core.javabuilder</name>
        <arguments>
        </arguments>
      </buildCommand>
      <buildCommand>
        <name>org.eclipse.pde.ManifestBuilder</name>
        <arguments>
        </arguments>
      </buildCommand>
      <buildCommand>
        <name>org.eclipse.pde.SchemaBuilder</name>
        <arguments>
        </arguments>
      </buildCommand>
   </buildSpec>
   <natures>
     <nature>org.eclipse.pde.PluginNature</nature>
     <nature>org.eclipse.jdt.core.javanature</nature>
   </natures>
</projectDescription>
END_of_project
	    }

		close (PROJECT);

	closedir(FRAG_DIR);
    }
}

sub moveProperties {
    if ($_ =~ /.svn/) {
	return;
    }
    if ($_ =~ /(plugin[\w]+.properties)/ || $_ =~ /(messages[\w]+.properties)/)  {
	$filename = $1;
#	print "Found $1 ($File::Find::dir)\n";
	$File::Find::dir =~ m|[\S]*/[\S]*/([\S]*)/?|;
	$plugin = $1;
	#print "$plugin\n";

	if ($filename =~ /plugin[\w]+.properties/){
	    #print "svn mv $filename ../../fragments/$plugin.nl1/\n";
	    system("svn mv --force $filename ../../fragments/$plugin.nl1/$filename");
	} else {
	    print "$File::Find::dir\n";
	    $File::Find::dir =~ m|[\S]*?/[\S]*?/([\S]*?)/([\S]*)|;
	    $plugin = $1;
	    $srcDir = $2;

#HACK HACK HACK HACk
	    $dir = "~/udig/fragments/$plugin.nl1/$srcDir";
	    if (opendir(SRC_DIR, $dir)) {
	    } else {
		system("mkdir -p $dir");
		system("svn add ~/udig/fragments/$plugin.nl1/*");
		# mkdir ($dir, 0777) || die "Cannot make $dir: $!";
		opendir(SRC_DIR, $dir);
	    }
#	    system("pwd");
#	    print("svn mv --force $filename $dir/$filename\n");
	    system("svn mv --force $filename $dir/$filename");
	}
    }
}
