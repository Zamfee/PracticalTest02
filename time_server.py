import socket
from datetime import datetime
import time

# Configurarea serverului
HOST = '0.0.0.0'  # Ascultă pe toate interfețele rețelei
PORT = 12345       # Portul de ascultare

# Crearea socket-ului
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind((HOST, PORT))
server_socket.listen(1)

print(f"Server is running on {HOST}:{PORT}")

try:
    while True:
        client_socket, client_address = server_socket.accept()
        print(f"Connection from {client_address}")

        try:
            while True:
                # Trimite ora curentă
                now = datetime.now().strftime("%H:%M:%S")
                client_socket.sendall(now.encode('utf-8') + b'\n')
                time.sleep(1)  # Trimite la fiecare secundă
        except (ConnectionResetError, BrokenPipeError):
            print(f"Connection with {client_address} closed.")
        finally:
            client_socket.close()
except KeyboardInterrupt:
    print("Server is shutting down.")
finally:
    server_socket.close()