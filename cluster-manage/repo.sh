mkdir /root/.ssh
cp /root/authorized_keys /root/.ssh/
cp /opt/RPM-GPG-KEY-EPEL /etc/pki/rpm-gpg/RPM-GPG-KEY-EPEL
cp /opt/epel.repo /etc/yum.repos.d/
#install ganglia
yum -y install ganglia
yum -y install ganglia-gmond.x86_64
cp -f /opt/gmond.conf /etc/gmond.conf
chkconfig gmond on
service gmond start

