#!/usr/bin/perl

# Output is list of keys that are not found!

#use strict;
use File::Find;

#globals:
my %messages; #KVPs from messages.properties

my $messageFilename;

sub locateMessagesFile {
	my $filename = $_;
	if ($filename !~ /^messages.properties$/i) {
		return;
	}
	if ($File::Find::name =~ /^\.?\/?bin/) {
		print "Ignoring file in bin directory..";
		return;
	}
	if ($messageFilename =~ //) {
		die "Extra messages.properties file has been found at $messageFilename! Unable to continue.";
	}
	$messageFilename = $File::Find::name;
}

sub readProperties () {

	open (MESSAGES, "< $messageFilename") or die "Can't open $messageFilename: $!";
	my %messageHash;
	while (my $line = <MESSAGES>) {
		chomp($line);
		if ($line =~ /(.*)=(.*)/) {
			my $key = trim($1);
			#print "Key: $key\n";
			$messageHash{$key} = undef;
		}
	}
	
	close MESSAGES;
	return %messageHash;

}

sub trim () {
	my $result = $_[0];
	$result =~ s/^\s+//;
	$result =~ s/\s+$//;
	return $result;
}

sub process_file {
	my $filename = $_;
	if ($filename !~ /\.java$/i) {
		return;
	}
	#print "Processin file: $filename\n";

	open (INPUT, "< $filename") or die "Couldn't open $filename: $!\n";
	while (my $line = <INPUT>) {
		chomp($line);
		test($line);
	}
	close INPUT;
}

# Determine if $line contains a key. If so, make its presence in the hash.
sub test {
	my $line = $_[0];
#	print "Testing: $line\n";

	foreach my $key (keys %messages) {
#		print "Key: $key\n";
		if ($line =~ /$key/i) {
			#print "!$line! contains !$key!\n";
			$messages{$key} = "found";
		}
	}
}

# Determine if $line begins with a key that should be erased. If so, return true.
sub test2 {
	my $line = $_[0];

	foreach my $key (keys %messages) {
		if (!defined($messages{$key})) {
			if ($line =~ /^\s*$key/) {
				return true;
			}
		}
	}
}

find(\&locateMessagesFile, ".");

if (!defined($messageFilename)) {
	die "Unable to locate a messages.properties file. Aborting.";
}

%messages = readProperties();

find(\&process_file, ".");

my $status = system("mv $messageFilename $messageFilename.bak");
die "Unable to make a backup of $messageFilename: $?" unless $status == 0;

print "Writing out to $messageFilename\n";
open (MESSAGES, "> $messageFilename") or die "Can't open $messageFilename: $!";
open (MESSAGESBACKUP, "< $messageFilename.bak") or die "Can't open $messageFilename.bak: $!";

while (my $line = <MESSAGESBACKUP>) {
	chomp($line);
	if (!test2($line)) {
		print MESSAGES "$line\n";
	}
}

foreach my $key (keys %messages) {
	if (!defined($messages{$key})) {
		#print STDERR "WARNING: Could not find key: $key\n";
		print "$key\n";
	}
}
