project name: BitTorrent P2P Network for File Downloading in Distributed System 
implement time:09/2015
implementer:Xin Du
All rights reserved.

1. Description:
In this project, there are one fileowner and five peers. All these five peers download file from the fileowner and at the same time they download from one other peer, that is: client1 uploads to client5, client2 uploads to client1, client3 uploads to client2, client4 uploads to client3, client5 uploads to client4 and server uploads to all these five cleints. The corresponding port number are shown as below:

server 8821->client1  server 8822->client2  server 8824-> client3  server 8828->client4   server 8830->client5
client1 8823->client5   client2 8825-> client1   client3 8826-> client2   client4 8827->client3   client5 8829->client4

2:Operation flows:
First,depress p2p.zip and there will be six directories containing codes of server and clients. Directory of server also contains one file which will be shared.
Second, run server.java,client1.java,client2.java,client3.java,client4.java,client5.java one by one.