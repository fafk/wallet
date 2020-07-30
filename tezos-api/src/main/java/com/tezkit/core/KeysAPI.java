package com.tezkit.core;

import com.goterl.lazycode.lazysodium.SodiumJava;
import com.tezkit.core.crypto.KeyHolder;
import com.tezkit.core.crypto.TezosKeys;
import org.bitcoinj.core.Base58;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;

import java.io.IOException;
import java.util.List;

public class KeysAPI {

    /**
     * Generate a new mnemonic of 15 words.
     *
     * @return new ranom mnemonic
     */
    public static List<String> generateMnemonic() {
        byte[] bytes = new byte[20];
        new java.util.Random().nextBytes(bytes);
        try {
            return new MnemonicCode().toMnemonic(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Mnemonic gen failed unexpectedly. " + e.toString());
        } catch (MnemonicException.MnemonicLengthException e) {
            e.printStackTrace();
            throw new RuntimeException("Mnemonic gen failed unexpectedly. " + e.toString());
        }
    }

    /**
     * Get tezos public key, private key and public key hash given a mnemonic phrase and
     * a passphrase.
     *
     * @param mnemonicWords 15 mnemonic words
     * @param passphrase additional passphrase, can be an empty string
     * @return a data structure with pub key, private key and pub key hash  with tezos prefixes
     */
    static public KeyHolder keysFromMnemonic(List<String> mnemonicWords, String passphrase) {
        byte[] seed = TezosKeys.getSeedBytesFromMnemonic(mnemonicWords, passphrase);
        byte[] sodiumPrivateKey = new byte[(32 * 2)];
        byte[] sodiumPublicKey = new byte[32];

        // Ed25519
        new SodiumJava().crypto_sign_seed_keypair(sodiumPublicKey, sodiumPrivateKey, seed);

        return KeyHolder.builder()
                .publicKey(TezosKeys.getTezosPublicKey(sodiumPublicKey))
                .publicKeyHash(Base58.encode(TezosKeys.getTezosPublicKeyHash(sodiumPublicKey)))
                .privateKey(TezosKeys.getTezosPrivateKey(sodiumPrivateKey))
                .build();
    }

}
