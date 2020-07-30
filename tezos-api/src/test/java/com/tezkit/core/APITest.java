package com.tezkit.core;

import com.tezkit.core.crypto.KeyHolder;
import com.tezkit.core.network.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TezosRPC.class, Network.class})
@PowerMockIgnore({"javax.crypto.*"})
public class APITest {

    private final List<String> mnemonic =
            List.of(("success banner steak finger cradle puppy time" +
                    " reason castle minute parrot pony segment piano mad").split(" "));
    private final KeyHolder keys = KeysAPI.keysFromMnemonic(mnemonic, "pass");

    public APITest() { }

    @Test
    public void sendTestWithRevealTest() throws TezosRPCException {
        PowerMockito.mockStatic(TezosRPC.class);
        PowerMockito.when(TezosRPC.getHeadBlock(any()))
                .thenReturn(BlockDTO.builder().hash("block_hash").build());
        PowerMockito.when(TezosRPC.accountStateInBlock(any(), any(), any()))
                .thenReturn(AccountDTO.builder().balance("1").counter("3").build());
        PowerMockito.when(TezosRPC.getManagerKey(any(), any(), any()))
                .thenReturn(Optional.empty());
        PowerMockito.when(TezosRPC.forgeOperations(any(), any(), any()))
                .thenReturn("ffff");
        PowerMockito.when(TezosRPC.injectOperations(any(), any())).thenReturn("op_hash");

        var result = new TezosAPI().send(keys, "receiver", BigInteger.valueOf(123));

        PowerMockito.verifyStatic(TezosRPC.class);
        TezosRPC.injectOperations(any(), any());
        PowerMockito.verifyStatic(TezosRPC.class);
        ArgumentCaptor opsCaptor = ArgumentCaptor.forClass(ArrayList.class);
        TezosRPC.forgeOperations(any(), any(), (List<HashMap<String, String>>) opsCaptor.capture());

        var ops = (ArrayList) opsCaptor.getValue();
        assertEquals(2, ops.size());
        assertEquals(keys.getPublicKeyHash(), ((HashMap<String, String>) ops.get(0)).get("source"));
        assertEquals("reveal", ((HashMap<String, String>) ops.get(0)).get("kind"));
        assertEquals(
                "edpkuKJwnM2voj5gjya8aYnqGMqS6rthfWzkQfjBrnYNoTm1C6cR6K",
                ((HashMap<String, String>) ops.get(0)).get("public_key"));
        assertEquals("4", ((HashMap<String, String>) ops.get(0)).get("counter"));
        assertEquals("transaction", ((HashMap<String, String>) ops.get(1)).get("kind"));
        assertEquals("123", ((HashMap<String, String>) ops.get(1)).get("amount"));
        assertEquals("5", ((HashMap<String, String>) ops.get(1)).get("counter"));
        assertEquals("receiver", ((HashMap<String, String>) ops.get(1)).get("destination"));
        assertEquals(keys.getPublicKeyHash(), ((HashMap<String, String>) ops.get(1)).get("source"));
        assertEquals("op_hash", result);
    }

    @Test
    public void sendWithoutRevealTest() throws TezosRPCException {
        PowerMockito.mockStatic(TezosRPC.class);
        PowerMockito.when(TezosRPC.getHeadBlock(any()))
                .thenReturn(BlockDTO.builder().hash("block_hash").build());
        PowerMockito.when(TezosRPC.accountStateInBlock(any(), any(), any()))
                .thenReturn(AccountDTO.builder().balance("1").counter("3").build());
        PowerMockito.when(TezosRPC.getManagerKey(any(), any(), any()))
                .thenReturn(Optional.of("key"));
        PowerMockito.when(TezosRPC.forgeOperations(any(), any(), any()))
                .thenReturn("ffff");
        PowerMockito.when(TezosRPC.injectOperations(any(), any())).thenReturn("op_hash");

        new TezosAPI()
                .setNodeUrl("url")
                .setDefaultFee(BigInteger.valueOf(111))
                .send(keys, "receiver", BigInteger.valueOf(123));

        PowerMockito.verifyStatic(TezosRPC.class);
        TezosRPC.getHeadBlock(eq("url"));
        PowerMockito.verifyStatic(TezosRPC.class);
        TezosRPC.accountStateInBlock(eq("url"), eq("block_hash"), eq(keys.getPublicKeyHash()));
        PowerMockito.verifyStatic(TezosRPC.class);
        TezosRPC.getManagerKey(eq("url"), eq("block_hash"), eq(keys.getPublicKeyHash()));
        ArgumentCaptor opsCaptor = ArgumentCaptor.forClass(ArrayList.class);
        PowerMockito.verifyStatic(TezosRPC.class);
        TezosRPC.forgeOperations(
                eq("url"), eq("block_hash"), (List<HashMap<String, String>>) opsCaptor.capture());
        PowerMockito.verifyStatic(TezosRPC.class);
        var ops = (ArrayList) opsCaptor.getValue();
        assertEquals(1, ops.size());
        assertEquals("111", ((HashMap<String, String>) ops.get(0)).get("fee"));
        // send off bytes + signature
        TezosRPC.injectOperations(
                eq("url"),
                eq("ffff21b5afe2586ad8a66152ee59ed9b452d97b7ad8c9aa73f9196bc12ee2b3c3dea57d89900633a7fb112223d1caae5eb86aa3b9ecf6ca95fc8fdf2344f69bc2007"));
    }

}
