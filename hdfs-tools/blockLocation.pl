#!/user/bin/perl -w

use GD::Graph::bars;
use strict;
use warnings;
my $node1;
my $node2;
my $line;
my %nodes;
my %blocks;
my $filename;
my @array;
my $i=0;
open(IN,"blocks.txt")||die "Does not have block file";
open(OUT,">blockDistr.txt")||die "can not open output file!";
@array=<IN>;
while($i<@array){
    $node1="";
    $node2="";
    $filename="";
    #print $line;
	$line=$array[$i];
#    if($line=~m/generator\/(\S+) <dir>$/){
#    	$filename=$1;
#	$mark=1;
    #}
	#print $line;
    if($line=~m/\[192\.168\.0\.(\d+):50010, 192\.168\.0\.(\d+):50010\]$/){
        #$filename=$1;
	$line=$array[$i-2];
#	print $line;
	#print $line;
	$node1=$1-1;
	$node2=$2-1;
	if($line=~m/generator\/(\S+) <dir>$/){
            $filename=$1;
            #print $filename."\t".$node1."\t".$node2."\n";
	    $nodes{($node1)}{"blocks"}=(exists ($nodes{$node1}{"blocks"}))?(($nodes{($node1)}{"blocks"}).",".$filename):($filename);
	    $nodes{($node1)}{"weight"}=(exists ($nodes{$node1}{"weight"}))?(($nodes{$node1}{"weight"})+1):1;
	    $nodes{($node2)}{"blocks"}=(exists ($nodes{$node2}{"blocks"}))?(($nodes{($node2)}{"blocks"}).",".$filename):($filename);
	    $nodes{($node2)}{"weight"}=(exists ($nodes{$node2}{"weight"}))?(($nodes{$node2}{"weight"})+1):1;
            $blocks{$filename}{"1"}=$node1;
	    $blocks{$filename}{"2"}=$node2;
	}
    }
$i++;
}
close IN;
#foreach my $key (sort {$a<=>$b} keys %nodes){
#    print OUT "node".$key.":".$nodes{$key}{"blocks"}."\n";
#    print OUT "weight:".$nodes{$key}{"weight"}."\n";
#}
#close OUT;

my $file;
my $interval;
my @eachline;
foreach my $parameter (@ARGV){
    open(BK,"$parameter")||die "can not open simulate file";
    if($parameter=~m/simulate(\d+)\.sh/){
      $interval=$1;
      while(<BK>){
  	$line=$_;
 	@eachline=split(" ",$line);
        foreach (@eachline){
        $line=$_;
	if($line=~m/\/user\/generator\/(\S+)\/file/){
	    $file=$1;
	    print $file."\n";
	    if($file=~m/file/){
		$blocks{$file}{"request"}=(exists ($blocks{$file}{"request"}))?($blocks{$file}{"request"}+1):1;
		push @{$blocks{$file}{"interval"}},$interval;
#		print $interval."\n";
		foreach my $key (keys %nodes){
		    if ($nodes{$key}{"blocks"}=~m/$file/){
			$nodes{$key}{"request"}=(exists ($nodes{$key}{"request"}))?($nodes{$key}{"request"}+1):1;
			
		    }
		}
	    }

	}
        }
      }
    }
}
my $my_graph= GD::Graph::bars->new(1024,768);
my $my_graph1= GD::Graph::bars->new(1024,768);
my $my_graph2= GD::Graph::bars->new(1024,768);
my $my_graph3= GD::Graph::bars->new(1024,768);
my @data;
my @data1;
my @data2;
my @data3;
my @x;
my @y;
my @y1;
my @xt;
my @yt;
my @yreq;
foreach my $key (sort {$a<=>$b} keys %nodes){
    print OUT "node".$key.":".$nodes{$key}{"blocks"}."\n";
    print OUT "\tDistributed:".$nodes{$key}{"weight"}."\n";
    push(@y1,$nodes{$key}{"weight"});
    $nodes{$key}{"request"}=(exists ($nodes{$key}{"request"}))?($nodes{$key}{"request"}):0;
    print OUT "\tRequests:".($nodes{$key}{"request"})."\n";
    push(@x,$key);
    #print $key."\n";
    #if(exists ($nodes{$key}{"request"})){
    push(@y,($nodes{$key}{"request"}));
    #}
}
my $counter;
my $sum;
my $previous=0;
my $current;
my $type=0;
my $totalrequest=0;
foreach my $blockname (sort keys %blocks){
    if(exists ($blocks{$blockname}{"request"})){
        $counter=0;
        $sum=0;
        $current=0;
        $previous=0;
        push(@xt,$blockname);
        $type++;
        print  $blockname."\t".$blocks{$blockname}{"request"}."\n";
        $totalrequest+=$blocks{$blockname}{"request"};
        #print $blockname."\n";
        push(@yreq,$blocks{$blockname}{"request"});
        foreach ( @{$blocks{$blockname}{"interval"}}){
            print $blockname."\t".$_."\n";
	    $current=$_;
            if($previous==0){
 	        $previous=$_;
	        $sum=0;
	        $counter++;
	        next;
	    }
	    else{
	        $sum+=$current-$previous;
	        $counter+=1;
	        $previous=$current;
	    }
	#print $blockname."\t".$sum."\t".$counter."\n";
        }
        if($counter!=0){
            $sum=$sum/$counter;
            push (@yt,$sum);
        }
    }
}
print $totalrequest/$type."\n";
push(@data,\@x);
push(@data,\@y);
push(@data1,\@x);
push(@data1,\@y1);
push(@data2,\@xt);
push(@data2,\@yt);
push(@data3,\@xt);
push(@data3,\@yreq);
$my_graph->set(
    x_label =>'Node Number',
    y_label =>'Request Frequency',
    title => 'Node Request Frequency',
    transparent => 0,
);
open(IMG,">reqfrq.png")||die "Can not open png to output!";
$my_graph->plot(\@data) or return 0;
print IMG $my_graph->gd->png;
close IMG;

$my_graph1->set(
    x_label=>'Node Name'.
    y_label=>'No. of blocks located'.
    title=>'Block distribution',
    transparent=>0,
);
open(IMG1,">blkdistr.png");
$my_graph1->plot(\@data1) or return 0;
print IMG1 $my_graph1->gd->png;
close IMG1;

$my_graph2->set(
    x_label=>'Block Name'.
    y_label=>'Interavel Time / ms'.
    title=>'Block Interavel Time',
    transparent=>0,
);
open(IMG2,">blkreq.png");
$my_graph2->plot(\@data2) or return 0;
print IMG2 $my_graph2->gd->png;
close IMG2;

$my_graph3->set(
    x_label=>'Block Name'.
    y_label=>'Request'.
    title=>'Block Request',
    transparent=>0,
);
open(IMG3,">bkreq.png");
$my_graph3->plot(\@data3) or return 0;
print IMG3 $my_graph3->gd->png;
close IMG3;

