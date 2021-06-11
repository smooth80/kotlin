/*
 * Copyright 2010-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */
import kotlin.test.*

import kotlin.native.concurrent.*
import kotlin.native.internal.MemoryUsageInfo

object Blackhole {
    private val hole = AtomicLong(0)

    fun consume(value: Any) {
        hole.addAndGet(value.hashCode())
    }

    fun discharge() {
        println(hole.value)
    }
}

// Keep a class to ensure we allocate in heap.
// TODO: Protect it from escape analysis.
class MemoryHog(val size: Int, val value: Byte, val stride: Int) {
    val data = ByteArray(size)

    init {
        for (i in 0..(size / stride)) {
            data[i * stride] = value
        }
        Blackhole.consume(data)
    }
}

@Test
fun test() {
    // One item is ~10MiB.
    val size = 10_000_000
    // Total amount is ~1TiB.
    val count = 100_000
    val value: Byte = 42
    // Try to make sure each page is written
    val stride = 4096
    // Limit maximum memory usage at ~200MiB
    val rssLimit: Long = 200_000_000
    // Trigger GC after ~100MiB are allocated
    val retainLimit: Long = 100_000_000
    val progressReportsCount = 100

    if (Platform.memoryModel == MemoryModel.EXPERIMENTAL) {
        kotlin.native.internal.GC.thresholdAllocations = retainLimit
        // Effectively disable trigger on safepoints.
        kotlin.native.internal.GC.threshold = Int.MAX_VALUE
    }

    for (i in 0..count) {
        if (i % (count / progressReportsCount) == 0) {
            println("Allocating iteration ${i + 1} of $count")
        }
        val currentPeakRss = MemoryUsageInfo.peakResidentSetSizeBytes
        if (currentPeakRss == 0) {
            fail("Error trying to obtain peak RSS. Check if current platform is supported")
        }
        if (currentPeakRss > rssLimit) {
            // If GC does not exist, this should eventually fail.
            fail("Current RSS $currentPeakRss is more than the limit $rssLimit")
        }
        MemoryHog(size, value, stride)
    }

    // Make sure `Blackhole` does not get optimized out.
    Blackhole.discharge()
}
