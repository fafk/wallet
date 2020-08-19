package com.tezkit.core;

import com.tezkit.core.crypto.KeyHolder;
import com.tezkit.core.network.RPCException;

import java.math.BigInteger;

public interface ITezosAPI {

    String send(KeyHolder from, String to, BigInteger amount) throws RPCException;
    BigInteger getBalance(String address) throws RPCException;

}
