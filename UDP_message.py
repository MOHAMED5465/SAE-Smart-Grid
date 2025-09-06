import socket

# Configuration
UDP_IP = "127.0.0.1"     # Localhost
UDP_PORT = 12345         # The port your Vert.x app is listening on
MESSAGE = "1:25.4:523.6:1741334062"  # id:temperature:power:timestamp

# Create socket and send
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.sendto(MESSAGE.encode(), (UDP_IP, UDP_PORT))

print(f"Sent UDP message to {UDP_IP}:{UDP_PORT} -> {MESSAGE}")
