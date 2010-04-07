#!/usr/bin/perl

use LWP::UserAgent;
use URI::Escape;

my $lang = "it";

$ua = new LWP::UserAgent;
$ua->agent("Translate/1.0");

my $value = "";
my $key = "";

while(<>) {
  my $line = $_;
  if ( ( ! ( $line =~ /\S/ ) ) || ( $line =~ /^#/ ) ) {
    print;
    next;
  }
  if($line =~ /(.*?)=(.*)/) {
    $key = $1;
    $value = $2; 
    if($line =~ /\\\s*$/) {
      next;
    }
    else {
      print $key . "=" . translate($value);
      print "\n";
      $value = "";
      $key = "";
    }
  }
  else {
    if($line =~ /\\\s*$/) {
      $value .= $_;
      next;
    }
    else {
      $value .= $_;
      print $key . "=" . translate($value);
      print "\n";
      $value = "";
      $key = "";
    }
  }
}
  
sub translate {
  my $text = shift; 
  my $rv = "";
  my $req = new HTTP::Request POST => 'http://translate.google.com/translate_t';
  $req->content_type('application/x-www-form-urlencoded');

  $text = "text=" . uri_escape($text);
  $langpair = "langpair=" . uri_escape("en|" . $lang);
  $options = "hl=en&ie=UTF8";

  $req->content($text . "&" . $langpair . "&" . $options);

  my $res = $ua->request($req);

  if ($res->is_success) {
    if( $res->content =~ /<textarea.*?>(.*?)<\/textarea>/ ) {
      $rv = $1;
      $rv =~ s/\\ /\\\n/g
    }
  }
  $rv
}


#///* 
#<form action=translate_t method=poste
#<textarea name=text rows=5 cols=45 wrap=PHYSICAL>
#</textarea>
#<select name=langpair>
#<option value="en|de"> English to German</option>
#<option value="en|es"> English to Spanish</option>
#<option value="en|fr"> English to French</option>
#<option value="en|it"> English to Italian</option>
#<option value="en|pt"> English to Portuguese</option>
#<option value="en|ja"> English to Japanese BETA</option>
#<option value="en|ko"> English to Korean BETA</option>
#</select>
#<input type=hidden name=hl value="en">
#<input type=hidden name=ie value="UTF8">
#<input type=submit value="Translate">
#</form>

