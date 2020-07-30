package com.tezkit.core;

import com.tezkit.core.crypto.Hash;
import com.tezkit.core.crypto.KeyHolder;
import com.tezkit.core.crypto.Signer;
import com.tezkit.core.crypto.TezosKeys;
import org.bitcoinj.core.Base58;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.powermock.tests.utils.Keys;

import java.util.List;

//import static org.junit.Assert.assertEquals;
import static org.bitcoinj.core.Utils.HEX;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KeysAPITest {

    private final List<String> mnemonic =
            List.of(("success banner steak finger cradle puppy time" +
                    " reason castle minute parrot pony segment piano mad").split(" "));
    private final KeyHolder keys = KeysAPI.keysFromMnemonic(mnemonic, "pass");

    KeysAPITest() {
    }

    @Test
    public void generateKeysTest() {
        assertEquals("tz1YH4wtBGRHsiZFsVqYRKFWWeuPY5HW1B6r", keys.getPublicKeyHash());
        assertEquals(
                "0d0f25d958c01698e1b3ae1ea3157222e84a27a42f3a054c1a91d3e7125dc97089808ae0f6a2ef3c",
                Hex.toHexString(keys.getPublicKey()));
        assertEquals(
                "2bf64e074b03ca8cb3a86fc3e4985edf4bb815bf1e4c209c67f878d2b54b96dc0538729" +
                        "458c01698e1b3ae1ea3157222e84a27a42f3a054c1a91d3e7125dc97089808ae0b72df8d2",
                Hex.toHexString(keys.getPrivateKey()));
    }

    @Test
    public void generateMnemonicTest() {
        var mnemonic = KeysAPI.generateMnemonic();
        assertEquals(15, mnemonic.size());
    }

    @Test
    public void testSigning() {
        byte[] bytes = {5};
        var signed = Signer.sign(bytes, keys.getPrivateKey(), "03");
        assertEquals(
                "61650ad7d98479ef3f6e8c30dedfbb011ac92177df707a2e39add2232786eab8a75aa5882c45f58781f33f5aa8dea4f06b80e2ea3b9be4510b419394112f680b",
                signed.getSig());
        assertEquals(
                "0561650ad7d98479ef3f6e8c30dedfbb011ac92177df707a2e39add2232786eab8a75aa5882c45f58781f33f5aa8dea4f06b80e2ea3b9be4510b419394112f680b",
                signed.getSbytes());
        assertEquals(
                "05",
                signed.getBytes());
        assertEquals(
                "edsigtkYoGJ4BxTBGZYiHmpdECtyp5nNWtTEenUrGz4v3rc2sz8MWF1Srzu8hqufGiy3rvB2c1X45XAJM7p1o2HMeokDj5XYEe8",
                signed.getEdsig());
    }

    @Test
    public void tezosKeysTest() {
        var wordsArr = (
                "slender, undo, sock, vast, rate, true, ridge, domain, already, basket, " +
                "vague, shuffle, wave, blouse, like").split(", ");
        var keys = KeysAPI.keysFromMnemonic(List.of(wordsArr), "pass");
        assertEquals(
                "edpkvB3E6ST6UyCP5txjmNTqcpT3gEp9LRJb7iqY9WB1YZPjJiFgpg",
                Base58.encode(keys.getPublicKey()));
        assertEquals(
                "edskRryqR79KBMPyRY9Jq9FJozv3apnPfraMknm5mLbsxP7AHZDdRyNXTRTb44rYpvP3DPfgbAUDC6MVxDzesHWzVhKxtVX5ro",
                Base58.encode(keys.getPrivateKey()));
        assertEquals("tz1Z2qBexCKuV8MJzWrApAJZWhSWx6g5GfRF", keys.getPublicKeyHash());
    }

    @Test
    public void blake2bTest() {
        byte[] bytes = {1, 2, 4};
        byte[] hash = Hash.computeGenericHash(bytes, 8);
        assertEquals("6c166c8a6dd17ea0", HEX.encode(hash));
    }

}
