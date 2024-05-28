package server;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
  private static AtomicInteger sharedAdd = new AtomicInteger(0);
  private static AtomicInteger counter = new AtomicInteger(0);
  private int port = 5000;

  public void run() {

    try (DatagramSocket socket = new DatagramSocket(port)) {
      System.out.println("UDP Server running in port: " + port);

      while (true) {
        byte[] receiveData = new byte[1024];

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);

        new ClienteHandler(socket, receivePacket).start();
      }
    } catch (IOException e) {
      System.out.println("Error in the UDP server: " + e.getMessage());
    }
  }

  private static class ClienteHandler extends Thread {
    private DatagramSocket socket;
    private DatagramPacket receivePacket;

    public ClienteHandler(DatagramSocket socket, DatagramPacket receivePacket) {
      this.socket = socket;
      this.receivePacket = receivePacket;
    }

    public void run() {
      try {
        // Leer el número enviado por el cliente
        ByteArrayInputStream byteStream = new ByteArrayInputStream(receivePacket.getData());
        DataInputStream in = new DataInputStream(byteStream);
        int number = in.readInt();

        InetAddress clientAddress = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();

        System.out.println("Received number: " + number);

        int result = sharedAdd.addAndGet(number);

        while (result == number) {
          Thread.sleep(10);
          result = sharedAdd.get();
        }

        int actualCount = counter.addAndGet(1);

        String response = String.valueOf(result);
        byte[] sendData = response.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
        socket.send(sendPacket);

        if (actualCount >= 2) {
          counter.set(0);
          sharedAdd.set(0);
          System.out.println("Result of the addition is: " + result);
        }

      } catch (IOException e) {
        System.out.println("Error en el manejo del cliente UDP: " + e.getMessage());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
