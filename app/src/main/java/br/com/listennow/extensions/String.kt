package br.com.listennow.extensions

const val IS_VALID_UUID_REGEX = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-8][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$"

/**
 * Return trues if uuid matches regex.
 */
fun String.isValidUUID(): Boolean {
    return IS_VALID_UUID_REGEX.toRegex().matches(this)
}