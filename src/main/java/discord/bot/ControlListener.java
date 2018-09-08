package discord.bot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

public class ControlListener extends Thread{
    public void run(){
        ServerSocket mainServer;
        Socket client = new Socket();
        Boolean whileRun = true;
        Boolean clientConnected = false;
        System.out.println("[" + App.dtf.format(LocalDateTime.now()) + "] Control Connection Listener started on new Thread");
        try {
            mainServer = new ServerSocket(8443);
            System.out.println("[" + App.dtf.format(LocalDateTime.now()) + "] Started Control Server ");
            while (whileRun) {
                client = mainServer.accept();
                Scanner cmdReader = new Scanner(client.getInputStream());
                System.out.println("[" + App.dtf.format(LocalDateTime.now()) + "] Waiting for Client Message");
                int Count = 0;
                while (Count <= 50)
                {
                    if (cmdReader.hasNextLine())
                    {
                        String commandReceived = cmdReader.nextLine();
                        System.out.println("[" + App.dtf.format(LocalDateTime.now()) + "] Received: " + commandReceived);
                        Count = 501;
                        CommandDecoder.decodeCmd(commandReceived);
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Count ++;
                    }

                }
            }
        } catch (IOException e) {
            System.out.println("[" + App.dtf.format(LocalDateTime.now()) + "] Failed to start main Server Socket");
            whileRun = false;
        }

    }
}
