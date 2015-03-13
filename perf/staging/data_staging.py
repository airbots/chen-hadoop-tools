#!/usr/bin/python

import os
#from subprocess import call

for point in ['80G','160G','320G','1024G','6400G','12T','48T','120T']:
  if (point == '80G'):
    for pfx in ['_1','_2']:
      #os.system('hadoop fs -ls '+'/user/oracle/perf/40G')
      os.system('hadoop fs -cp '+'/user/oracle/perf/40G /user/oracle/perf/80G/40G'+pfx) 
  elif (point == '160G'):
    for pfx in ['_1','_2']:
      #os.system('hadoop fs -ls '+'/user/oracle/perf/80G')
      os.system('hadoop fs -cp '+'/user/oracle/perf/40G /user/oracle/perf/80G/40G'+pfx)
  elif (point == '320G'):
    for pfx in ['_1','_2']:
      #os.system('hadoop fs -ls '+'/user/oracle/perf/16G')
      os.system('hadoop fs -cp '+'/user/oracle/perf/160G /user/oracle/perf/320G/160GT'+pfx)
  elif (point == '1024G'):
    for pfx in ['_1','_2','_3']:
      #os.system('hadoop fs -ls '+'/user/oracle/perf/320G')
      os.system('hadoop fs -cp '+'/user/oracle/perf/320G /user/oracle/perf/1024G/320G'+pfx)
    #os.system('hadoop fs -ls '+'/user/oracle/perf/160G')
    os.system('hadoop fs -cp '+'/user/oracle/perf/160G /user/oracle/perf/1024G/160G_4')
  elif (point == '6400G'):
    for pfx in ['_1','_2','_3','_4','_5','_6']:
      #os.system('hadoop fs -ls '+'/user/oracle/perf/1024G')
      os.system('hadoop fs -cp '+'/user/oracle/perf/1024G /user/oracle/perf/6400G/1024G'+pfx) 
  elif (point == '12T'):
    for pfx in ['_1','_2']:
      #os.system('hadoop fs -ls '+'/user/oracle/perf/6400G')
      os.system('hadoop fs -cp '+'/user/oracle/perf/6400G /user/oracle/perf/12T/6400G'+pfx)
  elif (point == '48T'):
    for pfx in ['_1','_2','_3','_4']:
      #os.system('hadoop fs -ls '+'/user/oracle/perf/12T')
      os.system('hadoop fs -cp '+'/user/oracle/perf/12T /user/oracle/perf/48T/12T'+pfx)
  elif (point == '120T'):
    for pfx in ['_1','_2']:
      #os.system('hadoop fs -ls '+'/user/oracle/perf/12T')
      #os.system('hadoop fs -ls '+'/user/oracle/perf/48T')      
      os.system('hadoop fs -cp '+'/user/oracle/perf/12T /user/oracle/perf/120T/10T'+pfx)
      os.system('hadoop fs -cp '+'/user/oracle/perf/48T /user/oracle/perf/120T/40T'+pfx)
  #print "Point is:"+point;
