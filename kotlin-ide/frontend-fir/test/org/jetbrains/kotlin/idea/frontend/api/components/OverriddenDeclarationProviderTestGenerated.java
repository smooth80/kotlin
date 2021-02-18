/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.frontend.api.components;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("idea/idea-frontend-fir/testData/components/overridenDeclarations")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class OverriddenDeclarationProviderTestGenerated extends AbstractOverriddenDeclarationProviderTest {
    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
    }

    public void testAllFilesPresentInOverridenDeclarations() throws Exception {
        KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("idea/idea-frontend-fir/testData/components/overridenDeclarations"), Pattern.compile("^(.+)\\.kt$"), null, true);
    }

    @TestMetadata("inOtherFile.kt")
    public void testInOtherFile() throws Exception {
        runTest("idea/idea-frontend-fir/testData/components/overridenDeclarations/inOtherFile.kt");
    }

    @TestMetadata("intersectionOverride.kt")
    public void testIntersectionOverride() throws Exception {
        runTest("idea/idea-frontend-fir/testData/components/overridenDeclarations/intersectionOverride.kt");
    }

    @TestMetadata("intersectionOverride2.kt")
    public void testIntersectionOverride2() throws Exception {
        runTest("idea/idea-frontend-fir/testData/components/overridenDeclarations/intersectionOverride2.kt");
    }

    @TestMetadata("javaAccessors.kt")
    public void testJavaAccessors() throws Exception {
        runTest("idea/idea-frontend-fir/testData/components/overridenDeclarations/javaAccessors.kt");
    }

    @TestMetadata("multipleInterfaces.kt")
    public void testMultipleInterfaces() throws Exception {
        runTest("idea/idea-frontend-fir/testData/components/overridenDeclarations/multipleInterfaces.kt");
    }

    @TestMetadata("sequenceOfOverrides.kt")
    public void testSequenceOfOverrides() throws Exception {
        runTest("idea/idea-frontend-fir/testData/components/overridenDeclarations/sequenceOfOverrides.kt");
    }
}
