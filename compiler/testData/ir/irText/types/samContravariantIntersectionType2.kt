// SKIP_KT_DUMP

interface X
interface Z

interface A : X, Z
interface B : X, Z

fun interface IFoo<T : X> {
    fun accept(t: T)
}

fun <T> sel(x: T, y: T) = x

class G<T : X> {
    fun check(x: IFoo<in T>) {}
}


fun test() {
    val g = sel(G<A>(), G<B>())
    g.check {}
}
