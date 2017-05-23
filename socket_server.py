'''
socket_server.py - Python code for serving a socket on an IP address

'''


import socket
import time

def serve_socket(port):
    '''
    Serves a blocking socket on the specified port.  Returns a new socket object 
    representing the client, on which send() and recv() can be invoked.
    '''

    print('Server: running on port %d' % port)
    
    sock = None   
        
    while True:
        
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

        try:
            sock.bind(('', port))
            break
            
        except socket.error as err:
            print('Server: bind failed: ' + str(err))
            exit(1)
            
        time.sleep(1)
            
    sock.listen(1) 
    
    print('Server: waiting for client to connect ...')
    try:
        client, _ = sock.accept()
        print('Server: accepted connection')
       
        
    except:
        print('Failed')
        exit(1)

    return client
