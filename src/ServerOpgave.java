import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class ServerOpgave
{
    // 2XX = det gik godt
    // 4XX = brugeren har lavet en fejl
    // 5XX = vi har lavet en fejl
    
    public static void main(String[] args)
    {
        String httpRequest;
        ServerSocket welcomeSocket = null;
        File file;
        String path = "src/resources";
        ArrayList<String>
    
    
        System.out.println("Initiating server");
        try
        {
            // Ny socket oprettes med forbindelse til port 8081
            welcomeSocket = new ServerSocket(80);
            System.out.println("Socket found - server running");
        }
        catch(IOException e)
        {
            System.out.println("HTTP500 - Internal server error. The server isn't working right now.\\n\" +\n" +
                                       "Please try again later. \"" + e.getMessage());
            System.exit(69);
        }
        
        boolean stayInWhile = true;
        while(stayInWhile)
        {
           
            try
            {
                // vi laver en connectionSocket ud fra welcomeSocket
                Socket connectionSocket = welcomeSocket.accept();// Her throwes potentielt IOException
                System.out.println("You are connected");
    
                /* getInputStream reads bytes fra connectionSocket
                // InputStreamReader omdanner bytes til charset
                // BufferedReader læser stream of char fra inputStreamReader'en
                // == vi får bytes ind - omdannes til char af InputStreamReader, som så læses af BufferedReader*/
                BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                /* getOutputStream writes bytes TIL connectionSocket
                // DataOutputStream tager primitive typer og returnerer som bytes (som læses af maskine)
                // == DataOutputStream'en omdanner til bytes som getOutputStream så skriver til socket'en*/
                DataOutputStream outputToClient = new DataOutputStream(connectionSocket.getOutputStream());
    
                /* httpRequest == get, getRequest, HTTP/1.1
                // getRequest == det som står efter port-nr. i url
                // HTTP/1.1 == browseren siger til os: "jeg kommunikerer i HTTP/1.1 - derfor skal i snakke"*/
                httpRequest = inputFromClient.readLine();
    
                StringTokenizer stringTokenizer = new StringTokenizer(httpRequest);
                String request = stringTokenizer.nextToken(); // får første token = GET
                
                // TODO: find lige ud af hvordan man laver en anden type request end GET for at teste funktionen
                if(!request.equals("GET"))
                {
                    file = new File(path + "/http400.html");
                    // TODO ret lige i noget for at den ikke længere nede også sender:  outputToClient.writeBytes
                    //  ("HTTP/1.1 200 OK"+ "\r\n") fordi filen eksisterer
                    System.out.println("HTTP400 - bad request");
                    // TODO lav  outputToClient.writeBytes("HTTP/1.1 400 bad request"+ "\r\n");
                }
                
                String getRequest = stringTokenizer.nextToken(); // Vi VED det er en getter, på dette tidspunkt i koden
    
                file = new File(path + getRequest); // her throwes potentielt IOException
                
                /*
                if(!file.exists() && !getRequest.equals("/")) // hvis den IKKE eksisterer OG getRequesten IKKE er /
                {
                    headerByteArray =
                    "HTTP/1.1 404 Not Found"+ "\r\n";
                }
                else // eksisterer den ELLER er "/"
                {
                    headerByteArray =
                      outputToClient.writeBytes("HTTP/1.1 200 OK"+ "\r\n"); // status-code 200's reason phrase == OK
                    // ELLER sæt dem i header-byteArray'et
                    // Lav det som ArrayList til at starte med og så convert ArrayList til array
                }
                 */
                
                if(getRequest.equals("/")) // hvis den er /
                {
                    System.out.println("HTTP200 - success");
                    file = new File(path + "/index.html");
                }
                else if(!file.exists()) // hvis den ikke findes
                {
                    System.out.println("HTTP404 - file not found");
                    file = new File(path + "/http404.html");
                }
                
                else // hvis den findes
                {
                    System.out.println("HTTP200 - success");
                }
    
                /* browseren lukkede forbindelsen fordi vi ikke havde sagt hvad den kunne forvente: af type og længde
                // Vi siger til browseren: "her har du et svar og det er i http/1.1. og svaret er 200 ok"
                // \r == CR (carriage return), \n == LF (line feed)
                // Det første vi skriver ud her til browseren er en header*/
                outputToClient.writeBytes("HTTP/1.1 200 OK"+ "\r\n"); // status-code 200's reason phrase == OK
                outputToClient.writeBytes("Content-Length: "+ file.length() + "\r\n");
                outputToClient.writeBytes("Content-Type: text/html" + "\r\n");
                outputToClient.writeBytes( "\r\n");
                
                
                // TODO: header-byteArray + fil/content-byteArray
                // TODO: vi skal lave flere: outputToClient.writeBytes()
                
                // det næste vi skal skrive ud er body/content - vores fil som skrives ud
                
                
                showFile(outputToClient, file);
                
                connectionSocket.close();
            }
            catch(IOException e)
            {
                System.out.println("Fejlen er: " + e.getMessage());
                // TODO skriv en http500
            }
            
           
        }
        
    }
    
    /**
     * Metode til at vise file-object på hjemmeside
     *
     *
     * */
    public static void showFile(DataOutputStream outputToClient, File file)
    {
        try
        {
            FileInputStream inputFromFile = new FileInputStream(file);
            
            // finder filens længde
            long fileLength = file.length(); // TODO: det kan være den ikke viser korrekt antal bytes
           
            // finder antal gange som for-loop skal køre - hvor mange gange går 30 op i fileLength'en
            long numberOfByteArrays = fileLength / 30;
            
            // finder resterende bytes i file udover hele 30'ere
            long remainingBytes = fileLength % 30;
            /*
            // finder antal gange som for-loop skal køre - hvor mange gange går 30 op i fileLength'en
            int numberOfByteArrays = (int) (fileLength / 30);
            // finder resterende bytes i file udover hele 30'ere
            int remainingBytes = (int) (fileLength % 30);
             */
    
            byte[] byteArray = new byte[30];
    
            
            for(long i = 0; i < numberOfByteArrays; i++)
            {
                // readFromFile er knyttet til file-objektet
                // read()-metoden tager et byteArray og læse byteArrayet størrelse over i det
                // == læser 30 bytes fra fil og lægger de 30 bytes over i byteArray'et
                inputFromFile.read(byteArray);
                
                // udskriver byteArray til hjemmeside
                outputToClient.write(byteArray);
            }
            
            
            
            if(remainingBytes > 0)
            {
                // sætter byteArray til ny størrelse
                byteArray = new byte[(int) remainingBytes];
                
                inputFromFile.read(byteArray);
                outputToClient.write(byteArray);
            }
            
            
    
        }
        catch(IOException e)
        {
            System.out.println("Fejl i FileInputStream: " + e.getMessage());
        }
    }
}


































































/*
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class ServerOpgave
{
    public static void main(String args[]) throws IOException
    {
        String request = "";
        String path = "src/resources"; // start-path til fil
        //ServerSocket welcomeSocket = null; // Fordi ellers siger den at den måske aldrig bliver til noget
        
    
        System.out.println("Starting server");
        //try
        //{
            ServerSocket welcomeSocket = new ServerSocket(8081);
            System.out.println("Socket found");
        
        //catch(Exception e)
        //{
          //  System.out.println("HTTP500 - Internal server error. The server isn't working right now.\n" +
            //                           "    Please try again later. ");
        //}
        
        
    
        boolean stayInWhileLoop = true;
        while (stayInWhileLoop)
        {
            try
            {
                System.out.println("Waiting for a connection");
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("User connected");
    
                //Læser fra URL
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                //Skriver til browser
                System.out.println("MERE TEST1");
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                System.out.println("MERE TEST2");
                //inputFraClient
                request = inFromClient.readLine();
                System.out.println("MERE TEST3");
                System.out.println("Input from client: " + request); // request == get, getRequest, HTTP/1.1
    
                // Finder stien til filen brugeren requester
                StringTokenizer tokenizer = new StringTokenizer(request);
                String getRequest = tokenizer.nextToken(); //gemmer get (getRequest == GET)
                getRequest = tokenizer.nextToken(); //gemmer getRequest (getRequest == getReqeust man skriver i URL'en)
    
                System.out.println("TEEEEST: " + getRequest);
                // ny fil oprettes ud fra path'en og getRequest'en
                File file = new File(path + getRequest); // src/resources/
    
                if(file.exists()) // hvis den findes
                {
                    System.out.println("TEST1");
                 
    
                    // TODO: Find ud af filtype
                    // TODO: Find ud af fillængde
    
    
                    if(getRequest.equals("/"))
                    {
                        System.out.println("TEST2");
                        file = new File(path + getRequest + "index.html"); // HER
                        System.out.println(path + getRequest + "index.html");
                        
                    }
                    else if(getRequest.equals("/exit"))
                    {
                        System.out.println("TEST3");
                        System.out.println("Exit test");
                        stayInWhileLoop = false;
                        connectionSocket.close();
                        welcomeSocket.close();
                        file = new File(path + "exit.html");
                    }
                }
                else // if(!file.exists()) // hvis filen ikke eksisterer
                {
                    System.out.println("TEST4");
                    // sendes en HTTP 404 = Not Found
                    file = new File(path + "/http404.html");
                }
                // Todo: rykket til metode
                FileInputStream fromFile = new FileInputStream(file);
    
                boolean stayInWhileLoop1 = true;
                byte[] bytearr = new byte[30];
                int bla = 0;
                while(stayInWhileLoop1)
                {
                    bla = fromFile.read(bytearr); // hvis der ikke er flere bytes i filen
                    if (bla == -1) // fordi bla == -1, hvis der ikke er flere bytes i filen
                    {
                        System.out.println("TEST6");
                        stayInWhileLoop1 = false;
                    }
                    else
                    {
                        System.out.println("TEST7");
                        outToClient.write(bytearr);
                    }
                }
                connectionSocket.close();
            }
            catch(Exception e)
            {
                System.out.println("TEST5");
                System.out.println("Der skete en fejl");
                System.out.println(e);
                
            }
            
            
            
            
            
            /*
            boolean cont = true;
            int bla = 0;
            byte[] bytearr = new byte[30];
            while(cont)
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
            
             */
            /*
            
        }
        
    }
    
    public static void printFile(File file, DataOutputStream outToClient) throws IOException
    {
        
        FileInputStream fromFile = new FileInputStream(file);
        boolean stayInWhileLoop = true;
        int moreBytesInFile = 0;
        byte[] byteArray = new byte[30];
        while(stayInWhileLoop)
        {
            moreBytesInFile = fromFile.read(byteArray); // hvis der ikke er flere bytes i filen
            if (moreBytesInFile == -1) // fordi bla == -1, hvis der ikke er flere bytes i filen
            {
                stayInWhileLoop = false;
            }
            else
            {
                outToClient.write(byteArray);
            }
        }
        
        
        
        
        // Lav smart udregning ift. hvor mange bytes der skal læses ind i array'et ud fra fillængden
        /*
        int fileLength = file.getLength();
        outToClient.write(fileLength);
        ELLER
        outToClient.write(byte[file.getLength()]);
        
        Hvis det er hurtigere at læse det af flere omgange, sø brug modulus til at finde ud af hvor mange gange á 30
        eller sådan noget og så resten
         */
        
        /*
        byte[] byteArray = new byte[30];
        
        // condition == i != -1    FORDI hvis der ikke er flere bytes, så returnerer den -1
        for(int i = 0; i != -1; i = fromFile.read(byteArray))
        {
            outToClient.write(byteArray);
        }
        */
        
        
    /*
        
        
        
    }
}
*/

