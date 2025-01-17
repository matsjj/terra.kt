package money.terra.util

import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import org.bouncycastle.crypto.signers.ECDSASigner
import org.bouncycastle.crypto.signers.HMacDSAKCalculator
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Sign
import java.math.BigInteger

actual object Bip32 {

    actual fun keyPair(seed: ByteArray, hdPath: IntArray): Bip32KeyPair {
        val masterKey = Bip32ECKeyPair.generateKeyPair(seed)
        val terraHD = Bip32ECKeyPair.deriveKeyPair(masterKey, hdPath)
        val privateKey = terraHD.privateKeyBytes33
        val publicKey = terraHD.publicKeyPoint.getEncoded(true)

        return Bip32KeyPair(publicKey, privateKey)
    }

    actual fun publicKeyFor(privateKey: ByteArray): ByteArray {
        return Sign.publicPointFromPrivate(BigInteger(1, privateKey)).getEncoded(true)
    }

    actual fun sign(messageHash: ByteArray, privateKey: ByteArray): ByteArray {
        val keyPair = Bip32ECKeyPair.create(privateKey)

        val signature = Sign.signMessage(messageHash, keyPair, false)

        return concatRandS(signature.r, signature.s)
    }

    private val CURVE = ECDomainParameters(
        Sign.CURVE_PARAMS.curve,
        Sign.CURVE_PARAMS.g,
        Sign.CURVE_PARAMS.n,
        Sign.CURVE_PARAMS.h
    )

    actual fun verify(messageHash: ByteArray, publicKey: ByteArray, signature: ByteArray): Boolean {
        val (r, s) = splitRandS(signature)
        val signer = ECDSASigner(HMacDSAKCalculator(SHA256Digest()))
        val publicKeyParameters = ECPublicKeyParameters(CURVE.curve.decodePoint(publicKey), CURVE)
        signer.init(false, publicKeyParameters)

        return signer.verifySignature(messageHash, BigInteger(1, r), BigInteger(1, s))
    }

    actual fun recoverPublicKey(messageHash: ByteArray, signature: ByteArray): ByteArray {
        val (r, s) = splitRandS(signature)

        return Sign.signedMessageToKey(messageHash, Sign.SignatureData(byteArrayOf(), r, s)).toByteArray()
    }
}