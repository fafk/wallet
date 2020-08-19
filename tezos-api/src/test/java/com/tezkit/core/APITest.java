package com.tezkit.core;

import com.tezkit.core.crypto.KeyHolder;
import com.tezkit.core.network.*;
import com.tezkit.core.tezos.Forger;
import com.tezkit.core.tezos.operations.Operation;
import com.tezkit.core.tezos.operations.Reveal;
import com.tezkit.core.tezos.operations.Transaction;
import org.bitcoinj.core.Base58;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigInteger;
import java.util.*;

import static com.tezkit.core.tezos.operations.Operation.DEFAULT_GAS_LIMIT;
import static com.tezkit.core.tezos.operations.Operation.DEFAULT_STORAGE_LIMIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;

@RunWith(PowerMockRunner.class) // requires JUnit 4
@PrepareForTest({TezosRPC.class, Network.class, Forger.class})
@PowerMockIgnore({"javax.crypto.*"})
public class APITest {

    private final List<String> mnemonic =
            List.of(("success banner steak finger cradle puppy time" +
                    " reason castle minute parrot pony segment piano mad").split(" "));
    private final KeyHolder keys = KeysAPI.keysFromMnemonic(mnemonic, "pass");
    String blockHash = "BM7Bvkbj1KeJeLMcRfoZGJvDaKBy8m4VZmqoHN29AiWm3zn7EUM";

    public APITest() {
    }

    @Test
    public void sendTestWithRevealTest() throws RPCException {
        PowerMockito.mockStatic(TezosRPC.class);
        PowerMockito.mockStatic(Forger.class);
        PowerMockito.when(TezosRPC.getHeadBlock(any()))
                .thenReturn(BlockDTO.builder().hash(blockHash).build());
        PowerMockito.when(TezosRPC.accountStateInBlock(any(), any(), any()))
                .thenReturn(AccountDTO.builder().balance("1").counter("3").build());
        PowerMockito.when(TezosRPC.getManagerKey(any(), any(), any()))
                .thenReturn(Optional.empty());
        PowerMockito.when(Forger.encodePayload(any(), any()))
                .thenReturn("ffff");
        PowerMockito.when(TezosRPC.injectOperations(any(), any())).thenReturn("op_hash");

        var result = new TezosAPI().send(keys, "tz3RB4aoyjov4KEVRbuhvQ1CKJgBJMWhaeB8", BigInteger.valueOf(123));

        PowerMockito.verifyStatic(TezosRPC.class);
        TezosRPC.injectOperations(any(), any());
        PowerMockito.verifyStatic(Forger.class);
        ArgumentCaptor opsCaptor = ArgumentCaptor.forClass(ArrayList.class);
        Forger.encodePayload(anyString(), (List<? extends Operation>) opsCaptor.capture());

        var ops = (ArrayList) opsCaptor.getValue();
        var reveal = (Reveal) ops.get(0);
        var tx = (Transaction) ops.get(1);
        assertEquals(2, ops.size());
        assertTrue(reveal instanceof Reveal);
        assertTrue(tx instanceof Transaction);
        assertEquals(keys.getPublicKeyHash(), reveal.getSource());
        assertEquals("6b", reveal.getKind());
        assertTrue(
                Arrays.equals(
                        Base58.decode("edpkuKJwnM2voj5gjya8aYnqGMqS6rthfWzkQfjBrnYNoTm1C6cR6K"),
                        reveal.getPublicKey()));
        assertEquals("4", reveal.getCounter().toString());
        assertEquals(DEFAULT_GAS_LIMIT, reveal.getGasLimit());
        assertEquals(DEFAULT_STORAGE_LIMIT, reveal.getStorageLimit());
        assertEquals("6c", tx.getKind());
        assertEquals("123", tx.getAmount().toString());
        assertEquals("5", tx.getCounter().toString());
        assertEquals("tz3RB4aoyjov4KEVRbuhvQ1CKJgBJMWhaeB8", tx.getDestination());
        assertEquals(keys.getPublicKeyHash(), tx.getSource());
        assertEquals(DEFAULT_GAS_LIMIT, tx.getGasLimit());
        assertEquals(DEFAULT_STORAGE_LIMIT, tx.getStorageLimit());
        assertEquals("op_hash", result);
    }

    @Test
    public void sendWithoutRevealTest() throws RPCException {
        PowerMockito.mockStatic(TezosRPC.class);
        PowerMockito.mockStatic(Forger.class);
        PowerMockito.when(TezosRPC.getHeadBlock(any()))
                .thenReturn(BlockDTO.builder().hash(blockHash).build());
        PowerMockito.when(TezosRPC.accountStateInBlock(any(), any(), any()))
                .thenReturn(AccountDTO.builder().balance("1").counter("3").build());
        PowerMockito.when(TezosRPC.getManagerKey(any(), any(), any()))
                .thenReturn(Optional.of("key"));
        PowerMockito.when(Forger.encodePayload(any(), any()))
                .thenReturn("ffff");
        PowerMockito.when(TezosRPC.injectOperations(any(), any())).thenReturn("op_hash");

        new TezosAPI()
                .setNodeUrl("url")
                .setDefaultFee(BigInteger.valueOf(111))
                .send(keys, "receiver", BigInteger.valueOf(123));

        PowerMockito.verifyStatic(TezosRPC.class);
        TezosRPC.getHeadBlock(eq("url"));
        PowerMockito.verifyStatic(TezosRPC.class);
        TezosRPC.accountStateInBlock(eq("url"), eq(blockHash), eq(keys.getPublicKeyHash()));
        PowerMockito.verifyStatic(TezosRPC.class);
        TezosRPC.getManagerKey(eq("url"), eq(blockHash), eq(keys.getPublicKeyHash()));
        ArgumentCaptor opsCaptor = ArgumentCaptor.forClass(ArrayList.class);
        PowerMockito.verifyStatic(Forger.class);
        Forger.encodePayload(anyString(), (List<? extends Operation>) opsCaptor.capture());
//        PowerMockito.verifyStatic(TezosRPC.class);
        var ops = (ArrayList) opsCaptor.getValue();
        var tx = (Transaction) ops.get(0);
        assertEquals(1, ops.size());
        assertEquals("111", tx.getFee().toString());
        // send off bytes + signature
        TezosRPC.injectOperations(
                eq("url"),
                eq("ffff21b5afe2586ad8a66152ee59ed9b452d97b7ad8c9aa73f9196bc12ee2b3c3dea57d89900633a7fb112223d1caae5eb86aa3b9ecf6ca95fc8fdf2344f69bc2007"));
    }

}
