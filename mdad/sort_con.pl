#!/user/bin/perl


open (IN, "$ARGV[0]" )||die "CAN NOT OPEN OR NO SUCH FILE~!";
open (IN2, "$ARGV[1]")|| die "can not open";
open (OUT, ">>COM_RESULT.TXT");
my @array1=<IN>;
my @array2=<IN2>;
my $i,$j;
$i=$j=0;

while ($j<@array1){
  my  $line1=$array1[$j];
  my ($front1,@back1)=split(" ",$line1);
  $i=0;
      while($i<@array2){
          my  $line2=$array2[$i];
          my ($front2,@back2)=split(" ",$line2);   
          if ($front1==$front2){
#              print $front1."\n";
              my $iter=1;
#              print OUT "\n".$front2;
              while($iter<4){
#                   print $back1[$iter];
                  if (!($back1[$iter]==$back2[$iter])){
                      print OUT $front2."\t".$back1[$iter]."--".$back2[$iter]."\n";
                  }
                  $iter++;
              }
          }
          $i++;
      }
 $j++;
}
close IN;
close IN2;
close OUT;
