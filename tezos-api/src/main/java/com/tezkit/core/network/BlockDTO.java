package com.tezkit.core.network;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockDTO {
    String hash;
    String protocol;

    @JsonProperty("chain_id")
    String chainId;
    Integer level;
    Integer proto;
    String timestamp;

    @JsonProperty("validation_pass")
    Integer validationPass;

    @JsonProperty("operations_hash")
    String operationHash;
    List<String> fitness;
    Integer priority;
    String signature;

    @JsonProperty("proof_of_work_nonce")
    String proofOfWorkNonce;
}
