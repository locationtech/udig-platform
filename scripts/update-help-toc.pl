#!/usr/bin/perl -wt
 
use strict;

my @langs = ('EN', 'DE', 'FR');
my @pages = ('TOC', 'tocgettingstarted', 'tocconcepts', 'toctasks', 'tocreference', 'tocfaq');

foreach my $lang (@langs) {
  foreach my $page (@pages) {
    my $url = 'http://udig.refractions.net/confluence/display/'.$lang.'/'.$page;
    my $file;
    if ($lang eq 'EN') { #put in help plugin
      $file = '../plugins/net.refractions.udig.help/'.lc($page).'.xml';
    } else { #put in help fragment
      $file = '../fragments/net.refractions.udig.help.nl1/nl/'.lc($lang).'/'.lc($page).'.xml';
    }
    print time().' '.$url;
    use LWP::UserAgent;
    my $ua = LWP::UserAgent->new;
    $ua->agent('uDig help updater /scripts/update-help-toc.pl');
    $ua->cookie_jar({});
    my $req = HTTP::Request->new(GET => $url);
    my $res = $ua->request($req);
    if ($res->is_success) {
      my $html=$res->content;
      $html=~m/<div class\="wiki-content">/g;
      my $p1 = pos($html);
      $html=~m/<\/div>/g;
      my $p2 = pos($html);
      my $xml = substr($html, $p1, $p2-$p1-6);
      $xml=~s/<[^>]+>//g; #remove all tags
      $xml=~s/&gt;/>/g; #revive '>'
      $xml=~s/&lt;/</g; #revive '<'
      $xml=~s/&#(91|93);//g; #destroy []
      $xml=~s/^\s+//g; #remove leading spaces
      $xml=~s/\s+$//g; #remove trailing spaces
      $xml=~s/\n\s+/\n/g; #remove spaces at start of line
      $xml=~s/\s+\n/\n/g; #remove spaces at end of line
      $xml=~s/\s{2,}/ /g; #remove multiple spaces
      open(FILEOUT,'>'.$file);
      print FILEOUT $xml;
      close FILEOUT;
    } else {
      print " (couldn't fetch)";
    }
    print "\n";
  }
}
