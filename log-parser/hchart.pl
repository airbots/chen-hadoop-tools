#!usr/bin/perl

use strict;
use warnings;
use GD::Graph::area;
use GD::Graph::linespoints;
use GD::Graph::Data;

open (MYFILEIN,"$ARGV[0]")|| die "Can not open or no such a file!~";
open (OUT,">$ARGV[0].analysis")|| die "Can not open!";
open (CMBAR,">>cmbar.bar")|| die "Can not open!";

my @time;
my $taskid;
my $jobid;
my %taskhash;
my $j=0;
my $jobsubmit=0;
my $joblaunch=0;
my $jobfinish=0;
my $compare=0;
my $taskstart=0;
my $mapnumber=0;
my $reducenumber=0;
my $tasknum=0;
my $line;
my $subline;
my $taskfinish;
my $setupstart=9999999999999;
my $setupstop=0;
my $mapstart=9999999999999;
my $mapstop=0;
my $shufflestart=9999999999999;
my $shufflestop=0;
my $reducestart=0;
my $reducestop=0;
my $cleanstart=9999999999999;
my $cleanstop=0;
my $taskstatus;
my $tasktype;
my $job_exe_time;
my $name1=$ARGV[0];
my (@setup,@map,@shuffle,@reduce);
my $jobscale=0;
my $tailstart=9999999999999;
my $tailstop=0;
my $compower_m=0;
my $compower_s=0;
my $compower_r=0;
my $file_bytes_written=0;
my $hdfs_bytes_read=0;
my $file_bytes_read=0;
my $hdfs_bytes_written=0;
my $reduce_shuffle_bytes=0;
my $map_input_bytes=0;
my $map_output_bytes=0;
while(<MYFILEIN>){
    $line=$_;
    if($line=~/JOBID/){
		$jobid=$1;
		if ($line=~/SUBMIT_TIME=\"(\d+)\"/){
			$jobsubmit=$1;
		}
		elsif($line=~/LAUNCH_TIME=\"(\d+)\"/){
			$joblaunch=$1;
			$joblaunch=$joblaunch-$jobsubmit;
		}
		elsif($line=~/FINISH_TIME=\"(\d+)\"/){
			$jobfinish=$1;
			$job_exe_time=($jobfinish-$jobsubmit)/1000;
			if($line=~/\(Map\sinput\srecords\)\((\d+)\)/){
                 $jobscale=$1;
				 print $jobscale."\n";
            }
                        if($line=~/\(FILE_BYTES_READ\)\((\d+)\)/){
                            $file_bytes_read=$1;
							print $file_bytes_read."\n";
                        }
						if($line=~/\(FILE_BYTES_WRITTEN\)\((\d+)\)/){
                            $file_bytes_written=$1;
							print $file_bytes_written."\n";
                        }
                        if($line=~/\(HDFS_BYTES_READ\)\((\d+)\)/){
                            $hdfs_bytes_read=$1;
							print $hdfs_bytes_read."\n";
                        }
                        if($line=~/\(HDFS_BYTES_WRITTEN\)\((\d+)\)/){
                            $hdfs_bytes_written=$1;
							print $hdfs_bytes_written."\n";
                        }
                        if($line=~/\(Map\sinput\sbytes\)\((\d+)\)/){
                            $map_input_bytes=$1;
							print $map_input_bytes."\n";
                        }
                        if($line=~/\(Map\soutput\sbytes\)\((\d+)\)/){
                            $map_output_bytes=$1;
							print $map_output_bytes."\n";
                        }
		}
    }
	elsif($line=~/Task TASKID=\"(\w+)\"/){		
		$taskid=$1;
		if(~/TASK_TYPE=\"(\w+)\"/){
			$tasktype=$1;
			$taskhash{$taskid}{"Type"}=$tasktype;
			if ($tasktype eq "SETUP") {
			
				#print "SETUP\n";
				if($line=~/START_TIME=\"(\d+)\"/){
					if ($setupstart>$1) {
						$setupstart=$1;
					}
					$taskstart=$1-$jobsubmit;
	    			if(exists $taskhash{$taskid}{"SetupStart"}){
	    				if($taskstart lt $taskhash{$taskid}{"SetupStart"}){
							$taskhash{$taskid}{"SetupStart"}=$taskstart/1000;
														print $taskstart."\n";
						}
						#print $taskhash{$taskid}{"SetupStart"}."\t".$taskstart."\n";	
					}
					else{
	
						$taskhash{$taskid}{"SetupStart"}=$taskstart/1000;
						#print $taskhash{$taskid}{"SetupStart"}."\t".$taskstart."\n";	
					}
				}
				if($line=~/TASK_STATUS=\"(\w+)\"/){
					$taskstatus=$1;
					if ($taskstatus eq "SUCCESS") {
						if($line=~/FINISH_TIME=\"(\d+)\"/){
							if($setupstop<$1){
								$setupstop=$1;
							}
							$taskfinish=$1-$jobsubmit;
							if(($taskfinish=$1-$jobsubmit)<0){
								$taskfinish=0;
							}
							else {
								$taskfinish=$1-$jobsubmit;
							}
							if(exists $taskhash{$taskid}{"SetupFinish"}){
								if($taskfinish>$taskhash{$taskid}{"SetupFinish"}){
                					$taskhash{$taskid}{"SetupFinish"}=$taskfinish/1000;
                				}
							}
							else {
								$taskhash{$taskid}{"SetupFinish"}=$taskfinish/1000;
							}
							#print $taskhash{$taskid}{"SetupFinish"}."\t".$taskfinish."\n";
						}
					}
				}
			}
			elsif ($tasktype eq "CLEANUP") {
				if($line=~/START_TIME=\"(\d+)\"/){
					if ($cleanstart>$1) {
						$cleanstart=$1;
					}
					$taskstart=$1-$jobsubmit;
	    			if(exists $taskhash{$taskid}{"CleanStart"}){
	    				if($taskstart lt $taskhash{$taskid}{"CleanStart"}){
							$taskhash{$taskid}{"CleanStart"}=$taskstart/1000;
						}
						#print $taskhash{$taskid}{"SetupStart"}."\t".$taskstart."\n";	
					}
					else{
						$taskhash{$taskid}{"CleanStart"}=$taskstart/1000;
						#print $taskhash{$taskid}{"SetupStart"}."\t".$taskstart."\n";	
					}
				}
				if($line=~/TASK_STATUS=\"(\w+)\"/){
					$taskstatus=$1;
					if ($taskstatus eq "SUCCESS") {
						if($line=~/FINISH_TIME=\"(\d+)\"/){
							if($cleanstop<$1){
								$cleanstop=$1;
							}
							$taskfinish=$1-$jobsubmit;
							if(($taskfinish=$1-$jobsubmit)<0){
								$taskfinish=0;
							}
							else {
								$taskfinish=$1-$jobsubmit;
							}
							if(exists $taskhash{$taskid}{"CleanFinish"}){
								if($taskfinish>$taskhash{$taskid}{"CleanFinish"}){
                					$taskhash{$taskid}{"CleanFinish"}=$taskfinish/1000;
                				}
							}
							else {
								$taskhash{$taskid}{"CleanFinish"}=$taskfinish/1000;
							}
							#print $taskhash{$taskid}{"SetupFinish"}."\t".$taskfinish."\n";
						}
					}
				}
			}
			elsif ($tasktype eq "MAP") {
				
				$mapnumber=$mapnumber+1;
				#print $taskhash{$taskid}{"Type"}."\n";
				if($line=~/START_TIME=\"(\d+)\"/){
					if ($mapstart>$1) {
						$mapstart=$1;
					}
	    			if(($taskstart=$1-$jobsubmit)<0){
						$taskstart=0;
					}
					else {
						$taskstart=$1-$jobsubmit;
					}
	    			if(exists $taskhash{$taskid}{"MapStart"}){
	    				if($taskstart lt $taskhash{$taskid}{"MapStart"}){
							$taskhash{$taskid}{"MapStart"}=$taskstart/1000;
						}
					}
					else{
						$taskhash{$taskid}{"MapStart"}=$taskstart/1000;
					}	
				}
				$line=~/TASK_STATUS=\"(\w+)\"/;
				$taskstatus=$1;
				$line=~/FINISH_TIME=\"(\d+)\"/;
				if ($taskstatus eq "SUCCESS") {
					$tasknum++;

						if($mapstop<$1){
							$mapstop=$1;
							$tailstop=$mapstop;
						}
						$taskfinish=$1-$jobsubmit;

						if(($taskfinish=$1-$jobsubmit)<0){
							$taskfinish=0;
						}
						else {
							$taskfinish=$1-$jobsubmit;
						}
						if ($tailstart>$taskfinish) {
							$tailstart=$taskfinish;
					    }
						if(exists $taskhash{$taskid}{"MapFinish"}){
							if($taskfinish>$taskhash{$taskid}{"MapFinish"}){
                				$taskhash{$taskid}{"MapFinish"}=$taskfinish/1000;
                			}
						}
						else {
							$taskhash{$taskid}{"MapFinish"}=$taskfinish/1000;
						}
				}
			}
		}
		if($tasktype eq "REDUCE"){
			$reducenumber=$reducenumber+1;
			if($line=~/START_TIME=\"(\d+)\"/){
					#print $1."\n";
				if ($shufflestart>$1) {
					$shufflestart=$1;
				}
                $taskstart=$1-$jobsubmit;
	    		if(($taskstart=$1-$jobsubmit)<0){
					$taskstart=0;
				}
				else {
					$taskstart=$1-$jobsubmit;
				}
                		if(exists $taskhash{$taskid}{"ReduceStart"}) {
				    if($taskstart<$taskhash{$taskid}{"ReduceStart"}){
					$taskhash{$taskid}{"ReduceStart"}=$taskstart/1000;
				    }
				}
				else {
					$taskhash{$taskid}{"ReduceStart"}=$taskstart/1000;
				}
			}
			$line=~/TASK_STATUS=\"(\w+)\"/;
			$taskstatus=$1;
			if ($taskstatus eq "SUCCESS") {
			    if($line=~/FINISH_TIME=\"(\d+)\"/){
                		$taskfinish=$1-$jobsubmit;
				if($reducestop<$1){
					$reducestop=$1;
				}
				if(($taskfinish=$1-$jobsubmit)<0){
					$taskfinish=0;
				}
				else {
					$taskfinish=$1-$jobsubmit;
				}
                	        if(exists $taskhash{$taskid}{"ReduceFinish"}){
				    if($taskfinish>$taskhash{$taskid}{"ReduceFinish"}){
						$taskhash{$taskid}{"ReduceFinish"}=$taskfinish/1000;
					}
				}
				else {
					$taskhash{$taskid}{"ReduceFinish"}=$taskfinish/1000;
				}
			    }
		    	}
    		}
	}
	#To get the detail of each reduce attaempt, record the first start and latest finished 
	elsif($line=~/^ReduceAttempt/){
		$line=~/TASKID=\"(\w+)\"/;
		$taskid=$1;
		if($line=~/START_TIME=\"(\d+)\"/){
			my $Ratttaskstart=$1-$jobsubmit;
	    	if(($Ratttaskstart=$1-$jobsubmit)<0){
				$Ratttaskstart=0;
			}
			else {
				$Ratttaskstart=$1-$jobsubmit;
			}
            if(exists $taskhash{$taskid}{"RattStart"}) {
				if($Ratttaskstart<$taskhash{$taskid}{"RattStart"}){
					$taskhash{$taskid}{"RattStart"}=$Ratttaskstart/1000;
				}
			}
			else {
				$taskhash{$taskid}{"RattStart"}=$Ratttaskstart/1000;
			}
		}
		$line=~/TASK_STATUS=\"(\w+)\"/;
				#print $1."\n";
		$taskstatus=$1;
		if ($taskstatus eq "SUCCESS") {
			if($line=~/FINISH_TIME=\"(\d+)\"/){
				my  $Ratttaskfinish=$1-$jobsubmit;
				if(($Ratttaskfinish=$1-$jobsubmit)<0){
					$Ratttaskfinish=0;
				}
				else {
					$Ratttaskfinish=$1-$jobsubmit;
					}
				if(exists $taskhash{$taskid}{"RattFinish"}){
					if($Ratttaskfinish>$taskhash{$taskid}{"RattFinish"}){
						$taskhash{$taskid}{"RattFinish"}=$Ratttaskfinish/1000;
					}
				}
				else {
					$taskhash{$taskid}{"RattFinish"}=$Ratttaskfinish/1000;
				}
			}
			if($line=~/SHUFFLE_FINISHED=\"(\d+)\"/){ 
				my $Rattshufflefinish=$1-$jobsubmit;
				if ($shufflestop<$1) {
						$shufflestop=$1;
				}
				if(($Rattshufflefinish=$1-$jobsubmit)<0){
					$Rattshufflefinish=0;
				}
				else {
					$Rattshufflefinish=$1-$jobsubmit;
				}
				if(exists $taskhash{$taskid}{"RattShuffleFinish"}){
					if($Rattshufflefinish>$taskhash{$taskid}{"RattShuffleFinish"}){
						$taskhash{$taskid}{"RattShuffleFinish"}=$Rattshufflefinish/1000;
					}
				}
				else {
					$taskhash{$taskid}{"RattShuffleFinish"}=$Rattshufflefinish/1000;
				}
				if ($shufflestop<$Rattshufflefinish) {
					$reducestart=$shufflestop=$Rattshufflefinish;
				}

			}
	 		if($line=~/SORT_FINISHED=\"(\d+)\"/){
				my $Rattsortfinish=$1-$jobsubmit;
				if(($Rattsortfinish=$1-$jobsubmit)<0){
					$Rattsortfinish=0;
				}
				else {
					$Rattsortfinish=$1-$jobsubmit;
					}
				if(exists $taskhash{$taskid}{"RattSortFinish"}){
					if($Rattsortfinish>$taskhash{$taskid}{"RattSortFinish"}){
             			$taskhash{$taskid}{"RattSortFinish"}=$Rattsortfinish/1000;
					}
				}
				else {
					$taskhash{$taskid}{"RattSortFinish"}=$Rattsortfinish/1000;
				}
			}
		}
    }
#    elsif ($line=~/^Task TASKID=\"(\w+)\"/){
		
#		if($line=~/TASK_TYPE=\"(\w+)\"/){
#			my $tasktype=$1;
#			$taskhash{$taskid}{"Type"}=$tasktype;
			
#			if($tasktype eq "MAP"){
#				$mapnumber=$mapnumber+1;
#				if($line=~/START_TIME=\"(\d+)\"/){
#					#print $1."\t".$jobsubmit."\n";
					#$taskstart=$line=~/START_TIME=\"(\d+)\"/
					
#	    			if(($taskstart=$1-$jobsubmit)<0){
#					    $taskstart=0;
#					}
#					else {
#						$taskstart=$1-$jobsubmit;
#						}
					#print $taskstart."\n";
#	    			if(exists $taskhash{$taskid}{"Start"}){
#	    				if($taskstart lt $taskhash{$taskid}{"Start"}){
#							$taskhash{$taskid}{"Start"}=$taskstart/1000;
#						}
						#print $taskhash{$taskid}{"Start"}."\t".$taskstart."\n";	
#					}
#					else{
	
#						$taskhash{$taskid}{"Start"}=$taskstart/1000;
						#print $taskhash{$taskid}{"Start"}."\t".$taskstart."\n";	
#					}
					
#				}
#				if($line=~/FINISH_TIME=\"(\d+)\"/){
#	    			my  $taskfinish=$1-$jobsubmit;
#					if(($taskfinish=$1-$jobsubmit)<0){
#					    $taskfinish=0;
#					}
#					else {
#						$taskfinish=$1-$jobsubmit;
#					}
#	    			if(exists $taskhash{$taskid}{"Finish"}){
#						if($taskfinish>$taskhash{$taskid}{"Finish"}){
#                			$taskhash{$taskid}{"Finish"}=$taskfinish/1000;
#               		}
#					}
#					else {
#						$taskhash{$taskid}{"Finish"}=$taskfinish/1000;
#					}
#				}
#			}
#			elsif*/
}
close MYFILEIN;
$j=0;
while($j<$job_exe_time+1){
    $time[$j][0]=0;
    $time[$j][1]=0;
    $time[$j][2]=0;
    $time[$j][3]=0;
    $time[$j][4]=0;
    $time[$j][5]=0;
	#print "select start";

    foreach $taskid (keys %taskhash){	
		#print $taskhash{$taskid}{"Type"}."\n";
		if($taskhash{$taskid}{"Type"} eq "MAP"){
			if(($taskhash{$taskid}{"MapStart"} <= $j) and $taskhash{$taskid}{"MapFinish"} >= $j){
				$time[$j][0]=$time[$j][0]+1;
			}
		}
		elsif($taskhash{$taskid}{"Type"} eq "SETUP"){
			if(($taskhash{$taskid}{"SetupStart"} <= $j) and $taskhash{$taskid}{"SetupFinish"} >= $j){
				$time[$j][4]=$time[$j][4]+1;
			}
		}
		elsif($taskhash{$taskid}{"Type"} eq "CLEANUP"){
			if(($taskhash{$taskid}{"CleanStart"} <= $j) and $taskhash{$taskid}{"CleanFinish"} >= $j){
				$time[$j][5]=$time[$j][5]+1;
			}
		}
        elsif($taskhash{$taskid}{"Type"} eq "REDUCE"){
			#print $taskhash{$taskid}{"RattShuffleFinish"}."\n";
            if(($taskhash{$taskid}{"RattStart"} <= $j) and ($taskhash{$taskid}{"RattShuffleFinish"} >= $j)){
                $time[$j][1]=$time[$j][1]+1;
            }
   			if(($taskhash{$taskid}{"RattShuffleFinish"} <= $j) and ($taskhash{$taskid}{"RattSortFinish"}>= $j)){
                $time[$j][2]=$time[$j][2]+1;
            }
			if(($taskhash{$taskid}{"RattSortFinish"} <= $j) and $taskhash{$taskid}{"ReduceFinish"}>=$j){
                $time[$j][3]=$time[$j][3]+1;
            }
	
        }
    }
    $j=$j+1;
}

$j=0;
print OUT "\nTime";
while ($j<($job_exe_time+1))
{
    print OUT "\t".$j;
	$j++;
}
while ($j<($job_exe_time+1))
{
    print OUT "\t".$j;
	$j++;
}
$j=0;
print OUT "\nSETUP";

while ($j<($job_exe_time+1))
{
    print OUT "\t".$time[$j][4];
	$j++;
}
$j=0;
print OUT "\nMAP";
while ($j<($job_exe_time+1))
{
    print OUT "\t".$time[$j][0];
    $compower_m+=$j*$time[$j][0];
	$j++;
}
$j=0;
print OUT "\nSHUFFLE";
while ($j<($job_exe_time+1))
{
    print OUT "\t".$time[$j][1];
	$compower_s+=$j*$time[$j][1];
	$j++;
}
$j=0;
print OUT "\nSORT";
while ($j<($job_exe_time+1))
{
    print OUT "\t".$time[$j][2];
	$compower_r+=$j*$time[$j][2];
	$j++;
}
$j=0;
print OUT "\nREDUCE";
while ($j<($job_exe_time+1))
{
    print OUT "\t".$time[$j][3];
	$j++;
}
$j=0;
print OUT "\nCLEANUP";
while ($j<($job_exe_time+1))
{
    print OUT "\t".$time[$j][5];
	$j++;
}
print $tasknum;
#print OUT "\nSTARTUP_OVERHEAD:\t".($setupstart-$jobsubmit)/1000;
#print OUT "\nMAP_SETUP:\t\t".($setupstop-$setupstart)/1000;
#print OUT "\nMAP:\t\t\t".($mapstop-$mapstart)/1000;
#print OUT "\nSHUFFLE:\t\t".($shufflestop-$shufflestart)/1000;
#print OUT "\nREDUCE:\t\t\t".($reducestop-$shufflestop)/1000;
#print OUT "\nMAP_CLEANUP:\t\t".($cleanstop-$cleanstart)/1000;
#print OUT "\nCLOSE_OVERHEAD:\t".($jobfinish-$cleanstop)/1000;
#close OUT;
print "\nThis is the job scale:".$jobscale;
print CMBAR "JOB:".$jobscale;
print CMBAR "\nSTARTUP\tMAP\tSHUFFLE\tREDUCE\tTAIL\tMAP_COMPOWER\tSHUFFLE_COMPOWER\tREDUCE_COMPOWER\tFILE_READ\tFILE_WRITTEN\tHDFS_READ\tHDFS_WRITTEN\tMAP_INPUT\tMAP_OUTPUT\n";
print CMBAR (($setupstart-$jobsubmit)/1000)."\t".(($mapstop-$mapstart)/1000)."\t".(($shufflestop-$shufflestart)/1000)."\t".(($reducestop-$shufflestop)/1000)."\t".(($tailstop-$tailstart-$jobsubmit)/1000)."\t".$compower_m."\t".$compower_s."\t".$compower_r."\t".$file_bytes_read."\t".$file_bytes_written."\t".$hdfs_bytes_read."\t".$hdfs_bytes_written."\t".$map_input_bytes."\t".$map_output_bytes."\n";
close CMBAR;
 
	$j=0;
	while ($j<($job_exe_time+1))
	{
		$setup[$j]=$time[$j][4];
		$map[$j]=$time[$j][0];
		$shuffle[$j]=$time[$j][1];
		$reduce[$j]=$time[$j][3];
		$j++;
	}

	my $my_graph = GD::Graph::area->new(500,300);
	my $data = GD::Graph::Data->new(
	[
		[(0..$job_exe_time)],
		[@setup],
		[@map],
		[@shuffle],
		[@reduce],
	]
	) or die GD::Graph::Data->error;
#        my $my_scale = GD::Graph::linespoints;
	my $scale = GD::Graph::Data->new(
	[
		[qw(STARTUP SETUP MAP SHUFFLE REDUCE)],
		[($setupstart-$jobsubmit)/1000, ($setupstop-$setupstart)/1000,($mapstop-$mapstart)/1000,($shufflestop-$shufflestart)/1000,($reducestop-$shufflestop)/1000]
]);
	$my_graph->set(
	#Graph Setting
	x_label => 'Time/second',
	y_label => 'Num. of Mappers',
	title => 'MRMD Timeline',
	y_max_value => $tasknum,
	y_min_value => 0,
	x_max_value => $job_exe_time+1,
	y_tick_number => $tasknum,
	#y_label_skip => ,
	#x_tick_number => 10,
	#y_all_ticks => 1,
	x_label_skip => $job_exe_time/($job_exe_time/10),
	x_plot_values => 1,
	y_plot_values => 1,
	box_axis => 1,

	#Legend setting
	legend_placement =>'RT',
	#legend_spacing => ,
	#lg_cols => ,
	#legend_marker_width => ,
	#legend_marker_height => ,

	#Area setting
	dclrs           => ['#065fb9','#fe9d01','#a2b700','red'],  # area color
	accentclr	 => 'green',  # area color
	#bgclr          => 'white',
     	#fgclr               => 'cyan',
     	#boxclr              => 'dblue',
     	accentclr           => 'dblue',
     	valuesclr           => '#ffff77',
	
	#margin setting
	l_margin            => 5,       # ¸÷¸ö±ß¾à
	b_margin            => 5,
	r_margin            => 5,
	t_margin	=>5,
 
	transparent => 0,
);
	$my_graph->set_y_label_font('arial', 16);
    $my_graph->set_x_label_font('arial', 16);                 
    $my_graph->set_y_axis_font('arial', 12);                
    $my_graph->set_x_axis_font('arial', 12);
    $my_graph->set_title_font('arial', 16);
    $my_graph->set_legend_font('arial', 14);
    $my_graph->set_values_font('arial', 12);
	$my_graph->set_legend('SETUP','MAP','SHUFFLE','REDUCE');
	#$graph->set_legend_font(font name);
	#$my_graph->plot($data) or die $my_graph->error;
	#$graph->plot($scale) | die $graph->error;


	#my $ext = $my_graph->export_format;
	open(IMG,">$ARGV[0]"."_Exe.png");
	$my_graph->plot($data) or die $my_graph->error;
	print IMG $my_graph->gd->png;
	close IMG;
