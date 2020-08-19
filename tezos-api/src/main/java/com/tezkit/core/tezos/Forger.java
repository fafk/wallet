package com.tezkit.core.tezos;

import com.tezkit.core.crypto.Base58Check;
import com.tezkit.core.crypto.TezosKeys;
import com.tezkit.core.tezos.operations.*;
import org.bitcoinj.core.Base58;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.bitcoinj.core.Utils.HEX;

/**
 * Forge (= encode) transactions to inject them to the network.
 */
public class Forger {

    public static byte[] blPrefix = {1, 52};
    public static Integer addressPrefixLength = 3; // in bytes

    /**
     * Encode a payload with operations to be submitted (injected) to the network.
     * @param blockHash
     * @param ops operations to submit
     * @return payload as hexadecimals
     * @throws FailedToGetValueException
     */
    public static String encodePayload(String blockHash, List<? extends Operation> ops)
    throws FailedToGetValueException {
        var encodedOps = ops.stream().map(Forger::encodeOperation).collect(Collectors.joining());
        return branch(blockHash) + encodedOps;
    }

    /**
     * Encode a single operation.
     * @param data operation object
     * @return hexadecimals
     * @throws FailedToGetValueException
     */
    public static String encodeOperation(Operation data) throws FailedToGetValueException {
        return Arrays.stream(data.getClass().getDeclaredFields()).map(field -> {
            var value = getObjectValue(data, field);
            var fieldType = field.getType();
            if (fieldType.equals(BigInteger.class)) return zarith((BigInteger) value);
            if (fieldType.equals(byte[].class)) {
                if (field.isAnnotationPresent(PublicKeyEncoding.class)) {
                    return publicKey((byte[]) value);
                }
                return HEX.encode((byte[]) value);
            }
            if (fieldType.equals(String.class)) {
                if (field.isAnnotationPresent(AddressEncoding.class)) return address((String) value);
                if (field.isAnnotationPresent(PublicKeyHashEncoding.class)) return publicKeyHash((String) value);
                if (field.isAnnotationPresent(NoEncoding.class)) return (String) value;
                throw new CannotEncodeException("Unannotated string field: " + field.getName());
            }
            if (fieldType.equals(Operation.class)) return encodeOperation((Operation) value);

            throw new CannotEncodeException("Field: " + field.getName());
        }).collect(Collectors.joining());
    }

    /**
     * Encoding for numbers.
     * @param num BigInteger to encode
     * @return hexa string of encoded number
     */
    public static String zarith(BigInteger num) {
        if (num == null) return "";

        ArrayList<String> out = new ArrayList<>();
        while (true) {
            if (num.compareTo(BigInteger.valueOf(128)) < 0) {
                if (num.compareTo(BigInteger.valueOf(16)) < 0) out.add("0");
                out.add(num.toString(16));
                break;
            } else {
                var b = num.mod(BigInteger.valueOf(128));
                num = num.subtract(b);
                num = num.divide(BigInteger.valueOf(128));
                b = b.add(BigInteger.valueOf(128));
                out.add(b.toString(16));
            }
        }
        return String.join("", out);
    }

    /**
     * Encode public key by removing its checksum, prefix and
     * @param tezosPublicKey
     * @return
     */
    public static String publicKey(byte[] tezosPublicKey) {
        var base58pubKey = Base58.encode(tezosPublicKey);
        var rawKey = TezosKeys.rawKeyFromPublicKey(tezosPublicKey);
        if (base58pubKey.startsWith("edpk")) return "00" + HEX.encode(rawKey);
        if (base58pubKey.startsWith("sppk")) return "01" + HEX.encode(rawKey);
        if (base58pubKey.startsWith("p2pk")) return "02" + HEX.encode(rawKey);
        throw new CannotEncodeException("Failed to encode pubkey " + tezosPublicKey);
    }

    /**
     * Encode block hash by removing checksum, prefix and encoding as hexa.
     * @param blockHash block hash to encode
     * @return hexadecimals
     */
    public static String branch(String blockHash) {
        return HEX.encode(Base58Check.decode(blockHash)).substring(blPrefix.length * 2);
    }

    /**
     * Encode an address (prefixes tz1, tz2, tz3 or kt1).
     * @param address account public key hash or smart contract address
     * @return hexa encoded address
     */
    public static String address(String address) {
        if (address.toLowerCase().startsWith("kt1")) {
            return "01" + getEncodedPublicKey(address) + "00";
        }
        return "00" + publicKeyHash(address);
    }

    /**
     * Encode public key hash.
     * @param publicKeyHash
     * @return hexa encoded public key hash
     * @throws CannotEncodeException
     */
    public static String publicKeyHash(String publicKeyHash) throws CannotEncodeException {
        String encodedPubKey = getEncodedPublicKey(publicKeyHash);
        if (publicKeyHash.toLowerCase().startsWith("tz1")) return "00" + encodedPubKey;
        if (publicKeyHash.toLowerCase().startsWith("tz2")) return "01" + encodedPubKey;
        if (publicKeyHash.toLowerCase().startsWith("tz3")) return "02" + encodedPubKey;
        throw new CannotEncodeException("Failed to encode address " + publicKeyHash);
    }

    /**
     * Remove checksum and prefix and and encode as hexadecimals.
     * @param publicKeyHash
     * @return hexa decimals
     */
    private static String getEncodedPublicKey(String publicKeyHash) {
        return HEX.encode(Base58Check.decode(publicKeyHash)).substring(addressPrefixLength * 2);
    }

    /**
     * Get field value from an object. Find a getter, calls it and return found value.
     * @param data object to get data from
     * @param field field of the object
     * @return retval of the field's getter
     * @throws FailedToGetValueException
     */
    private static Object getObjectValue(Object data, Field field) throws FailedToGetValueException {
        try {
            return new PropertyDescriptor(field.getName(), data.getClass()).getReadMethod().invoke(data);
        } catch (Exception e) {
            throw new FailedToGetValueException(e.toString());
        }
    }

}
