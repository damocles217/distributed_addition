import java.io.*;
import java.net.*;

public class App {
  public static void main(String[] args) throws Exception {

    String serverAddress = "localhost"; // Dirección del servidor
    int serverPort = 5000; // Puerto del servidor

    try (DatagramSocket socket = new DatagramSocket()) {
      InetAddress serverIPAddress = InetAddress.getByName(serverAddress);

      int number = (int) (Math.random() * 100) + 1;
      System.out.println("Your number is: " + number);
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(byteStream);
      out.writeInt(number);
      byte[] sendData = byteStream.toByteArray();

      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIPAddress, serverPort);
      socket.send(sendPacket);

      byte[] receiveData = new byte[1024];
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      socket.receive(receivePacket);

      String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
      System.out.println("The add is: " + response);
    } catch (IOException e) {
      System.out.println("Error in UDP client: " + e.getMessage());
    }
  }
}
