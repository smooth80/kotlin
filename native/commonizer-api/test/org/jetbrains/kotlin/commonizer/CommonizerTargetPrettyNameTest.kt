/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.commonizer

import org.junit.Test
import kotlin.test.assertEquals

class CommonizerTargetPrettyNameTest {

    @Test
    fun leafTargetNames() {
        listOf(
            Triple("foo", "[foo]", FOO),
            Triple("bar", "[bar]", BAR),
            Triple("baz_123", "[baz_123]", BAZ),
        ).forEach { (name, prettyName, target: LeafCommonizerTarget) ->
            assertEquals(name, target.name)
            assertEquals(prettyName, target.prettyName)
        }
    }

    @Test
    fun sharedTargetNames() {
        listOf(
            "[foo]" to SharedTarget(FOO),
            "[bar, foo]" to SharedTarget(FOO, BAR),
            "[bar, baz_123, foo]" to SharedTarget(FOO, BAR, BAZ),
        ).forEach { (prettyName, target: SharedCommonizerTarget) ->
            assertEquals(prettyName, target.prettyName)
        }
    }

    @Test
    fun prettyCommonizedName() {
        val sharedTarget = SharedTarget(FOO, BAR, BAZ)
        listOf(
            "[bar, baz_123, foo(*)]" to FOO,
            "[bar(*), baz_123, foo]" to BAR,
            "[bar, baz_123(*), foo]" to BAZ,
            "[bar, baz_123, foo]" to sharedTarget,
        ).forEach { (prettyCommonizerName, target: CommonizerTarget) ->
            assertEquals(prettyCommonizerName, sharedTarget.prettyName(target))
        }
    }

    @Test
    fun sharedTargetNoInnerTargets() {
        assertEquals(
            "[]", SharedCommonizerTarget(emptySet<LeafCommonizerTarget>()).prettyName
        )
    }

    private companion object {
        val FOO = LeafCommonizerTarget("foo")
        val BAR = LeafCommonizerTarget("bar")
        val BAZ = LeafCommonizerTarget("baz_123")

        @Suppress("TestFunctionName")
        fun SharedTarget(vararg targets: LeafCommonizerTarget) = SharedCommonizerTarget(linkedSetOf(*targets))
    }
}
