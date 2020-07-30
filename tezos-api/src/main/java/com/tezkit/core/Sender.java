package com.tezkit.core;

import com.tezkit.core.crypto.KeyHolder;
import com.tezkit.core.crypto.Signer;
import com.tezkit.core.crypto.Operations;
import com.tezkit.core.network.TezosRPC;
import com.tezkit.core.network.TezosRPCException;
import lombok.Builder;
import lombok.Data;
import org.bitcoinj.core.Base58;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.bitcoinj.core.Utils.HEX;

/**
 * Transfer tez between accounts.
 */
public class Sender {

    /**
     * Transfer XTZ between accounts. Reveals the public key of the sender if it hasn't been done.
     *
     * @param nodeUrl URL of the node to query
     * @param from TezosKeys for the sender account
     * @param to public key hash (address) of the receiver
     * @param amount amount in micro tez (10e-6 XTZ)
     * @param fee fee in micro tez
     * @return hash of a successfully submitted operation returned by node
     * @throws TezosRPCException communication with node failed
     */
    static public String send(
            String nodeUrl, KeyHolder from, String to, BigInteger amount, BigInteger fee)
    throws TezosRPCException {
        var params = TransferParams.builder().amount(amount).fee(fee).senderKeys(from).to(to).build();
        var signedOperations = buildOperationPayload(nodeUrl, params);

        return TezosRPC.injectOperations(nodeUrl, signedOperations.getSbytes());
    }

    /**
     * Get operations and sign them with a private key.
     *
     * @param nodeUrl node to query
     * @param params  operation params such as fee and limits
     * @return Signed operation payload
     * @throws TezosRPCException communication with node failed
     */
    private static Signer.SignedData buildOperationPayload(String nodeUrl, TransferParams params)
    throws TezosRPCException {
        var blockHash = TezosRPC.getHeadBlock(nodeUrl).getHash();
        var operations = getTransferOps(nodeUrl, blockHash, params);
        var forgedOperations = TezosRPC.forgeOperations(nodeUrl, blockHash, operations);
        var privateKey = params.getSenderKeys().getPrivateKey();

        return Signer.sign(HEX.decode(forgedOperations), privateKey, "03");
    }

    /**
     * Build the payload for a transfer. For accounts with a revealed public key this means just
     * the transaction operation, otherwise bundles in the reveal operation.
     *
     * @param nodeUrl   URL of the node to get information needed to build the payload from
     * @param blockHash latest known block hash
     * @param params    operation params such as fee and limits
     * @return array of operations
     * @throws TezosRPCException communication with node failed
     */
    private static ArrayList<HashMap<String, String>> getTransferOps(
            String nodeUrl, String blockHash, TransferParams params)
    throws TezosRPCException {
        var operations = new ArrayList();
        var pubKeyHash = params.getSenderKeys().getPublicKeyHash();
        var account = TezosRPC.accountStateInBlock(nodeUrl, blockHash, pubKeyHash);
        var counter = Integer.parseInt(account.getCounter()) + 1;

        if (!isRevealed(nodeUrl, pubKeyHash, blockHash)) {
            var pubKeyBytes = Base58.encode(params.getSenderKeys().getPublicKey()).getBytes();
            var encodedPubKey = new String(pubKeyBytes);
            var reveal = Operations.revealWithDefaults(pubKeyHash, encodedPubKey, counter);
            var transfer = Operations.transferWithDefaults(
                    pubKeyHash, params.getTo(), params.getAmount(), counter + 1);
            transfer.put("fee", params.getFee().toString());

            operations.addAll(List.of(reveal, transfer));
        } else {
            var transfer = Operations.transferWithDefaults(
                    pubKeyHash, params.getTo(), params.getAmount(), counter);
            transfer.put("fee", params.getFee().toString());
            operations.add(transfer);
        }

        return operations;
    }

    /**
     * See whether the public key of a public key hash has been revealed on the network
     * by the time of a given block.
     *
     * @param nodeUrl   URL of the node to query
     * @param address   Public key hash (e.g. "tz1SiPXX4MYGNJNDsRc7n8hkvUqFzg8xqF9m") of the account
     * @param blockHash Block hash such as "BKsZChoQiQbHSTXoRWBqHZNgAdqP1Jz5aRuBDnckecQ2fudvxMc"
     * @return Public key has/has not been revealed
     * @throws TezosRPCException communication with node failed
     */
    private static boolean isRevealed(String nodeUrl, String address, String blockHash)
    throws TezosRPCException {
        return TezosRPC.getManagerKey(nodeUrl, blockHash, address).isPresent();
    }

    @Data
    @Builder
    protected static class TransferParams {
        private final BigInteger amount;
        private final BigInteger fee;
        private final String to;
        private final KeyHolder senderKeys;
    }
}
