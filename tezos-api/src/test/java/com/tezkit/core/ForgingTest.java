package com.tezkit.core;

import com.tezkit.core.tezos.Forger;
import com.tezkit.core.tezos.operations.Reveal;
import com.tezkit.core.tezos.operations.Transaction;
import org.bitcoinj.core.Base58;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static com.tezkit.core.tezos.Forger.zarith;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ForgingTest {

    String blockHash = "BM7Bvkbj1KeJeLMcRfoZGJvDaKBy8m4VZmqoHN29AiWm3zn7EUM";

    @Test
    public void forgeRevealAndTransferTest() {
        var reveal = Reveal.builder()
                .counter(BigInteger.valueOf(13))
                .fee(BigInteger.valueOf(7866))
                .gasLimit(BigInteger.valueOf(3984))
                .storageLimit(BigInteger.valueOf(9))
                .publicKey(Base58.decode("edpkvCq9fHmAukBpFurMwR7YVukezNW7GCdQop3PJsGCo62t5MDeNw"))
                .source("tz1i6q8g1dcUha9PkKYpt3NtaXiQDLdLPSVn")
                .build();
        var tx = Transaction.builder()
                .source("tz1LSAycAVcNdYnXCy18bwVksXci8gUC2YpA")
                .fee(BigInteger.valueOf(34567123))
                .counter(BigInteger.valueOf(8))
                .gasLimit(BigInteger.valueOf(56787))
                .storageLimit(BigInteger.valueOf(0))
                .amount(BigInteger.valueOf(54321))
                .destination("tz1LSAycAVcNdYnXCy18bwVksXci8gUC2YpA")
                .build();
        assertEquals(
                Forger.encodePayload(blockHash, List.of(reveal, tx)),
                "b7b56ccee0a001ee3eff22e36ecba1ab94d08924c931fed1aa2afcba288fb8d56b00f6645" +
                        "951a38c13586cda1edb9bdbebcf34a50773ba3d0d901f0900cdbc0c3449784bd53907c3c" +
                        "7a06060cf12087e492a7b937f044c6a73b522a2346c0008ba0cb2fad622697145cf16651" +
                        "24096d25bc31ed3e7bd1008d3bb0300b1a803000008ba0cb2fad622697145cf166512409" +
                        "6d25bc31e00");
    }

    @Test
    public void forgeTx() {
        var tx = Transaction.builder()
                .source("tz1LSAycAVcNdYnXCy18bwVksXci8gUC2YpA")
                .fee(BigInteger.valueOf(34567123))
                .counter(BigInteger.valueOf(8))
                .gasLimit(BigInteger.valueOf(56787))
                .storageLimit(BigInteger.valueOf(0))
                .amount(BigInteger.valueOf(54321))
                .destination("tz1LSAycAVcNdYnXCy18bwVksXci8gUC2YpA")
                .build();
        assertEquals(
                Forger.encodePayload(blockHash, List.of(tx)),
                "b7b56ccee0a001ee3eff22e36ecba1ab94d08924c931fed1aa2afcba288fb8d56c0008ba0" +
                        "cb2fad622697145cf1665124096d25bc31ed3e7bd1008d3bb0300b1a803000008ba0cb2f" +
                        "ad622697145cf1665124096d25bc31e00");
    }

    @Test
    public void forgeTxWithKt1Destination() {
        var tx = Transaction.builder()
                .source("tz1KtGwriE7VuLwT3LwuvU9Nv4wAxP7XZ57d")
                .fee(BigInteger.valueOf(56723))
                .counter(BigInteger.valueOf(87988))
                .gasLimit(BigInteger.valueOf(9875))
                .storageLimit(BigInteger.valueOf(2342356))
                .amount(BigInteger.valueOf(67846))
                .destination("KT1RZsEGgjQV5iSdpdY3MHKKHqNPuL9rn6wy")
                .build();
        assertEquals(
                Forger.encodePayload(blockHash, List.of(tx)),
                "b7b56ccee0a001ee3eff22e36ecba1ab94d08924c931fed1aa2afcba288fb8d56c0002b1b" +
                        "8e2338ea7bf67ef23ff1277cbc7d4b6842493bb03b4af05934dd4fb8e0186920401ba4e7" +
                        "349ac25dc5eb2df5a43fceacc58963df4f50000");
    }

    @Test
    public void forgeRevealAsPayload() {
        var reveal = Reveal.builder()
                .counter(BigInteger.valueOf(13))
                .fee(BigInteger.valueOf(7866))
                .gasLimit(BigInteger.valueOf(3984))
                .storageLimit(BigInteger.valueOf(9))
                .publicKey(Base58.decode("edpkvCq9fHmAukBpFurMwR7YVukezNW7GCdQop3PJsGCo62t5MDeNw"))
                .source("tz1i6q8g1dcUha9PkKYpt3NtaXiQDLdLPSVn")
                .build();
        assertEquals(
                Forger.encodePayload(blockHash, List.of(reveal)),
                "b7b56ccee0a001ee3eff22e36ecba1ab94d08924c931fed1aa2afcba288fb8d56b00f6645" +
                        "951a38c13586cda1edb9bdbebcf34a50773ba3d0d901f0900cdbc0c3449784bd53907c3c" +
                        "7a06060cf12087e492a7b937f044c6a73b522a234");
    }

    @Test
    public void ZArithTest() {
        assertEquals("ffffffffffffff0f", zarith(new BigInteger("9007199254740991")));
        assertEquals("8080808080808010", zarith(new BigInteger("9007199254740992")));
        assertEquals("8180808080808010", zarith(new BigInteger("9007199254740993")));
        assertEquals("8280808080808010", zarith(new BigInteger("9007199254740994")));
    }

    @Test
    public void brancEncoding() {
        assertEquals(
                "a732d3520eeaa3de98d78e5e5cb6c85f72204fd46feb9f76853841d4a701add3",
                Forger.branch("BLyvCRkxuTXkx1KeGvrcEXiPYj4p1tFxzvFDhoHE7SFKtmP1rbk"));
    }

    @Test
    public void addressEncoding() {
        assertEquals(
                "000008ba0cb2fad622697145cf1665124096d25bc31e",
                Forger.address("tz1LSAycAVcNdYnXCy18bwVksXci8gUC2YpA"));
    }

    @Test
    public void publicKeyEncodingTest() {
        var inputKey = "edpkvCq9fHmAukBpFurMwR7YVukezNW7GCdQop3PJsGCo62t5MDeNw";

        assertEquals(
                "00cdbc0c3449784bd53907c3c7a06060cf12087e492a7b937f044c6a73b522a234",
                Forger.publicKey(Base58.decode(inputKey)));
    }

}
