package com.farmaexpress.app.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.math.pow
import kotlin.random.Random

class BackoffRetryInterceptor(
    private val maxRetries: Int = 3,
    private val initialDelayMs: Long = 1000L
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var exception: IOException? = null
        var tryCount = 0

        while (tryCount < maxRetries) {
            try {
                val sanitizedUrl = request.url.toString().replace(Regex("(token|cedula|apiKey)=[^&]+"), "$1=[REDACTED]")
                println(" 📊 [Telemetría Intento ${tryCount + 1}]: Solicitando a $sanitizedUrl")

                response = chain.proceed(request)

                if (response.isSuccessful || !isTransientError(response.code)) {
                    return response
                }

                response.close()
            } catch (e: IOException) {
                exception = e
            }

            tryCount++
            if (tryCount < maxRetries) {
                // Cálculo de Exponential Backoff + Jitter aleatorio [3, 6]
                val backoffMs = (initialDelayMs * 2.0.pow(tryCount.toDouble())).toLong()
                val jitterMs = Random.nextLong(0, 300)
                val totalDelay = backoffMs + jitterMs

                println(" ⚠️ [Resiliencia Network]: Error transitorio (HTTP ${response?.code}). Reintento $tryCount/$maxRetries en ${totalDelay}ms...")
                Thread.sleep(totalDelay)
            }
        }

        if (response != null) return response
        throw exception ?: IOException("Fallo de red persistente tras $maxRetries intentos.")
    }

    private fun isTransientError(statusCode: Int): Boolean {
        return statusCode == 429 || statusCode in 500..599
    }
}
