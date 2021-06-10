// SKIP_KT_DUMP

abstract class BaseClass
interface BaseInterface

class ConcreteType : BaseClass(), BaseInterface
class ConcreteType2 : BaseClass(), BaseInterface

fun example(input: Int) {
    val instance = when (input) {
        0 -> GenericHolder<ConcreteType>()
        else -> GenericHolder<ConcreteType2>()
    }

    instance.doOnSuccess {}
    instance.doOnSuccess(::functionReference)
}

fun functionReference(x: Any) {}

class GenericHolder<T> {
    fun doOnSuccess(onSuccess: Consumer<in T>) {}
}

fun interface Consumer<T> {
    fun accept(t: T)
}
