package info.laht.threekt.renderers.renderers.gl

import info.laht.threekt.*
import info.laht.threekt.materials.ShaderMaterial
import info.laht.threekt.renderers.Renderer
import java.lang.IllegalArgumentException

class GLProgram(
    val renderer: Renderer,
    val code: String,
    val material: ShaderMaterial
) {

    val id = programIdCount++

    private companion object {

        var programIdCount = 0

        fun getEncodingComponents(encoding: Int): Pair<String, String> {

            return when (encoding) {

                LinearEncoding -> "Linear" to "( value )"
                sRGBEncoding -> "sRGB" to "( value )"
                RGBEEncoding -> "RGBE" to "( value )"
                RGBM7Encoding -> "RGBM" to "( value, 7.0 )"
                RGBM16Encoding -> "RGBM" to "( value, 16.0 )"
                RGBDEncoding -> "RGBD" to "( value, 256.0 )"
                GammaEncoding -> "Gamma" to "( value, float( GAMMA_FACTOR ) )"
                else -> throw IllegalArgumentException("unsupported encoding: $encoding")

            }

        }

        fun getEncodingComponents(functionName: String, toneMapping : Int): String {

            val toneMappingName = when (toneMapping) {

                LinearToneMapping -> "Linear"
                ReinhardToneMapping -> "Reinhard"
                Uncharted2ToneMapping -> "Uncharted2"
                CineonToneMapping -> "OptimizedCineon"
                ACESFilmicToneMapping -> "ACESFilmic"
                else -> throw IllegalArgumentException("unsupported toneMapping: $toneMapping")

            }

            return "vec3 " + functionName + "( vec3 color ) { return " + toneMappingName + "ToneMapping( color ); }";

        }

    }


}