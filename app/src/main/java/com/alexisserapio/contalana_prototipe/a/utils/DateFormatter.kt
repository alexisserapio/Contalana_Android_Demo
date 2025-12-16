package com.alexisserapio.contalana_prototipe.a.utils

import java.util.Date
import java.util.Locale
import java.text.DateFormat // Importamos la clase principal para los estilos
import java.text.SimpleDateFormat // Mantener por si acaso, aunque no la usaremos directamente

fun Long?.toFormattedDateString(
    style: Int, // <-- Cambiamos el tipo de String a Int (estilo)
    locale: Locale = Locale.getDefault()
): String {

    if (this == null) {
        return ""
    }

    // 1. Convertir Long a Date
    val date = Date(this)

    // 2. Crear el formateador usando el estilo y la localización por defecto.
    // Esto crea un patrón culturalmente apropiado (ej. respeta el orden dd/MM/yyyy vs MM/dd/yyyy).
    val formatter: DateFormat = DateFormat.getDateInstance(style, locale)

    // 3. Formatear la Date a String
    return formatter.format(date)
}