package com.tezkit.core.tezos.operations;

import com.tezkit.core.tezos.Forger;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * An operation data class includes (annotated) properties. These properties are taken in order
 * and serialized by {@link Forger}. BigIntegers don't have to be explicitly
 * annotated, they will by ZArith-encoded by default. Browse through the annotations in this package
 * to see what encoding possibilities there are.
 *
 * Properties in operation classes should be private and must have provided getters!
 *
 * Order of the fields is important, they need to be in the order the node expects them to be.
 */
public abstract class Operation {

    @Getter
    @Setter
    public static BigInteger DEFAULT_GAS_LIMIT = BigInteger.valueOf(15400);
    @Getter
    @Setter
    public static BigInteger DEFAULT_STORAGE_LIMIT = BigInteger.valueOf(300);

}
