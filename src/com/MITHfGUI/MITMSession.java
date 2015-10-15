package com.MITHfGUI;

import javax.swing.*;

/**
 * Created by root on 15/10/15.
 */
public class MITMSession extends JFrame
{
    private Boolean ARP, HSTS, screenShot, keyLogger;
    private String dir, inter, params;
    private TerminalManager tm;

    public MITMSession(Boolean ARP, Boolean HSTS, Boolean screenShot, Boolean keyLogger, String dir, String inter, String params)
    {
        this.ARP = ARP;
        this.HSTS = HSTS;
        this.screenShot = screenShot;
        this.keyLogger = keyLogger;
        this.dir = dir;
        this.inter = inter;
        this.params = params;
        init();
    }

    private void init()
    {
        //cd to the script
        String command = "cd " + dir;

        //Build the command
        command = command.concat(" && python mitmf.py -i " + inter + " ");

        if(ARP)
            command = command.concat("--arp --spoof ");
        if(HSTS)
            command = command.concat("--hsts --dns ");
        if(screenShot)
            command = command.concat("--screen ");
        if(keyLogger)
            command = command.concat("--jskeylogger ");
        if(!params.isEmpty())
            command = command.concat(params);

        //Start the terminal.
        tm = new TerminalManager(command);
    }

    public TerminalManager getTm()
    {
        return tm;
    }
}
