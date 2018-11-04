package ServerClientWithDownload;

import ServerClientWithDownload.TestServer.*;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;


class WorkerThread implements Runnable {
    final static String CRLF = "\r\n";
    private Socket socket;
    private InputStream is;
    private OutputStream os;

    private int id = 0;

    public WorkerThread(Socket s, int id) {
        this.socket = s;

        try {
            this.is = this.socket.getInputStream();
            this.os = this.socket.getOutputStream();
        } catch (Exception e) {
            System.err.println("Sorry. Cannot manage client [" + id + "] properly.");
        }

        this.id = id;
    }

    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(this.is));
        PrintWriter pr = new PrintWriter(this.os);

        pr.println("Your id is: " + this.id);
        pr.flush();

        String str;

        while (true) {
            try {


                if ((str = br.readLine()) != null) {

                    //str = br.readLine();
                    StringTokenizer tokens = new StringTokenizer(str);
                    // System.out.println(tokens.nextToken());

                    if (str.equals("BYE")) {
                        System.out.println("[" + id + "] says: BYE. Worker thread will terminate now.");
                        break; // terminate the loop; it will terminate the thread also
                    } else if (str.equals("DL")) {
                        try {
                            File file = new File("capture.jpg");
                            FileInputStream fis = new FileInputStream(file);
                            BufferedInputStream bis = new BufferedInputStream(fis);
                            OutputStream os = socket.getOutputStream();
                            byte[] contents;
                            long fileLength = file.length();
                            pr.println(String.valueOf(fileLength));        //These two lines are used
                            pr.flush();                                    //to send the file size in bytes.

                            long current = 0;

                            long start = System.nanoTime();
                            while (current != fileLength) {
                                int size = 10000;
                                if (fileLength - current >= size)
                                    current += size;
                                else {
                                    size = (int) (fileLength - current);
                                    current = fileLength;
                                }
                                contents = new byte[size];
                                bis.read(contents, 0, size);
                                os.write(contents);
                                //System.out.println("Sending file ... "+(current*100)/fileLength+"% complete!");
                            }
                            os.flush();
                            System.out.println("File sent successfully!");
                        } catch (Exception e) {
                            System.err.println("Could not transfer file.");
                        }
                        pr.println("Downloaded.");
                        pr.flush();

                    } else if (tokens.nextToken().equals("GET")) {
                        System.out.println("in get");

                        String fileName = tokens.nextToken();
                        fileName = "." + fileName;

                        // Open the requested file.
                        FileInputStream fis = null;
                        boolean fileExists = true;
                        try {
                            fis = new FileInputStream(fileName);
                        } catch (FileNotFoundException e) {
                            fileExists = false;
                        }

                        // Construct the response message.
                        String statusLine = null;
                        String contentTypeLine = null;
                        String entityBody = null;
                        if (fileExists) {
                            statusLine = "HTTP/1.0 200 OK" + CRLF;
                            contentTypeLine = "Content-Type: " +
                                    contentType(fileName) + CRLF;
                        } else {
                            statusLine = "HTTP/1.0 404 Not Found" + CRLF;
                            contentTypeLine = "Content-Type: text/html" + CRLF;
                            entityBody = "<HTML>" +
                                    "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
                                    "<BODY>Not Found</BODY></HTML>";
                        }
                        // Send the status line.
                        os.write(statusLine.getBytes());
                        //os.writeBytes(statusLine);

                        // Send the content type line.
                        os.write(contentTypeLine.getBytes());

                        // Send a blank line to indicate the end of the header lines.
                        os.write(CRLF.getBytes());

                        // Send the entity body.
                        if (fileExists) {
                            sendBytes(fis, os);
                            fis.close();
                        } else {
                            os.write(entityBody.getBytes()) ;
                        }

                        pr.flush();
                    } else {
                        System.out.println("[" + id + "] says: " + str);
                        pr.println("Got it. You sent \"" + str + "\"");
                        pr.flush();
                    }
                } else {
                    System.out.println("[" + id + "] terminated connection. Worker thread will terminate now.");
                    break;
                }
            } catch (Exception e) {
                System.err.println("Problem in communicating with the client [" + id + "]. Terminating worker thread.");
                break;
            }
        }

        try {
            this.is.close();
            this.os.close();
            this.socket.close();
        } catch (Exception e) {

        }

        TestServer.workerThreadCount--;
        System.out.println("Client [" + id + "] is now terminating. No. of worker threads = "
                + TestServer.workerThreadCount);
    }



    private static void sendBytes(FileInputStream fis,
                                  OutputStream os) throws Exception {
        // Construct a 1K buffer to hold bytes on their way to the socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;

        // Copy requested file into the socket's output stream.
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String fileName) {
        if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if(fileName.endsWith(".ram") || fileName.endsWith(".ra")) {
            return "audio/x-pn-realaudio";
        }
        return "application/octet-stream" ;
    }
}



