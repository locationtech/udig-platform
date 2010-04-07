package Properties;
use strict;

# module for parsing property files 

sub new {
	my $self = {};
	$self->{props} = ();

	bless($self);
	return $self;
}

sub parse {
  #my $file = $_[0];
	my $self = shift;
	my $file = shift;

  open(PROPS, "<$file");
  while(<PROPS>) {
    chomp;

    #ignore commented lines and lines without an = in them
    if (!m/^#/g && m/=/g) {
      my ($prop,$value) = split(/=/,$_);
      $self->{props}{$prop} = $value;
    }
  }
  close(PROPS);
}

sub get {
	my $self = shift;
	my $prop = shift;

	return $self->{props}{$prop};
}

1;
