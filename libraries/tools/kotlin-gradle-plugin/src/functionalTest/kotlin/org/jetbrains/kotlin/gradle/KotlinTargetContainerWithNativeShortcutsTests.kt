/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

/* Associate compilations are not yet supported by the IDE. KT-34102 */
@file:Suppress("invisible_reference", "invisible_member", "FunctionName", "DuplicatedCode")

package org.jetbrains.kotlin.gradle

import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithNativeShortcuts.MacosHosts.All
import org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithNativeShortcuts.MacosHosts.Arm64
import org.jetbrains.kotlin.gradle.dsl.multiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMetadataTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.KonanTarget.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class KotlinTargetContainerWithNativeShortcutsTests {

    private lateinit var project: ProjectInternal
    private lateinit var kotlin: KotlinMultiplatformExtension

    @BeforeTest
    fun setup() {
        project = ProjectBuilder.builder().build() as ProjectInternal
        project.plugins.apply("kotlin-multiplatform")
        kotlin = project.multiplatformExtension
    }

    @Test
    fun `test ios(supportedHosts = Arm64) shortcut`() {
        kotlin.ios(Arm64)
        assertEquals(setOf(IOS_ARM64, IOS_SIMULATOR_ARM64), registeredKonanTargets())
        assertEquals(setOf("iosArm64", "iosSimulatorArm64"), registeredTargetNames())
    }

    @Test
    fun `test ios(supportedHosts = All) shortcut`() {
        kotlin.ios(All)
        assertEquals(setOf(IOS_ARM64, IOS_SIMULATOR_ARM64, IOS_X64), registeredKonanTargets())
        assertEquals(setOf("iosArm64", "iosSimulatorArm64", "iosX64"), registeredTargetNames())
    }

    @Test
    fun `test ios() shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.ios()
        assertEquals(setOf(IOS_ARM64, IOS_X64), registeredKonanTargets())
        assertEquals(setOf("iosArm64", "iosX64"), registeredTargetNames())
    }

    @Test
    fun `test ios(namePrefix) shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.ios("customIos")
        assertEquals(setOf(IOS_ARM64, IOS_X64), registeredKonanTargets())
        assertEquals(setOf("customIosArm64", "customIosX64"), registeredTargetNames())
    }

    @Test
    fun `test ios {} shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.ios {}
        assertEquals(setOf(IOS_ARM64, IOS_X64), registeredKonanTargets())
        assertEquals(setOf("iosArm64", "iosX64"), registeredTargetNames())
    }

    @Test
    fun `test ios(namePrefix) {} shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.ios("customIos") {}
        assertEquals(setOf(IOS_ARM64, IOS_X64), registeredKonanTargets())
        assertEquals(setOf("customIosArm64", "customIosX64"), registeredTargetNames())
    }

    @Test
    fun `test tvos() shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.tvos()
        assertEquals(setOf(TVOS_ARM64, TVOS_X64), registeredKonanTargets())
        assertEquals(setOf("tvosArm64", "tvosX64"), registeredTargetNames())
    }

    @Test
    fun `test tvos(namePrefix) shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.tvos("customTvos")
        assertEquals(setOf(TVOS_ARM64, TVOS_X64), registeredKonanTargets())
        assertEquals(setOf("customTvosArm64", "customTvosX64"), registeredTargetNames())
    }

    @Test
    fun `test tvos {} shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.tvos {}
        assertEquals(setOf(TVOS_ARM64, TVOS_X64), registeredKonanTargets())
        assertEquals(setOf("tvosArm64", "tvosX64"), registeredTargetNames())
    }

    @Test
    fun `test tvos(namePrefix) {} shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.tvos("customTvos") {}
        assertEquals(setOf(TVOS_ARM64, TVOS_X64), registeredKonanTargets())
        assertEquals(setOf("customTvosArm64", "customTvosX64"), registeredTargetNames())
    }

    @Test
    fun `test tvos(Arm64) shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.tvos(Arm64)
        assertEquals(setOf(TVOS_ARM64, TVOS_SIMULATOR_ARM64), registeredKonanTargets())
        assertEquals(setOf("tvosArm64", "tvosSimulatorArm64"), registeredTargetNames())
    }

    @Test
    fun `test tvos(namePrefix, Arm64) shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.tvos("customTvos", Arm64)
        assertEquals(setOf(TVOS_ARM64, TVOS_SIMULATOR_ARM64), registeredKonanTargets())
        assertEquals(setOf("customTvosArm64", "customTvosSimulatorArm64"), registeredTargetNames())
    }

    @Test
    fun `test tvos(Arm64) {} shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.tvos(supportedHosts = Arm64) {}
        assertEquals(setOf(TVOS_ARM64, TVOS_SIMULATOR_ARM64), registeredKonanTargets())
        assertEquals(setOf("tvosArm64", "tvosSimulatorArm64"), registeredTargetNames())
    }

    @Test
    fun `test tvos(namePrefix, Arm64) {} shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.tvos("customTvos", Arm64) {}
        assertEquals(setOf(TVOS_ARM64, TVOS_SIMULATOR_ARM64), registeredKonanTargets())
        assertEquals(setOf("customTvosArm64", "customTvosSimulatorArm64"), registeredTargetNames())
    }

    @Test
    fun `test watchos() shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.watchos()
        assertEquals(setOf(WATCHOS_ARM32, WATCHOS_ARM64, WATCHOS_X64), registeredKonanTargets())
        assertEquals(setOf("watchosArm32", "watchosArm64", "watchosX64"), registeredTargetNames())
    }

    @Test
    fun `test watchos(namePrefix) shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.watchos("customWatchos")
        assertEquals(setOf(WATCHOS_ARM32, WATCHOS_ARM64, WATCHOS_X64), registeredKonanTargets())
        assertEquals(setOf("customWatchosArm32", "customWatchosArm64", "customWatchosX64"), registeredTargetNames())
    }

    @Test
    fun `test watchos {} shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.watchos {}
        assertEquals(setOf(WATCHOS_ARM32, WATCHOS_ARM64, WATCHOS_X64), registeredKonanTargets())
        assertEquals(setOf("watchosArm32", "watchosArm64", "watchosX64"), registeredTargetNames())
    }

    @Test
    fun `test watchos(namePrefix) {} shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.watchos("customWatchos") {}
        assertEquals(setOf(WATCHOS_ARM32, WATCHOS_ARM64, WATCHOS_X64), registeredKonanTargets())
        assertEquals(setOf("customWatchosArm32", "customWatchosArm64", "customWatchosX64"), registeredTargetNames())
    }

    @Test
    fun `test watchos(Arm64) shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.watchos(Arm64)
        assertEquals(setOf(WATCHOS_ARM32, WATCHOS_ARM64, WATCHOS_SIMULATOR_ARM64), registeredKonanTargets())
        assertEquals(setOf("watchosArm32", "watchosArm64", "watchosSimulatorArm64"), registeredTargetNames())
    }

    @Test
    fun `test watchos(namePrefix, Arm64) shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.watchos("customWatchos", Arm64)
        assertEquals(setOf(WATCHOS_ARM32, WATCHOS_ARM64, WATCHOS_SIMULATOR_ARM64), registeredKonanTargets())
        assertEquals(setOf("customWatchosArm32", "customWatchosArm64", "customWatchosSimulatorArm64"), registeredTargetNames())
    }

    @Test
    fun `test watchos(Arm64) {} shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.watchos(supportedHosts = Arm64) {}
        assertEquals(setOf(WATCHOS_ARM32, WATCHOS_ARM64, WATCHOS_SIMULATOR_ARM64), registeredKonanTargets())
        assertEquals(setOf("watchosArm32", "watchosArm64", "watchosSimulatorArm64"), registeredTargetNames())
    }

    @Test
    fun `test watchos(namePrefix, Arm64) {} shortcut`() {
        @Suppress("DEPRECATION")
        kotlin.watchos("customWatchos", Arm64) {}
        assertEquals(setOf(WATCHOS_ARM32, WATCHOS_ARM64, WATCHOS_SIMULATOR_ARM64), registeredKonanTargets())
        assertEquals(setOf("customWatchosArm32", "customWatchosArm64", "customWatchosSimulatorArm64"), registeredTargetNames())
    }

    private fun registeredKonanTargets() = kotlin.targets.toList().filterIsInstance<KotlinNativeTarget>()
        .map { it.konanTarget }.toSet()

    private fun registeredTargetNames() = kotlin.targets.filter { it !is KotlinMetadataTarget }
        .map { it.name }.toSet()
}
