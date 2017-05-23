#!/usr/bin/env python

# Import your controller here =====================================================

#from quadstick import ExtremePro3D as Controller
#from quadstick import PS3 as Controller
#from quadstick.rc.spektrum import DX8 as Controller
#from quadstick.rc.frsky import Taranis as Controller
#from quadstick.keyboard import Keyboard as Controller

# Simulation parameters ===========================================================

# Timeout for receiving data from client
TIMEOUT_SEC      = 1.0

# Other imports ===================================================================

from sys import argv, exit
from math import pi
import struct
import time
import random
import os

from socket_server import serve_socket

# Helper functions ================================================================

def sendFloats(client, data):

    client.send(struct.pack('%sf' % len(data), *data))

def unpackFloats(msg, nfloats):

    return struct.unpack('f'*nfloats, msg)

def receiveFloats(client, nfloats):
 
    # We use 32-bit floats
    msgsize = 4 * nfloats

    # Implement timeout
    start_sec = time.time()
    remaining = msgsize
    msg = ''
    while remaining > 0:
        msg += client.recv(remaining)
        remaining -= len(msg)
        if (time.time()-start_sec) > TIMEOUT_SEC:
            return None

    return unpackFloats(msg, nfloats)
    
def receiveString(client):
    
    return client.recv(int(receiveFloats(client, 1)[0]))

def scalarTo3D(s, a):

    return [s*a[2], s*a[6], s*a[10]]
    
# LogFile class ======================================================================================================

class LogFile(object):

    def __init__(self, directory):
 
        self.fd = open(directory + '/' + time.strftime('%d_%b_%Y_%H_%M_%S') + '.csv', 'w')

    def writeln(self, string):

        self.fd.write(string + '\n')
        self.fd.flush()

    def close(self):

        self.fd.close()

# Initialization =====================================================================================================

# Require controller
#controller = Controller(('Stabilize', 'Hold Altitude', 'Unused'))

# Serve a socket on the port indicated in the first command-line argument
client = serve_socket(int(argv[1]))

# Receive working directory path from client
pyquadsim_directory = receiveString(client)

# Create logs folder if needed
logdir = pyquadsim_directory + '/logs'
if not os.path.exists(logdir):
    os.mkdir(logdir)

# Open logfile named by current date, time
logfile = LogFile(logdir)

# Loop ==========================================================================================================

prevtime = time.time()

# Forever loop will be halted by VREP client or by exception
while True:

    try:

        currtime = time.time()
        print(currtime - prevtime)
        prevtime = currtime

        # Get core data from client
        coreData = receiveFloats(client, 3)

        # Quit on timeout
        if not coreData: exit(0)

        # Get extra data from client
        #extraData = getAdditionalData(client, receiveFloats)

        # Unpack IMU data        
        x    = coreData[0]  # positive = nose up
        y    = coreData[1]  # positive = right down
        z    = coreData[2]  # positive = nose right

        # Poll controller
        #demands = controller.poll()

        x_shift = 0
        with open('/home/volodymyr/Git/PyQuadSim/x_shift', 'r+') as x_shift_file:
            content = x_shift_file.readline()
            try:
                x_shift = float(content)
            except Exception:
                print('X shift file contains bad data: \"' + content + '\"')
            x_shift_file.seek(0)
            x_shift_file.truncate()
            x_shift_file.write("0")

        y_shift = 0
        with open('/home/volodymyr/Git/PyQuadSim/y_shift', 'r+') as y_shift_file:
            content = y_shift_file.readline()
            try:
                y_shift = float(content)
            except Exception:
                print('Y shift file contains bad data: \"' + content + '\"')
            y_shift_file.seek(0)
            y_shift_file.truncate()
            y_shift_file.write("0")

        # Send new position to client
        sendFloats(client, (x + x_shift, y + y_shift, z))

    except Exception as e:
        # Inform and exit on exception
        #controller.error()
        print('Unknown exception:', e)
        exit(0)
