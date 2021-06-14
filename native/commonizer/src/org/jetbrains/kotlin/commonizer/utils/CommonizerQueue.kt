/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.commonizer.utils

import org.jetbrains.kotlin.commonizer.*
import org.jetbrains.kotlin.commonizer.core.CommonizationVisitor
import org.jetbrains.kotlin.commonizer.mergedtree.CirCommonizedClassifierNodes
import org.jetbrains.kotlin.commonizer.mergedtree.CirKnownClassifiers
import org.jetbrains.kotlin.commonizer.transformer.Checked.Companion.invoke
import org.jetbrains.kotlin.commonizer.transformer.InlineTypeAliasCirNodeTransformer
import org.jetbrains.kotlin.commonizer.tree.CirTreeRoot
import org.jetbrains.kotlin.storage.LockBasedStorageManager

internal object CommonizerQueue {


    fun runSmarter(parameters: CommonizerParameters) {
        val storageManager = LockBasedStorageManager("Declarations commonization")


        val lazyLeafCirTress: Map<LeafCommonizerTarget, CirTreeRoot?> = parameters.outputTargets.allLeaves().associateWith { target ->
            deserialize(parameters, target)
        }

        parameters.logProgress("Deserialized libraries")

        parameters.outputTargets.forEach loop@{ target ->
            val classifiers = CirKnownClassifiers(
                commonizedNodes = CirCommonizedClassifierNodes.default(),
                commonDependencies = parameters.dependencyClassifiers(target)
            )

            val cirTrees = EagerTargetDependent(target.targets) {
                lazyLeafCirTress.getValue(it as LeafCommonizerTarget)
            }

            val mergedTree = merge(storageManager, classifiers, cirTrees) ?: return@loop
            InlineTypeAliasCirNodeTransformer(storageManager, classifiers).invoke(mergedTree)
            mergedTree.accept(CommonizationVisitor(classifiers, mergedTree), Unit)
            parameters.logProgress("Commonized declarations for $target")

            serialize(parameters, mergedTree, target)
        }
    }
}