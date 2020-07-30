package com.tezkit.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tezkit.core.crypto.KeyHolder;
import com.tezkit.core.network.Network;
import com.tezkit.core.network.TezosRPC;
import com.tezkit.core.network.TezosRPCException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigInteger;
import java.util.List;

/**
 * An interface to the Tezos blockchain. Requires a node specified in the {@link #nodeUrl} field.
 */
@NoArgsConstructor
@Accessors(chain = true)
public class TezosAPI implements ITezosAPI {

    @Getter
    @Setter
    private String nodeUrl = "https://mainnet-tezos.giganode.io";

    @Getter
    @Setter
    private BigInteger defaultFee = BigInteger.valueOf(2940);

    /**
     * Get current balance of an account in micro tez.
     * @param nodeUrl URL of a node to query
     * @param address public key hash
     * @return amount in micro tez (10e-6 tez)
     * @throws TezosRPCException communication with node failed
     */
    static public BigInteger getBalance(String nodeUrl, String address) throws TezosRPCException {
        var url = TezosRPC.buildGetBalanceUrl(nodeUrl, address);

        try {
            return new ObjectMapper().readValue(Network.get(url).body(), BigInteger.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot serialize JSON: " + e.toString());
        }
    }

    /**
     * Transfer XTZ between accounts. Uses default fees and node url unless set otherwise with
     * `.setDefaultFee(BigInteger)` and `.setNodeUrl(String)`.
     * Reveals the public key of the sender if it hasn't been done yet.
     *
     * @param from keys for the source account. Returned by
     *   {@link KeysAPI#keysFromMnemonic(List, String)}
     * @param to public key hash (e.g. "tz1SiPXX4MYGNJNDsRc7n8hkvUqFzg8xqF9m")
     * @param amount amount to transfer in micro tez (1e-6 XTZ)
     * @return hash of a successfully submitted operation returned by node
     * @throws TezosRPCException communication with node failed
     */
    public String send(KeyHolder from, String to, BigInteger amount)
    throws TezosRPCException {
        return Sender.send(getNodeUrl(), from, to, amount, getDefaultFee());
    }

    /**
     * Get the balance of a Tezos account in micro tez (1e-10 XTZ). Uses the default node url
     * unless previously set otherwise with `setNodeUrl(String`.
     *
     * @param address the public key hash of the account (address)
     * @return balance in micro tez
     * @throws TezosRPCException communication with node failed
     */
    public BigInteger getBalance(String address) throws TezosRPCException {
        return getBalance(getNodeUrl(), address);
    }

}
