#!/usr/bin/perl -i.orig

# find -iname 'messages*.properties' -exec perl -i.orig createMessages.pl '{}' \;

# input should be the contents of a messages.properties file

@keys;

while (<>) {

	# WMSServiceImpl.connecting.to=Connecting to {0}

	# print;
	
	#if (/^(.*)=(.*)$/){
	#	print "$1 ||| $2\n";
	#}
	
	if (/^(.+)=(.+)/) {
		$key = $1;
		$key =~ s/\./_/g;
		unshift(@keys, $key);
		s/^(.+)=/$key=/;
	}
	print;
}

if ($ARGV =~ /(.*)messages\.properties$/) {
	$plugin = "FIXME";
	$bundle = "FIXME";
	
	print "Creating file: $1Messages.java\n";

	open (MSG_CLASS, "> $1Messages.java") || break;
	
	print MSG_CLASS <<END_of_class1;
/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package $plugin.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "$bundle.internal.messages"; //\$NON-NLS-1\$
END_of_class1

	foreach $key (@keys) {
		print MSG_CLASS "\tpublic static String $key;\n";
	}

	print MSG_CLASS <<END_of_bork
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
END_of_bork
		    
}
