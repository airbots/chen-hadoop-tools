#!/bin/bash

iteration=0
echo $iteration
 until [ $iteration -gt 100 ];
   do  
     mkdir -p "$iteration"  
     hadoop dfs -copyToLocal "mdad/out$iteration/*" "./$iteration/"
    ((iteration+=1))
   done
