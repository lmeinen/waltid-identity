package id.walt.credentials.verification.policies.vp

import id.walt.credentials.schemes.JwsSignatureScheme.JwsOption
import id.walt.credentials.verification.HolderBindingException
import id.walt.credentials.verification.JwtVerificationPolicy
import id.walt.crypto.utils.JwsUtils.decodeJws
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class HolderBindingPolicy : JwtVerificationPolicy(
    "holder-binding",
    "Verifies that issuer of the Verifiable Presentation (presenter) is also the subject of all Verifiable Credentials contained within."
) {
    override suspend fun verify(credential: String, args: Any?, context: Map<String, Any>): Result<Any> {
        val jws = credential.decodeJws()

        val payload = jws.payload

        val presenterDid = payload[JwsOption.ISSUER]!!.jsonPrimitive.content

        val vp = payload["vp"]?.jsonObject ?: throw IllegalArgumentException("No \"vp\" field in VP!")

        val credentials =
            vp["verifiableCredential"]?.jsonArray ?: throw IllegalArgumentException("No \"verifiableCredential\" field in \"vp\"!")

        val credentialSubjects = credentials.map {
            it.jsonPrimitive.content.decodeJws().payload["sub"]!!.jsonPrimitive.content.split("#").first()
        }

        return when {
            credentialSubjects.all { it == presenterDid } -> Result.success(presenterDid)
            else -> Result.failure(
                HolderBindingException(
                    presenterDid = presenterDid,
                    credentialDids = credentialSubjects
                )
            )
        }
    }
}
