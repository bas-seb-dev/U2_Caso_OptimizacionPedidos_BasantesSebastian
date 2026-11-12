package com.farmaexpress.app.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import com.farmaexpress.app.R
import com.farmaexpress.app.network.FarmaApiClient
import com.farmaexpress.app.network.MedicamentoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CatalogActivity : AppCompatActivity() {

    private lateinit var btnCargar: Button
    private lateinit var rvMedicamentos: RecyclerView
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        btnCargar = findViewById(R.id.btnCargar)
        rvMedicamentos = findViewById(R.id.rvMedicamentos)
        tvStatus = findViewById(R.id.tvStatus)

        rvMedicamentos.layoutManager = LinearLayoutManager(this)

        btnCargar.setOnClickListener {
            cargarCatalogoDesdeMockoon()
        }
    }

    private fun cargarCatalogoDesdeMockoon() {
        // 1. Limpieza inmediata del RecyclerView en pantalla
        rvMedicamentos.adapter = MedicamentoAdapter(emptyList())

        // 2. Limpieza de la memoria caché de imágenes en Coil
        Coil.imageLoader(this).memoryCache?.clear()

        tvStatus.text = "Conectando con el servidor..."

        FarmaApiClient.service.getMedicamentos().enqueue(object : Callback<MedicamentoResponse> {
            override fun onResponse(call: Call<MedicamentoResponse>, response: Response<MedicamentoResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val medicamentos = response.body()!!.data
                    rvMedicamentos.adapter = MedicamentoAdapter(medicamentos)
                    tvStatus.text = "✅ Se cargaron ${medicamentos.size} productos e imágenes desde Mockoon"
                } else {
                    tvStatus.text = "⚠️ Respuesta HTTP ${response.code()}: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<MedicamentoResponse>, t: Throwable) {
                tvStatus.text = "❌ Error de conexión. No se pudo obtener las imagenes: ${t.message}"
            }
        })
    }
}

