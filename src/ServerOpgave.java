import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ServerOpgave
{
    public static void main(String args[]) throws Exception
    {
        System.out.println("Starting server");
        String sentence;
        //String userText;
        //boolean go_on = true;
        
        ServerSocket welcomeSocket = new ServerSocket(777);
        System.out.println("Socket found");
        
        System.out.println("Waiting for a connection");
        Socket connectionSocket = welcomeSocket.accept();
        System.out.println("User connected");
        

        //Læser fra URL
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

        //if(inFromClient.)
        sentence = inFromClient.readLine();
        System.out.println("FROM CLIENT: " + sentence); // sentence == get, getRequest, HTTP/1.1

        outToClient.writeBytes("HTTP/1.0 200 Ja den er fin" + '\n');
        outToClient.writeBytes("\n");
    
        boolean stayInWhileLoop = true;
        while (stayInWhileLoop)
        {
            
            String path = "/Web_Project/ServerOpgave/";
            StringTokenizer tokenizer = new StringTokenizer(sentence);
            String word;
            word = tokenizer.nextToken(); //gemmer get
            word = tokenizer.nextToken(); //gemmer getRequest
            
            File file = new File(path + word);
            if (word.equals("/"))
            {
                file = new File(path + word + "index.html");
            }
            if (!file.exists())
            {
                file = new File(path + "error404.html");
            }
            if (word.equals("exit"))
            {
                stayInWhileLoop = false;
                connectionSocket.close();
                welcomeSocket.close();
            }
            FileInputStream fromFile = new FileInputStream(file);
    
            boolean cont = true;
            int bla = 0;
            byte bytearr[] = new byte[10];
            while (cont)
            {
                bla = fromFile.read(bytearr);
                if (bla == -1)
                {
                    cont = false;
                }
                else
                {
                    outToClient.write(bytearr);
                }
            }
        }
    }
}

