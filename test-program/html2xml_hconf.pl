#!/usr/bin/perl
#use strict;
#use warnings;

open (IN,"$ARGV[0]")|| die "Can not open or no such file!";
my ($filename,$expand)=split('\.',$ARGV[0]);
open (OUT,">$filename.xml")|| die "Can not open!";

my $i=0;
my @array=<IN>;
print OUT "<?xml version=\"1.0\"?>"."\n"."<?xml-stylesheet type=\"text/xsl\" href=\"configuration.xsl\"?>\n";
print OUT "<!-- Do not modify this file directly.  Instead, copy entries that you -->\n";
print OUT "<!-- wish to modify from this file into hadoop-site.xml and change them -->\n";
print OUT "<!-- there.  If hadoop-site.xml does not already exist, create it.      -->\n";
print OUT "<configuration>\n";
print OUT "<!--- global properties -->\n";

while($i<@array) {
     my $comments2='';
     my $parameter='';
     my $parameter2='';
     my $setting='';
     my $comments='';
	$_=$array[$i];
	if (/<td><a name=\"/) {
#		my @content=split("</td><td>",$_);
#		$parameter=$content[0]=/>(\w+)<\/a>$/;
#		$setting=$content[1];
#		$comments=$content[2];
		my $counter=1;
		($parameter,$parameter2,$setting,$comments)=$_=~/<td><a name=\"(.*)\">(.*)<\/a><\/td><td>(.*)<\/td><td>(.*)/i;
		$_=$comments;
		if (/<\/td>/) {
			$i++;
            my ($comments,$tdout)=split("<\/td>",$comments);
            print $comments;
			print OUT "<property><name>".$parameter."</name><value>"."$setting"."</value><description>".$comments."</description></property>\n";
			next;
		}
		else {
			$_=$array[$i+1];
			while(!(/<\/td>$/)) {
				$comments2=$comments2.$_;
				$_=$array[$i+1+$counter];
				$counter++;
			}
			$_=~m/(.*)<\/td>$/g;
			$comments2=$comments2.$1;
			$i+=$counter;
			$comments=$comments.$comments2;
			print OUT "<property><name>".$parameter."</name><value>"."$setting"."</value><description>".$comments."</description></property>\n";
		}
	}
	else {
		$i++;
	}
}
print OUT "</configuration>";
close IN;
close OUT;


