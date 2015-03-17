#!/usr/bin/perl -w

use warnings;
use strict;

my $line;
open(IN,"$ARGV[0]")||die "Can not open input file";
while(<IN>){
   $line=$_;
   if($line=~m/DataNode/ or $line=~m/TaskTracker/){
	if($line=~m/^root(\s+)(\d+)/){
	    #print $2."\n";
	    `kill -9 $2`;
	}
   }
}
close IN;
