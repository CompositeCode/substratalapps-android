package com.substratalapps.hub.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
private data class CatalogExport(
    val apiVersion: String,
    val specVersion: String,
    val screens: List<ScreenEntry>,
)

@Serializable
private data class ScreenEntry(val spec: ScreenSpec, val buildState: BuildState, val navRoute: String, val docsPath: String)

/** Writes [ScreenCatalog] as JSON. The docs site renders its screen reference from this file. */
fun main(args: Array<String>) {
    val out = File(args.firstOrNull() ?: "screens.json")
    out.parentFile?.mkdirs()
    val json = Json { prettyPrint = true; encodeDefaults = true }
    out.writeText(
        json.encodeToString(
            CatalogExport.serializer(),
            CatalogExport(
                ApiVersioning.CURRENT,
                ApiVersioning.SPEC_VERSION,
                ScreenCatalog.all.map { ScreenEntry(it, ImplementationStatus.of(it.id), it.navRoute, it.docsPath) },
            ),
        ),
    )
    println("Wrote ${ScreenCatalog.all.size} screens to $out")
}
