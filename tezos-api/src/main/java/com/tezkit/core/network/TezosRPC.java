package com.tezkit.core.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class TezosRPC {

    /**
     * Get the latest block of the blockchain.
     * @param nodeUrl URL of the node to query.
     * @return block data transfer object
     * @throws TezosRPCException failed query
     */
    public static BlockDTO getHeadBlock(String nodeUrl) throws TezosRPCException {
        try {
            return new ObjectMapper().readValue(
                    Network.get(buildHeadBlockUrl(nodeUrl)).body(),
                    BlockDTO.class);
        } catch (Exception e) {
            throw new TezosRPCException("Failed to get block: " + e.toString());
        }
    }

    /**
     * Get the balance and operation counter by an address (public key hash) by a certain block.
     *
     * @param nodeUrl URL of a node to query
     * @param blockHash block hash as a point in time for this account state
     * @param address address of the account (such as "tz1VeaJWkdr2m5YaKFgeAafenhHhDcMxWfHC")
     * @return account data transfer object
     * @throws TezosRPCException failed query
     */
    public static AccountDTO accountStateInBlock(String nodeUrl, String blockHash, String address)
    throws TezosRPCException {
        try {
            return new ObjectMapper().readValue(
                    Network.get(buildAccountForBlock(nodeUrl, blockHash, address)).body(),
                    AccountDTO.class);
        } catch (Exception e) {
            throw new TezosRPCException("Cannot get account for block: " + e.toString());
        }
    }

    /**
     * Get public key associated with a public key hash (an address).
     *
     * @param nodeUrl URL of the node to query
     * @param blockHash block hash capturing the point in the blockchain state
     * @param address address of the account (such as "tz1VeaJWkdr2m5YaKFgeAafenhHhDcMxWfHC")
     * @return null or pubkey as json (`"edpktrYQdsQHZfHmRWS7QzbnVb43L1gTH8is3aXyDokcySxJmB8wbX"`)
     * which we cast into an Optional of string
     */
    public static Optional<String> getManagerKey(String nodeUrl, String blockHash, String address)
    throws TezosRPCException {
        try {
            var result = new ObjectMapper().readValue(
                    Network.get(buildManagerKey(nodeUrl, blockHash, address)).body(),
                    String.class);
            return result == null ? Optional.empty() : Optional.of(result);
        } catch (Exception e) {
            throw new TezosRPCException("Cannot get manager key: " + e.toString());
        }
    }

    /**
     * Broadcast to the blockchain.
     *
     * @param nodeUrl URL of the node to query
     * @param transaction encoded (= forged) transaction
     * @return a hash of the submitted transaction
     * @throws TezosRPCException failed query
     */
    public static String injectOperations(String nodeUrl, String transaction)
    throws TezosRPCException {
        try {
            var response = Network.post(
                    nodeUrl + "/injection/operation?chain=main",
                    new ObjectMapper().writeValueAsString(transaction))
                    .body();
            return new ObjectMapper().readValue(response, String.class);
        } catch (Exception e) {
            throw new TezosRPCException("Failed serializing operation: " + e.toString());
        }
    }

    /**
     * Pass in JSON and get the operations back encoded. Such encoded tx can be later signed and
     * injected to the blockchain.
     *
     * @param nodeUrl URL of the node to query
     * @param operations operations to encode
     * @return encoded operations
     * @throws TezosRPCException failed query
     */
    public static String forgeOperations(
            String nodeUrl, String blockHash, List<HashMap<String, String>> operations)
    throws TezosRPCException {
        var payload = new HashMap();
        payload.put("branch", blockHash);
        payload.put("contents", operations);
        try {
            var opGroup = new ObjectMapper().writeValueAsString(payload);

            return new ObjectMapper().readValue(
                    Network.post(buildForgeOp(nodeUrl), opGroup).body(),
                    String.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to serialize JSON: " + e.toString());
        }
    }

    private static String buildForgeOp(String nodeUrl) {
        return new StringBuilder()
                .append(nodeUrl)
                .append("/chains/main/blocks/head/helpers/forge/operations")
                .toString();
    }

    private static String buildManagerKey(String nodeUrl, String blockHash, String accountId) {
        return new StringBuilder()
                .append(nodeUrl)
                .append("/chains/main/blocks/")
                .append(blockHash)
                .append("/context/contracts/")
                .append(accountId)
                .append("/manager_key")
                .toString();
    }

    private static String buildAccountForBlock(String nodeUrl, String blockHash, String accountId) {
        return new StringBuilder()
                .append(nodeUrl)
                .append("/chains/main/blocks/")
                .append(blockHash)
                .append("/context/contracts/")
                .append(accountId)
                .toString();
    }

    private static String buildHeadBlockUrl(String nodeUrl) {
        return new StringBuilder()
                .append(nodeUrl)
                .append("/chains/main/blocks/head/header")
                .toString();
    }

    public static String buildGetBalanceUrl(String nodeUrl, String address) {
        return new StringBuilder()
                .append(nodeUrl)
                .append("/chains/main/blocks/head/context/contracts/")
                .append(address)
                .append("/balance")
                .toString();
    }


}
