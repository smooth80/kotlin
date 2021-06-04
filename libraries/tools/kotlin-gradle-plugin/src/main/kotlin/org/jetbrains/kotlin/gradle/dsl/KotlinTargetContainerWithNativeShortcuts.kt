/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.dsl

import groovy.lang.Closure
import org.gradle.util.ConfigureUtil
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.MAIN_COMPILATION_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.TEST_COMPILATION_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet.Companion.COMMON_MAIN_SOURCE_SET_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet.Companion.COMMON_TEST_SOURCE_SET_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetContainer
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

interface KotlinTargetContainerWithNativeShortcuts : KotlinTargetContainerWithPresetFunctions, KotlinSourceSetContainer {

    enum class MacosHosts {
        X64, Arm64, All;

        internal val containsArm64 get() = this == Arm64 || this == All
        internal val containsX64 get() = this == X64 || this == All
    }

    private data class DefaultSourceSets(val main: KotlinSourceSet, val test: KotlinSourceSet)

    private fun KotlinNativeTarget.defaultSourceSets(): DefaultSourceSets =
        DefaultSourceSets(
            compilations.getByName(MAIN_COMPILATION_NAME).defaultSourceSet,
            compilations.getByName(TEST_COMPILATION_NAME).defaultSourceSet
        )

    private fun mostCommonSourceSets() = DefaultSourceSets(
        sourceSets.getByName(COMMON_MAIN_SOURCE_SET_NAME),
        sourceSets.getByName(COMMON_TEST_SOURCE_SET_NAME)
    )

    private fun List<KotlinNativeTarget>.defaultSourceSets(): List<DefaultSourceSets> = map { it.defaultSourceSets() }

    private fun createIntermediateSourceSet(
        name: String,
        children: List<KotlinSourceSet>,
        parent: KotlinSourceSet? = null
    ): KotlinSourceSet =
        sourceSets.maybeCreate(name).apply {
            parent?.let { dependsOn(parent) }
            children.forEach {
                it.dependsOn(this)
            }
        }

    private fun createIntermediateSourceSets(
        namePrefix: String,
        children: List<DefaultSourceSets>,
        parent: DefaultSourceSets? = null
    ): DefaultSourceSets {
        val main = createIntermediateSourceSet("${namePrefix}Main", children.map { it.main }, parent?.main)
        val test = createIntermediateSourceSet("${namePrefix}Test", children.map { it.test }, parent?.test)
        return DefaultSourceSets(main, test)
    }

    fun macos(
        namePrefix: String = "macos",
        configure: KotlinNativeTarget.() -> Unit = {}
    ) {
        val targets = listOf(
            macosX64("${namePrefix}X64"),
            macosArm64("${namePrefix}Arm64")
        )

        createIntermediateSourceSets(namePrefix, targets.defaultSourceSets(), mostCommonSourceSets())
        targets.forEach { it.configure() }
    }

    fun macos() = macos("macos") { }
    fun macos(namePrefix: String) = macos(namePrefix) { }
    fun macos(namePrefix: String, configure: Closure<*>) = macos(namePrefix) { ConfigureUtil.configure(configure, this) }
    fun macos(configure: Closure<*>) = macos { ConfigureUtil.configure(configure, this) }

    fun ios(
        namePrefix: String = "ios",
        supportedHosts: MacosHosts,
        configure: KotlinNativeTarget.() -> Unit = {}
    ) {
        val targets = listOfNotNull(
            iosArm64("${namePrefix}Arm64"),
            if (supportedHosts.containsX64) iosX64("${namePrefix}X64") else null,
            if (supportedHosts.containsArm64) iosSimulatorArm64("${namePrefix}SimulatorArm64") else null
        )
        createIntermediateSourceSets(namePrefix, targets.defaultSourceSets(), mostCommonSourceSets())
        targets.forEach { it.configure() }
    }

    fun ios(supportedHosts: MacosHosts) = ios("ios", supportedHosts) { }
    fun ios(namePrefix: String, supportedHosts: MacosHosts) = ios(namePrefix, supportedHosts) { }
    fun ios(namePrefix: String, supportedHosts: MacosHosts, configure: Closure<*>) =
        ios(namePrefix, supportedHosts) { ConfigureUtil.configure(configure, this) }

    fun ios(supportedHosts: MacosHosts, configure: Closure<*>) =
        ios(supportedHosts = supportedHosts) { ConfigureUtil.configure(configure, this) }

    //region Deprecated iOS shortcuts (using only x64 simulators by default)
    @Deprecated(
        "Supported hosts should be specified explicitly",
        replaceWith = ReplaceWith(
            "ios(namePrefix, MacosHosts.X64, configure)",
            "org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithNativeShortcuts.IosSimulator"
        )
    )
    fun ios(
        namePrefix: String = "ios",
        configure: KotlinNativeTarget.() -> Unit = {}
    ) = ios(namePrefix, MacosHosts.X64, configure)

    @Suppress("DEPRECATION")
    @Deprecated(
        "Supported hosts should be specified explicitly",
        replaceWith = ReplaceWith(
            "ios(MacosHosts.X64)",
            "org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithNativeShortcuts.IosSimulator"
        )
    )
    fun ios() = ios("ios") { }

    @Suppress("DEPRECATION")
    @Deprecated(
        "Supported hosts should be specified explicitly",
        replaceWith = ReplaceWith(
            "ios(namePrefix, MacosHosts.X64)",
            "org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithNativeShortcuts.IosSimulator"
        )
    )
    fun ios(namePrefix: String) = ios(namePrefix) { }

    @Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
    @Deprecated("Supported hosts should be specified explicitly")
    fun ios(namePrefix: String, configure: Closure<*>) =
        ios(namePrefix) { ConfigureUtil.configure(configure, this) }

    @Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
    @Deprecated("Supported hosts should be specified explicitly")
    fun ios(configure: Closure<*>) = ios { ConfigureUtil.configure(configure, this) }
    //endregion

    fun tvos(
        namePrefix: String = "tvos",
        supportedHosts: MacosHosts,
        configure: KotlinNativeTarget.() -> Unit
    ) {
        val targets = listOfNotNull(
            tvosArm64("${namePrefix}Arm64"),
            if (supportedHosts.containsX64) tvosX64("${namePrefix}X64") else null,
            if (supportedHosts.containsArm64) tvosSimulatorArm64("${namePrefix}SimulatorArm64") else null
        )
        createIntermediateSourceSets(namePrefix, targets.defaultSourceSets(), mostCommonSourceSets())
        targets.forEach { it.configure() }
    }

    fun tvos(supportedHosts: MacosHosts) = tvos("tvos", supportedHosts) { }
    fun tvos(namePrefix: String, supportedHosts: MacosHosts) = tvos(namePrefix, supportedHosts) { }
    fun tvos(namePrefix: String, supportedHosts: MacosHosts, configure: Closure<*>) =
        tvos(namePrefix, supportedHosts) { ConfigureUtil.configure(configure, this) }

    fun tvos(supportedHosts: MacosHosts, configure: Closure<*>) =
        tvos(supportedHosts = supportedHosts) { ConfigureUtil.configure(configure, this) }


    //region Deprecated tvos shortcuts (using only x64 simulators by default)
    @Deprecated(
        "Supported hosts should be specified explicitly",
        replaceWith = ReplaceWith(
            "ios(namePrefix, MacosHosts.X64, configure)",
            "org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithNativeShortcuts.IosSimulator"
        )
    )
    fun tvos(
        namePrefix: String = "tvos",
        configure: KotlinNativeTarget.() -> Unit = {}
    ) = tvos(namePrefix, MacosHosts.X64, configure)

    @Suppress("DEPRECATION")
    @Deprecated(
        "Supported hosts should be specified explicitly",
        replaceWith = ReplaceWith(
            "tvos(MacosHosts.X64)",
            "org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithNativeShortcuts.IosSimulator"
        )
    )
    fun tvos() = tvos("tvos") { }

    @Suppress("DEPRECATION")
    @Deprecated(
        "Supported hosts should be specified explicitly",
        replaceWith = ReplaceWith(
            "tvos(namePrefix, MacosHosts.X64)",
            "org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithNativeShortcuts.IosSimulator"
        )
    )
    fun tvos(namePrefix: String) = tvos(namePrefix) { }

    @Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
    @Deprecated("Supported hosts should be specified explicitly")
    fun tvos(namePrefix: String, configure: Closure<*>) =
        tvos(namePrefix) { ConfigureUtil.configure(configure, this) }

    @Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
    @Deprecated("Supported hosts should be specified explicitly")
    fun tvos(configure: Closure<*>) = tvos { ConfigureUtil.configure(configure, this) }
    //endregion

    fun watchos(
        namePrefix: String = "watchos",
        supportedHosts: MacosHosts,
        configure: KotlinNativeTarget.() -> Unit = {}
    ) {
        val device32 = watchosArm32("${namePrefix}Arm32")
        val device64 = watchosArm64("${namePrefix}Arm64")
        val simulatorX64 = if (supportedHosts.containsX64) watchosX64("${namePrefix}X64") else null
        val simulatorArm64 = if (supportedHosts.containsArm64) watchosSimulatorArm64("${namePrefix}SimulatorArm64") else null
        val deviceTargets = listOf(device32, device64)

        val deviceSourceSets = createIntermediateSourceSets(
            "${namePrefix}Device",
            deviceTargets.defaultSourceSets()
        )

        createIntermediateSourceSets(
            namePrefix,
            listOfNotNull(deviceSourceSets, simulatorX64?.defaultSourceSets(), simulatorArm64?.defaultSourceSets()),
            mostCommonSourceSets()
        )

        listOfNotNull(device32, device64, simulatorX64, simulatorArm64).forEach { it.configure() }
    }

    fun watchos(supportedHosts: MacosHosts) = watchos("watchos", supportedHosts) { }
    fun watchos(namePrefix: String, supportedHosts: MacosHosts) = watchos(namePrefix, supportedHosts) { }
    fun watchos(namePrefix: String, supportedHosts: MacosHosts, configure: Closure<*>) =
        watchos(namePrefix, supportedHosts) { ConfigureUtil.configure(configure, this) }
    fun watchos(supportedHosts: MacosHosts, configure: Closure<*>) =
        watchos(supportedHosts = supportedHosts) { ConfigureUtil.configure(configure, this) }


    //region Deprecated watchos shortcuts (using only x64 simulators by default)
    @Deprecated(
        "Supported hosts should be specified explicitly",
        replaceWith = ReplaceWith(
            "watchos(namePrefix, MacosHosts.X64, configure)",
            "org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithNativeShortcuts.IosSimulator"
        )
    )
    fun watchos(
        namePrefix: String = "watchos",
        configure: KotlinNativeTarget.() -> Unit = {}
    ) = watchos(namePrefix, MacosHosts.X64, configure)

    @Suppress("DEPRECATION")
    @Deprecated(
        "Supported hosts should be specified explicitly",
        replaceWith = ReplaceWith(
            "watchos(MacosHosts.X64)",
            "org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithNativeShortcuts.IosSimulator"
        )
    )
    fun watchos() = watchos("watchos") { }

    @Suppress("DEPRECATION")
    @Deprecated(
        "Supported hosts should be specified explicitly",
        replaceWith = ReplaceWith(
            "watchos(namePrefix, MacosHosts.X64)",
            "org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithNativeShortcuts.IosSimulator"
        )
    )
    fun watchos(namePrefix: String) = watchos(namePrefix) { }

    @Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
    @Deprecated("Supported hosts should be specified explicitly")
    fun watchos(namePrefix: String, configure: Closure<*>) =
        watchos(namePrefix) { ConfigureUtil.configure(configure, this) }

    @Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
    @Deprecated("Supported hosts should be specified explicitly")
    fun watchos(configure: Closure<*>) = watchos { ConfigureUtil.configure(configure, this) }
    //endregion
}
