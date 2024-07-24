package id.walt.mdoc.mso

import id.walt.mdoc.dataelement.TDateElementOptionalTag
import id.walt.mdoc.dataelement.toDE
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Time-wise validity information for a mobile security object
 * @param signed Time of creation of the MSO signature
 * @param validFrom Time before which the MSO is not yet valid
 * @param validUntil Time after which the MSO is no longer valid
 * @param expectedUpdate Time at which the issuing authority expects to re-sign the MSO, default: null
 */
@Serializable
class ValidityInfo private constructor(
    val signed: TDateElementOptionalTag,
    val validFrom: TDateElementOptionalTag,
    val validUntil: TDateElementOptionalTag,
    val expectedUpdate: TDateElementOptionalTag? = null
) {

    /**
     * Time-wise validity information for a mobile security object
     * @param signed Time of creation of the MSO signature
     * @param validFrom Time before which the MSO is not yet valid
     * @param validUntil Time after which the MSO is no longer valid
     * @param expectedUpdate Time at which the issuing authority expects to re-sign the MSO, default: null
     */
    constructor(signed: Instant, validFrom: Instant, validUntil: Instant, expectedUpdate: Instant? = null)
            : this(
        TDateElementOptionalTag(signed),
        TDateElementOptionalTag(validFrom),
        TDateElementOptionalTag(validUntil),
        expectedUpdate?.let { TDateElementOptionalTag(it) })

    /**
     * Convert to CBOR map element
     */
    fun toMapElement() = buildMap {
        put("signed", signed)
        put("validFrom", validFrom)
        put("validUntil", validUntil)
        expectedUpdate?.let { put("expectedUpdate", it) }
    }.toDE()
}
