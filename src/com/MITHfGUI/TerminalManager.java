package com.MITHfGUI;

import java.io.*;

/**
 * Created by root on 15/10/15.
 */
public class TerminalManager implements Runnable
{
    private ProcessBuilder ps;
    private  BufferedReader in, error;
    private BufferedWriter bw;
    private Process pr;
    private Thread thread;

    public TerminalManager(String command)
    {
        try
        {
            init(command);

            Runtime.getRuntime().addShutdownHook(new Thread()
            {
                public void run()
                {
                        kill();
                }
            });
        }
        catch(Exception ex)
        {
            System.out.println("Failed to init Terminal session!");
            Main.running = false;
        }
    }

    private void init(String command) throws IOException
    {
        Main.running = true;

        //Setup the process
        ps = new ProcessBuilder("/bin/sh", "-c", command);
        ps.redirectErrorStream(true);
        pr = ps.start();

        in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        error = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
        bw = new BufferedWriter(new OutputStreamWriter(pr.getOutputStream()));


        //Start the loop
        thread = new Thread(this);
        thread.start();
    }

    public void kill()
    {
        if(pr.isAlive())
            pr.destroy();

        try
        {
            bw.write('\u0003');
        }
        catch(Exception ex)
        {
            System.out.println("Error sending escape sequence :(");
        }

        thread.interrupt();
        thread.interrupt();
    }

    @Override
    public void run()
    {
        while(Main.running)
        {
            try
            {
                String line, err;

                if ((line = in.readLine()) != null)
                {
                    System.out.println(line);

                }
                else if ((err = error.readLine()) != null)
                {
                    System.out.println(err);
                }
                else
                {
                    Main.running = false;
                    pr.waitFor();
                    System.out.println("Script execution halted!");
                    in.close();
                }

                System.out.flush();
            }
            catch(Exception ex)
            {
                System.out.println("Error in terminal: " + ex.getMessage());
                Main.running = false;
            }
        }

        System.out.println("TerminalManager Halted!");
    }
}
