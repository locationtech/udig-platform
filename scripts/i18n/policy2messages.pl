#!/usr/bin/perl -pi.orig

# find -iname '*.java' -exec perl -pi.orig policy2messages.pl '{}' \;

# ... mat(Policy.bind("WMSServiceImpl.connecting.to"), ne...
if (/Policy\s*\.\s*bind\s*\(\s*"(.*)"\s*\)/ ) {
	$key = $1;
	$key =~ s/\./_/g;

	s/Policy\s*\.\s*bind\s*\(\s*"(.*)"\s*\)/Messages.$key/g;
	s|//.NON.NLS-..||g;
} 
elsif (/Policy\s*\.\s*localize/) {
	$_ = "";
}
elsif (/Policy\s*\.\s*cleanup/) {
	$_ = "";
}
elsif (/\s*import.*Policy/) {
	$_ = "";
}
