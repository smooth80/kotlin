import kotlin.test.*

import kotlin.native.concurrent.*

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
    // One item is ~100MiB
    val size = 100_000_000
    val count = 10_000
    val value: Byte = 42
    // Try to make sure each page is written
    val stride = 4096

    if (Platform.memoryModel == MemoryModel.EXPERIMENTAL) {
        // Allow to retain ~1GiB
        kotlin.native.internal.GC.thresholdAllocations = 10 * size.toLong()
        // Effectively disable trigger on safepoints.
        kotlin.native.internal.GC.threshold = Int.MAX_VALUE
    }

    // This will allocate ~1TiB. If GC does not exist, this should fail.
    for (i in 0..count) {
        if (i % 100 == 0) {
            println("Allocating iteration ${i + 1} of $count")
        }
        MemoryHog(size, value, stride)
    }

    // Make sure `Blackhole` does not get optimized out.
    Blackhole.discharge()
}
