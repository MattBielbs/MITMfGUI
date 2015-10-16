package com.MITHfGUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

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
        List<String> commands = new ArrayList<String>();
        commands.add("python");
        commands.add("mitmf.py");
        commands.add("-i");
        commands.add(inter);

        if(ARP)
        {
            commands.add("--arp");
            commands.add("--spoof");
        }
        if(HSTS)
        {
            commands.add("--hsts");
            commands.add("--dns");
        }
        if(screenShot)
            commands.add("--screen");
        if(keyLogger)
            commands.add("--jskeylogger");

        if(!params.isEmpty())
        {
            String[] split = params.split(" ");
            for(String s : split)
                commands.add(s);
        }

        //Start the terminal.
        tm = new TerminalManager(dir, commands);
    }

    public TerminalManager getTm()
    {
        return tm;
    }
}
