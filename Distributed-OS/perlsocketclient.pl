#!/usr/bin/perl

use strict;
use Socket;
my $host= shift||'localhost';
my $port =shift||7070;
my $server = "129.93.229.129";
socket(SOCKET,PF_INET,SOCK_STREAM,(getprotobyname('http'))[2]) or die "Can't create a socket $!\n";
connect(SOCKET, pack('Sn4x8', AF_INET, $port,$server)) or die "Can't connect to port $port!\n";

my $line;
while ($line=<SOCKET>){
    print "$line\n";
}
close SOCKET or die "close:$!";
