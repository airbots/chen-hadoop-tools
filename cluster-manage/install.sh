
# Download configs
cd /root
#wget http://192.168.0.1/kickstart.tar
#tar -xvf kickstart.tar
unalias cp
cp -f /opt/backup/hosts /etc/
cp -f /opt/backup/passwd /etc/
cp -f /opt/backup/shadow /etc/
cp -f /opt/backup/selinux /etc/sysconfig/
#cp -f network /etc/sysconfig/
mkdir /root/.ssh
cp -f /opt/backup/authorized_keys /root/.ssh/authorized_keys
cp -f /opt/backup/group /etc/
#cp -f cloudera-stable.repo /etc/yum.repos.d/
#cp -f cloudera.repo /etc/yum.repos.d/
cp -f /opt/backup/passwd- /etc/
cp -f /opt/backup/shadow- /etc/
cp -f /opt/backup/group- /etc/



cd /
# Install PBS client
#rpm -i http://dl.atrpms.net/el5-i386/atrpms/stable/torque-mom-2.0.0p8-13.0.el5.x86_64.rpm

# Mount NFS
mount 192.168.0.1:/home /home
mount 192.168.0.1:/opt /opt
rpm -i /opt/kickstart/torque-mom-2.0.0p8-13.0.el5.x86_64.rpm
# chmod for PBS
chmod 1777 /var/spool/pbs/spool
chmod 1777 /var/spool/pbs/undelivered

echo Bugeater2 >> /var/spool/pbs/server_name


echo $usecp *:/home /home >> /var/spool/pbs/mom_priv/config

# Programming tools
yum -y update
yum install -y *gcc*
yum install -y *openmpi*
yum install -y openssh
yum install -y gcc-c++
yum install -y libtool
yum install -y gettext

#rpm -i /opt/kickstart/cloudera-repo-0.1.0-1.noarch.rpm
#rpm -i /opt/kickstart/jdk-6u17-linux-amd64.rpm
yum install -y condor-7.4.0-linux-x86_64-rhel5-dynamic-1.x86_64.rpm
#yum install -y hadoop-conf-bugeater2-slave.noarch

#yum install -y hadoop-datanode.noarch 0:0.18.3-14.cloudera.CH0_3

#chkconfig hadoop-datanode on
#chkconfig hadoop-tasktracker on
#service hadoop-datanode start
#service hadoop-tasktracker start



#update os environment
#yum -y update


#install ganglia
#sh /opt/gangliarepo.sh

chkconfig iptables off
iptables -F

yum -y install ntp
cp /opt/backup/ntp.conf /etc/ntp.conf
cp /opt/backup/step-tickers /etc/ntp/step-tickers
service ntpd start
chkconfig ntpd on

yum -y install log4j.x86_64 log4j-javadoc.x86_64 log4j-manual.x86_64 log4c.x86_64 log4cpp.x86_64

#update repository
cp /opt/RPM-GPG-KEY-EPEL /etc/pki/rpm-gpg/RPM-GPG-KEY-EPEL
cp /opt/epel.repo /etc/yum.repos.d/
#install ganglia

#install java
yum -y install jre
yum -y install java
yum -y install ant

#install perl modules
cd /opt/Chart-2.4.1
perl Makefile.PL
make
make test
make install

cd /opt/PathTools-3.31
perl Makefile.PL
make
make test
make install

cd /opt/Class-Accessor-0.34
perl Makefile.PL
make
make test
make install

cd /opt/IO-CaptureOutput-1.1102
perl Makefile.PL
make
make test
make install

cd /opt/Ganglia-Gmetric-0.3
perl Makefile.PL
make
make test
make install

rpm -ivh /opt/jre-6u18-linux-amd64.rpm
cp -f /opt/.bashrc /root/
cp -f /opt/.bash_profile /root/

rpm -ivh /opt/ganglia/libconfuse-2.6-2.el5.rf.x86_64.rpm
rpm -ivh /opt/ganglia/libconfuse-devel-2.6-2.el5.rf.x86_64.rpm
rpm -ivh /opt/ganglia/ganglia-gmond-3.1.2-1.x86_64.rpm 
rpm -ivh /opt/ganglia/ganglia-gmond-modules-python-3.1.2-1.x86_64.rpm
rpm -ivh /opt/ganglia/ganglia-devel-3.1.2-1.x86_64.rpm
sh /opt/ganglia/ganglia_upgrad.sh
cp -f /opt/ganglia/gmond.conf /etc/ganglia/gmond.conf
chkconfig gmond on
service gmond restart

yum -y install dstat.noarch
yum -y install sysstat.x86_64


#upgrade new metrics for ganglia
cp -f /opt/ganglia_metrics/*.sh /root/
cp -f /opt/ganglia_metrics/root /var/spool/cron/


#cleanup

