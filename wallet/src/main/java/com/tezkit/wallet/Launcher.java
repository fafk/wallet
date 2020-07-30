package com.tezkit.wallet;

public class Launcher {

    /**
     * The class containing main() must not extend Application for the maven-shade-plugin to work.
     */
    public static void main(String[] args) {
        com.tezkit.wallet.Main.main(args);
    }
}

