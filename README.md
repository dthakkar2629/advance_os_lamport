# Lamport Mutual Exclusion Algorithm Implementation in Java

## Description
Design and implement a distributed system which consists of multiple server and client processes. Assume
that each file is replicated on all the servers, and all replicas of a file are consistent in the beginning. A client can
perform a READ or WRITE operation on the files. READ operation involves only one server that has the target file
and the server is chosen randomly by a client. WRITE involves all servers that have a copy of the target file and all
of them should be updated in order to keep the replicas consistent. READ/WRITE on a file can be performed by only
one client at a time. However, different clients are allowed to concurrently perform a READ/WRITE on different files.
In order to ensure this, implement Lamport’s mutual exclusion algorithm among clients so that no READ/WRITE
violation could occur. The operations on files (hosted by servers) can be seen as critical section executions.

To host files, create separate directory for each server. The servers must support following operations and reply to
the clients with appropriate messages –
1. ENQUIRY : A request from a client for information about the list of hosted files.
2. READ: A request to read last line from a given file.
3. WRITE: A request to append a string to a given file.

The set of files does not change during your program’s execution. Also, assume that there is no server failure during
your program’s execution.

The clients must be able to do the following –
1. Gather information about the hosted files by querying the servers and keep the metadata for future.
2. Append a string < client id; Timestamp > to a file, fi during WRITE. Here client id is the name of the
client, and Timestamp is the value of the client’s local clock when the write request is generated. This must be
done to all


## Running the Code:  
Before running the code, please update the below mentioned details in configuration file:
* serverport (port for running the server)
* clientport (port for running the client)
* noofrequests ( # of operation to perform by a single)
* server address and it’s directory
* clients address

**Required Argument:** <configuration_file_path> <server/client> <server (1-3) or client (1-5) ID> <br />

**Server:** java -jar aos_assignment_1.jar config.properties server 1<br />
**Client:** java -jar aos_assignment_1.jar config.properties client 1
