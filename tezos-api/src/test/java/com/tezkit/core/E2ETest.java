package com.tezkit.core;

import com.tezkit.core.crypto.KeyHolder;
import com.tezkit.core.network.TezosRPCException;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

public class E2ETest {

    private final List<String> mnemonic =
            List.of(("success banner steak finger cradle puppy time " +
                     "reason castle minute parrot pony segment piano mad").split(" "));
    private final KeyHolder keys = KeysAPI.keysFromMnemonic(mnemonic, "pass");

    @Test
    public void realSendTest() throws TezosRPCException {
        new TezosAPI().send(
                keys, "tz1UPm1EoC7ZQ6EGHZ5LuHf37NnBrSixwgzq", new BigInteger("1"));
    }
}
