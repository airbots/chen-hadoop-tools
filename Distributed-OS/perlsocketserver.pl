#!/usr/bin/perl

use strict;
#use warning;
use Socket;
#use IO::Socket;
#my $sock= new IO::Socket::INET {
#                                 LocalAddr => '127.0.0.1:7070',
#				 #LocalPort => '7777',
#				 Proto => "http",
#				 Listen => 10,
#   				 Reuse => 10,
#};
#die "Could not create socket:$!\n" unless $sock;

#my $new_sock=$sock->accept();
#while (<$new_sock>){
#    print $_;
#}
#close($sock);
my $port= shift || 7070;
my $proto = getprotobyname('tcp');
socket(SOCKET, PF_INET,SOCK_STREAM, $proto) or die "Can't open socket $!\n";
setsockopt(SOCKET, SOL_SOCKET, SO_REUSEADDR, 1) or die "Can't see socket option to SO_REUSEADDR $!\n";
bind(SOCKET, pack('Sn4x8', AF_INET, $port, "127.0.0.1")) or die "Can't bind to port $port!";
listen(SOCKET,5)or die "Listen:$!";
print "SERVER started on port $port\n";
my $client_addr;
while($client_addr=accept(NET_SOCKET,SOCKET)){
    print NEW_SOCKET "Smile from the server";
    close NEW_SOCKET;
}
