package com.tezkit.wallet.platforms.mac;

import com.tezkit.wallet.Main;

import java.awt.*;
import java.net.URL;

public class MacOS {

    static public void init() {
        setDockIcon();
    }

    static void setDockIcon() {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        final URL imageResource = Main.class.getResource("/icons/Logo-white@2x.png");
        final java.awt.Image image = defaultToolkit.getImage(imageResource);

        //this is new since JDK 9
        final Taskbar taskbar = Taskbar.getTaskbar();

        try {
            //set icon for mac os (and other systems which do support this method)
            taskbar.setIconImage(image);
        } catch (final UnsupportedOperationException e) {
            System.out.println("The os does not support: 'taskbar.setIconImage'");
        } catch (final SecurityException e) {
            System.out.println("There was a security exception for: 'taskbar.setIconImage'");
        }
    }
}
