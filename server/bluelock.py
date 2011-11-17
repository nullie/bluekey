#!/usr/bin/env python

from subprocess import call

from bluetooth import *


UUID = "c29f1e50-f115-11e0-be50-0800200c9a66"


def get_free_channel():
    busy = set(
        s['port'] for s in find_service(address='localhost')
        if s['protocol'] == 'RFCOMM'
    )       

    for i in range(1, 32):
        if i not in busy:
            return i

    raise Exception("Cannot find free RFCOMM channel")


def serve():
    sock = BluetoothSocket(RFCOMM)

    # Ubuntu and probably other linuxen have a bug, PORT_ANY get assigned to
    # channel 1, where pnat plugin listens, so we implement our own
    # method for finding free channel

    # sock.bind(("", PORT_ANY)) # Doesn't work on recent ubuntu

    sock.bind(("", get_free_channel()))

    sock.listen(1)

    advertise_service(sock, "Blue Key", UUID, [UUID])

    print "Waiting for connection on channel", sock.getsockname()[1]

    while True:
        try:
            client, info = sock.accept()

            print "Accepted connection from", info

            command = client.recv(1)

            if command == 'L':
                call(["gnome-screensaver-command", "-l"])
            elif command == 'U':
                call(["gnome-screensaver-command", "-d"])
            
            client.close()
        except BluetoothError:
            pass


if __name__ == '__main__':
    serve()
