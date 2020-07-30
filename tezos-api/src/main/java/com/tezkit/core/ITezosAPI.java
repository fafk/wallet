package com.tezkit.core;

import com.tezkit.core.crypto.KeyHolder;
import com.tezkit.core.network.TezosRPCException;

import java.math.BigInteger;

public interface ITezosAPI {

    String send(KeyHolder from, String to, BigInteger amount) throws TezosRPCException;
    BigInteger getBalance(String address) throws TezosRPCException;

}
