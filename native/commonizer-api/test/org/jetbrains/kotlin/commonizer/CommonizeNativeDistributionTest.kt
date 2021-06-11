/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.commonizer

import org.jetbrains.kotlin.commonizer.utils.konanHome
import org.jetbrains.kotlin.konan.target.KonanTarget.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder


class CommonizeNativeDistributionTest {

    @get:Rule
    val temporaryOutputDirectory = TemporaryFolder()

    @Test
    fun commonizeLinuxPlatforms() {
        CliCommonizer(this::class.java.classLoader).commonizeNativeDistribution(
            konanHome = konanHome,
            outputTargets = setOf(
                CommonizerTarget(LINUX_X64, LINUX_ARM64),
                CommonizerTarget(LINUX_X64, LINUX_ARM64, LINUX_ARM32_HFP)
            ),
            outputDirectory = temporaryOutputDirectory.root,
            logLevel = CommonizerLogLevel.Info
        )
    }
}