package com.tezkit.core;

import com.tezkit.core.crypto.KeyHolder;
import com.tezkit.core.network.RPCException;
import com.tezkit.core.network.TzStatsRPC;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class E2ETest {

    private final List<String> mnemonic =
            List.of(("success banner steak finger cradle puppy time " +
                    "reason castle minute parrot pony segment piano mad").split(" "));
    private final KeyHolder keys = KeysAPI.keysFromMnemonic(mnemonic, "pass");

    @Test
    public void realSendTest() throws RPCException {
        // This tx with 0 fee is not likely to be included in a block
        new TezosAPI()
                .setDefaultFee(BigInteger.valueOf(0))
                .send(keys, "tz1UPm1EoC7ZQ6EGHZ5LuHf37NnBrSixwgzq", new BigInteger("1"));
    }

    @Test
    public void tzStats() throws ExecutionException, InterruptedException {
        assertNotNull(TzStatsRPC.getExchangeRate().get());
        assertNotNull(TzStatsRPC.getAllBalanceChanges("tz1UPm1EoC7ZQ6EGHZ5LuHf37NnBrSixwgzq"));
    }
}
