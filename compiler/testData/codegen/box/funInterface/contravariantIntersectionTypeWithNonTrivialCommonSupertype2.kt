// IGNORE_BACKEND: WASM
// WASM_MUTE_REASON: SAM_CONVERSIONS

interface Top
interface Unrelated

interface A : Top, Unrelated
interface B : Top, Unrelated

val flag = 0

fun box(): String {
    val g = when (flag) {
        0 -> G<A>()
        else -> G<B>()
    }

    g.check {}
    g.check(::functionReference)
    return "OK"
}

fun functionReference(x: Any) {}

class G<T : Top> {
    fun check(x: IFoo<in T>) {
        x.accept(object : A {} as T)
        x.accept(object : B {} as T)
    }
}

fun interface IFoo<T : Top> {
    fun accept(t: T)
}
