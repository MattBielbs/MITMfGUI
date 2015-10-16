package com.MITHfGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by root on 15/10/15.
 */
public class GUI extends JFrame implements ActionListener
{
    private JTextArea dir, params, inter;
    private JCheckBox arp, hsts, screen, keylogger;
    private JButton launch, kill;
    private MITMSession session;

    public GUI()
    {
        super("MITHfGUI - v0.9.8 (The Dark Side)");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        buildGUI();
        setSize(500,250);
        setVisible(true);
    }

    private void buildGUI()
    {
        setLayout(new BorderLayout());

        //Main settings
        JPanel settings = new JPanel(new FlowLayout());
        settings.setPreferredSize(new Dimension(500, 150));

        settings.add(new JLabel("MITMf Directory:"));
        settings.add(dir = new JTextArea("/root/MITMf/"));
        dir.setPreferredSize(new Dimension(450, 20));
        dir.setBackground(Color.LIGHT_GRAY);

        settings.add(new JLabel("Interface:"));
        settings.add(inter = new JTextArea("wlan0"));
        inter.setPreferredSize(new Dimension(450, 20));
        inter.setBackground(Color.LIGHT_GRAY);

        settings.add(new JLabel("Additional Params:"));
        settings.add(params = new JTextArea(""));
        params.setPreferredSize(new Dimension(450, 20));
        params.setBackground(Color.LIGHT_GRAY);

        //Checkboxes
        JPanel checks = new JPanel(new FlowLayout());
        checks.add(arp = new JCheckBox("ARP", true));
        checks.add(hsts = new JCheckBox("HSTS", true));
        checks.add(screen = new JCheckBox("ScreenShot", true));
        checks.add(keylogger = new JCheckBox("Keylogger", true));

        //Launch button
        launch = new JButton("Launch");
        launch.addActionListener(this);

        //Kill button
        kill = new JButton("Kill");
        kill.addActionListener(this);


        //Main content
        add(settings, BorderLayout.NORTH);
        add(checks, BorderLayout.CENTER);

        //Buttons
        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(launch);
        buttons.add(kill);
        add(buttons, BorderLayout.SOUTH);

        revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand() == "Launch")
        {
            if (Main.running)
                JOptionPane.showMessageDialog(null, "A MITMf session is currently in progress.", "Error", JOptionPane.ERROR_MESSAGE);
            else
                session = new MITMSession(arp.isSelected(), hsts.isSelected(), screen.isSelected(), keylogger.isSelected(), dir.getText(), inter.getText(), params.getText());
        }

        if(e.getActionCommand() == "Kill")
        {
            if(!Main.running && session != null)
                JOptionPane.showMessageDialog(null, "No running session to kill.", "Error", JOptionPane.ERROR_MESSAGE);
            else
                session.getTm().kill();
        }
    }
}
