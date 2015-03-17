#!/usr/bin/perl
use strict;
use warnings;

open (INPUT2,"argon.md")||die "can not open the file";  
open (INPUT1,"argon.mda")||die "can not open the file";
open (OUT,">argon.mol")||die "can not open the file";

my @array1=<INPUT1>;
my @array2=<INPUT2>;
my $i=0;
my $j=0;
my $line1;
my $line2;
my ($part1,$part2,$part3,$part4);
my ($p1,$p2,$p3,$p4,$p5,$p6,$p7,$p8,$p9,$p10,$p11);

while ($j<@array1) {
	$line1=$array1[$j];
	$line2=$array2[$j];
	($part1,$part2,$part3,$part4)=split(" ",$line1);
	
	($p1,$p2,$p3,$p4,$p5,$p6,$p7,$p8,$p9,$p10,$p11)=split(" ",$line2);
    print $p1."\n";
	if ($part1 eq "O") {
		$i++;
		print OUT $i."   "."Ar"."   ".$part2."   ".$part3."   ".$part4."   ".$p6."   ".$p7."   ".$p8."   ".$p9."   ".$p10."   ".$p11."\n";	
	}
	$j++;
}
close (INPUT1);
close (INPUT2);
close (OUT);

