#!/bin/sh

umount /home
umount /opt
mount -t nfs 192.168.0.1:/home /home
mount -t nfs 192.168.0.1:/opt /opt
service hadoop-datanode stop
service hadoop-tasktracker stop
exit
