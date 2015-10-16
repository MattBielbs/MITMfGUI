package com.MITHfGUI;


import com.sun.deploy.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;

/**
 * Created by root on 15/10/15.
 */
public class TerminalManager extends JFrame implements Runnable
{
    private ProcessBuilder ps;
    private  BufferedReader in, error;
    private BufferedWriter bw;
    private Process pr;
    private Thread thread;

    //GUI elements
    private DefaultListModel keylog;
    private ImageIcon screen;


    public TerminalManager(String dir, List<String> commands)
    {
        super(StringUtils.join(commands, " "));

        try
        {
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            //BuildGUI
            buildGUI();
            //Start the script
            init(dir, commands);

            Runtime.getRuntime().addShutdownHook(new Thread()
            {
                public void run()
                {
                        kill();
                }
            });

            setSize(600 , 300);
            setVisible(true);
        }
        catch(Exception ex)
        {
            System.out.println("Failed to init Terminal session!\n" + ex.getMessage());
            Main.running = false;
        }
    }

    private void buildGUI()
    {
        setLayout(new BorderLayout());
        add(new JList(keylog = new DefaultListModel()), BorderLayout.CENTER);

    }

    private void init(String dir, List<String> commands) throws IOException
    {
        Main.running = true;

        //Setup the process
        ps = new ProcessBuilder(commands);
        ps.directory(new File(dir));
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
        {
            try
            {
                String cmds[] = {"killall","python"};
                Runtime.getRuntime().exec(cmds);
            }
            catch(Exception ex)
            {
                System.out.println("Error sending escape sequence :(");
            }
        }
    }

    private int getIndexOfLog(String host, String field)
    {
        for(int i = 0; i < keylog.size(); i++)
        {
            String text = (String)keylog.elementAt(i);
            if(text.contains(host) && text.contains(field))
                return i;
        }
        return -1;
    }

    private void addKeyLog(String log)
    {
       // keylog.addElement(log);
        if(log.contains("Injected"))
        {
            keylog.add(0, log.substring(log.indexOf("Injected")));
        }
        else
        {
            String host = log.substring(log.indexOf("Host: "), log.indexOf(" | Field:"));
            String field = log.substring(log.indexOf("Field: "), log.indexOf(" | Keys:"));
            String keys = log.substring(log.indexOf("Keys: "));

            int index = getIndexOfLog(host, field);
            if(index > -1)
            {
                String item = (String)keylog.get(index);
                String keytemp = item.substring(item.indexOf("Keys: "));

                keytemp = keytemp.concat(keys);
                keylog.set(index, host + " | " + field + " | " + keytemp);
            }
            else
                keylog.add(0, host + " | " + field + " | " + keys);
        }
        //2015-10-16 11:37:20 192.168.43.176 [type:Iceweasel-38 os:Linux] [JSKeylogger] Injected JS file: justgaythings.com
        //2015-10-16 11:37:21 192.168.43.176 [type:Iceweasel-38 os:Linux] [JSKeylogger] Host: justgaythings.com | Field: s | Keys: i
        //2015-10-16 11:37:21 192.168.43.176 [type:Iceweasel-38 os:Linux] [JSKeylogger] Host: justgaythings.com | Field: s | Keys: i
    }

    private void addScreen(String screen)
    {
        //2015-10-16 11:37:17 192.168.43.176 [type:Iceweasel-38 os:Linux] [ScreenShotter] Saved screenshot to 192.168.43.176-justgaythings.com-2015-10-16_11:37:17:1444948637.png
        //2015-10-16 11:37:20 192.168.43.176 [type:Iceweasel-38 os:Linux] [ScreenShotter] Injected JS payload: justgaythings.com
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
                    if(line.contains("[JSKeylogger]"))
                        addKeyLog(line);
                    if(line.contains("[ScreenShotter]"))
                        addScreen(line);

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

                //Force output
                pr.getOutputStream().flush();
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
