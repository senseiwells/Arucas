package me.senseiwells.arucas.utils.impl

import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.runtimeError
import kotlin.math.max

class ArucasList private constructor(
    @Volatile var data: Array<ClassInstance?>,
): MutableList<ClassInstance>, ArucasCollection {
    companion object {
        private const val MAX_ARRAY_LENGTH = Int.MAX_VALUE - 8
        private const val DEFAULT_CAPACITY = 10

        private val DEFAULT_DATA = emptyArray<ClassInstance?>()

        @JvmStatic
        fun of(vararg elements: ClassInstance): ArucasList {
            return ArucasList(arrayOf(*elements)).also {
                it.size = elements.size
            }
        }
    }

    @Volatile
    override var size: Int = 0

    constructor(): this(DEFAULT_DATA)

    constructor(list: ArucasList): this(list.toArrayNullable()) {
        this.size = this.data.size
    }

    constructor(collection: Collection<ClassInstance>): this(collection.toTypedArray()) {
        this.size = this.data.size
    }

    fun toArray(): Array<ClassInstance> = Array(this.size) { this.data[it]!! }

    private fun toArrayNullable(): Array<ClassInstance?> = Array(this.size) { this.data[it] }

    override fun length() = this.size

    override fun asCollection() = this

    @Synchronized
    fun contains(interpreter: Interpreter, element: ClassInstance): Boolean {
        return this.indexOf(interpreter, element) >= 0
    }

    @Synchronized
    fun containsAll(interpreter: Interpreter, elements: ArucasCollection): Boolean {
        if (this.size < elements.length()) {
            return false
        }

        var mutElements: Iterable<ClassInstance> = elements
        if (mutElements is ArucasList) {
            // Avoiding deadlocks
            mutElements = mutElements.toArray().asList()
        }

        for (element in mutElements) {
            if (!this.contains(interpreter, element)) {
                return false
            }
        }
        return true
    }

    @Synchronized
    override fun get(index: Int): ClassInstance {
        this.checkExistingIndex(index)
        return this.data[index]!!
    }

    @Synchronized
    fun indexOf(interpreter: Interpreter, element: ClassInstance): Int {
        for (i in 0 until this.size) {
            if (element.equals(interpreter, this[i])) {
                return i
            }
        }
        return -1
    }

    @Synchronized
    override fun isEmpty(): Boolean {
        return this.size == 0
    }

    @Synchronized
    override fun iterator(): MutableIterator<ClassInstance> {
        return this.listIterator()
    }

    @Synchronized
    fun lastIndexOf(interpreter: Interpreter, element: ClassInstance): Int {
        for (i in (this.size - 1) downTo 0) {
            if (element.equals(interpreter, this[i])) {
                return i
            }
        }
        return -1
    }

    @Synchronized
    override fun add(element: ClassInstance): Boolean {
        this.add(this.size, element, this.data)
        return true
    }

    @Synchronized
    override fun add(index: Int, element: ClassInstance) {
        this.checkAddIndex(index)
        val size = this.size
        var data = this.data
        if (size == data.size) {
            data = this.grow()
        }
        System.arraycopy(data, index, data, index + 1, size - index)
        data[index] = element
        this.size = size + 1
    }

    @Synchronized
    override fun addAll(index: Int, elements: Collection<ClassInstance>): Boolean {
        this.checkAddIndex(index)

        val data = ArucasCollection.safeCollection(elements)
        val expansion = data.size
        if (expansion == 0) {
            return false
        }

        var thisData = this.data
        val size = this.size
        if (expansion > thisData.size - size) {
            thisData = this.grow(size + expansion)
        }

        val moved = size - index
        if (moved > 0) {
            System.arraycopy(thisData, index, thisData, index + expansion, moved)
        }
        System.arraycopy(data, 0, thisData, index, expansion)
        this.size = size + expansion
        return true
    }

    @Synchronized
    override fun addAll(elements: Collection<ClassInstance>): Boolean {
        return this.addAll(ArucasCollection.safeCollection(elements))
    }

    @Synchronized
    override fun clear() {
        val data = this.data
        val size = this.size
        for (i in 0 until size) {
            data[i] = null
        }
        this.size = 0
    }

    @Synchronized
    override fun listIterator(): MutableListIterator<ClassInstance> {
        return this.ArucasIterator()
    }

    @Synchronized
    override fun listIterator(index: Int): MutableListIterator<ClassInstance> {
        return this.ArucasIterator(index)
    }

    @Synchronized
    fun remove(interpreter: Interpreter, element: ClassInstance): Boolean {
        val data = this.data
        for (i in 0 until this.size) {
            if (element.equals(interpreter, data[i]!!)) {
                this.remove(data, i)
                return true
            }
        }
        return false
    }

    @Synchronized
    override fun removeAll(elements: Collection<ClassInstance>): Boolean {
        return this.batchRemove(elements, false)
    }

    @Synchronized
    override fun removeAt(index: Int): ClassInstance {
        this.checkAddIndex(index)
        val valueData: Array<ClassInstance?> = this.data
        val oldValue: ClassInstance = valueData[index]!!
        this.remove(valueData, index)
        return oldValue
    }

    @Synchronized
    override fun retainAll(elements: Collection<ClassInstance>): Boolean {
        return this.batchRemove(elements, true)
    }

    @Synchronized
    override fun set(index: Int, element: ClassInstance): ClassInstance {
        this.checkExistingIndex(index)
        val old = this.data[index]!!
        this.data[index] = element
        return old
    }

    @Synchronized
    override fun subList(fromIndex: Int, toIndex: Int): ArucasList {
        return ArucasList(this.data.copyOfRange(fromIndex, toIndex))
    }

    @Synchronized
    private fun add(size: Int, element: ClassInstance, data: Array<ClassInstance?>) {
        var mutData = data
        if (size == data.size) {
            mutData = this.grow()
        }
        mutData[size] = element
        this.size = size + 1
    }

    @Synchronized
    private fun addAll(elements: Array<ClassInstance>): Boolean {
        val expansion = elements.size
        if (expansion == 0) {
            return false
        }

        var data: Array<ClassInstance?> = this.data
        val size = this.size
        if (expansion > data.size - size) {
            data = this.grow(size + expansion)
        }

        System.arraycopy(elements, 0, data, size, expansion)
        this.size = size + expansion
        return true
    }

    @Synchronized
    private fun remove(elements: Array<ClassInstance?>, index: Int) {
        val newSize = this.size - 1
        if (newSize > index) {
            System.arraycopy(elements, index + 1, elements, index, newSize - index)
        }
        elements[newSize.also { this.size = it }] = null
    }

    @Synchronized
    private fun batchRemove(collection: Collection<ClassInstance>, compliment: Boolean): Boolean {
        val elements = ArucasCollection.safeCollection(collection)
        val data = this.data
        val to = this.size
        var i = 0
        while (true) {
            if (i == to) {
                return false
            }
            if (data[i] in elements != compliment) {
                break
            }
            i++
        }
        var j = i++
        var instance: ClassInstance?
        while (i < to) {
            if (data[i].also { instance = it } in elements == compliment) {
                data[j++] = instance
            }
            i++
        }
        this.shiftTailOverGap(data, j, to)
        return true
    }

    @Synchronized
    private fun shiftTailOverGap(valueData: Array<ClassInstance?>, low: Int, high: Int) {
        System.arraycopy(valueData, high, valueData, low, this.size - high)
        var i = high - low.let { this.size -= it; this.size }
        while (i < this.size) {
            valueData[i++] = null
        }
    }

    @Synchronized
    private fun checkAddIndex(index: Int) {
        if (index < 0 || index > this.size) {
            runtimeError("Index out of bounds")
        }
    }

    @Synchronized
    private fun checkExistingIndex(index: Int) {
        if (index < 0 || index >= this.size) {
            runtimeError("Index out of bounds")
        }
    }

    @Synchronized
    private fun grow(): Array<ClassInstance?> {
        return this.grow(this.size + 1)
    }

    @Synchronized
    private fun grow(minCapacity: Int): Array<ClassInstance?> {
        val oldCapacity: Int = this.data.size
        if (oldCapacity > 0 || this.data !== DEFAULT_DATA) {
            val newCapacity: Int = this.newLength(oldCapacity, minCapacity - oldCapacity, oldCapacity shr 1)
            return this.data.copyOf(newCapacity).also { this.data = it }
        }
        return arrayOfNulls<ClassInstance>(max(DEFAULT_CAPACITY, minCapacity)).also { this.data = it }
    }

    private fun newLength(oldLength: Int, minGrowth: Int, prefGrowth: Int): Int {
        val newLength = minGrowth.coerceAtLeast(prefGrowth) + oldLength
        return if (newLength - MAX_ARRAY_LENGTH <= 0) newLength else this.hugeLength(oldLength, minGrowth)
    }

    private fun hugeLength(oldLength: Int, minGrowth: Int): Int {
        val minLength = oldLength + minGrowth
        if (minLength < 0) {
            throw OutOfMemoryError("Required array length too large")
        }
        return if (minLength <= MAX_ARRAY_LENGTH) MAX_ARRAY_LENGTH else Int.MAX_VALUE
    }

    @Synchronized
    fun hashCode(interpreter: Interpreter): Int {
        val data: Array<ClassInstance?> = this.data
        var hashCode = 1
        for (i in 0 until this.size) {
            val element = data[i]
            hashCode = 31 * hashCode + (element?.hashCode(interpreter) ?: 0)
        }
        return hashCode
    }

    @Synchronized
    fun equals(interpreter: Interpreter, other: ArucasList): Boolean {
        if (this === other) {
            return true
        }
        // We need to make a copy or deadlocks may occur
        val copy = other.toArray()
        if (this.size != copy.size) {
            return false
        }
        for (i in 0 until this.size) {
            if (!this[i].equals(interpreter, copy[i])) {
                return false
            }
        }
        return true
    }

    override fun toSafeString() = "<list>"

    override fun formatCollection(elements: String): String {
        return "[$elements]"
    }

    @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("list.hashCode(interpreter)"))
    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }

    @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("list.equals(interpreter, other)"))
    override fun equals(other: Any?): Boolean {
        return this === other
    }

    @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("list.toString(interpreter)"))
    override fun toString(): String {
        return this.toSafeString()
    }

    @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("list.contains(interpreter, element)"))
    override fun contains(element: ClassInstance): Boolean {
        this.needInterpreterForThis()
    }

    @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("list.containsAll(interpreter, collection)"))
    override fun containsAll(elements: Collection<ClassInstance>): Boolean {
        this.needInterpreterForThis()
    }

    @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("list.indexOf(interpreter, element)"))
    override fun indexOf(element: ClassInstance): Int {
        this.needInterpreterForThis()
    }

    @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("list.lastIndexOf(interpreter, element)"))
    override fun lastIndexOf(element: ClassInstance): Int {
        this.needInterpreterForThis()
    }

    @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("list.remove(interpreter, element)"))
    override fun remove(element: ClassInstance): Boolean {
        this.needInterpreterForThis()
    }

    private inner class ArucasIterator(var cursor: Int = 0): MutableListIterator<ClassInstance> {
        var last = -1

        override fun hasPrevious(): Boolean {
            synchronized(this@ArucasList) {
                return this.cursor != 0
            }
        }

        override fun nextIndex(): Int {
            synchronized(this@ArucasList) {
                return this.cursor
            }
        }

        override fun previous(): ClassInstance {
            synchronized(this@ArucasList) {
                val i = this.cursor - 1
                if (i < 0) {
                    runtimeError("No such element")
                }
                val data = this@ArucasList.data
                this.cursor = i
                this.last = i
                return data[i]!!
            }
        }

        override fun previousIndex(): Int {
            synchronized(this@ArucasList) {
                return this.cursor - 1
            }
        }

        override fun add(element: ClassInstance) {
            synchronized(this@ArucasList) {
                val i = this.cursor
                this@ArucasList.add(i, element)
                this.cursor = i + 1
                this.last = -1
            }
        }

        override fun hasNext(): Boolean {
            synchronized(this@ArucasList) {
                return this.cursor != this@ArucasList.size
            }
        }

        override fun next(): ClassInstance {
            synchronized(this@ArucasList) {
                val i = this.cursor
                if (i >= this@ArucasList.size) {
                    runtimeError("No such element")
                }
                val data = this@ArucasList.data
                this.last = i
                this.cursor = i + 1
                return data[i]!!
            }
        }

        override fun remove() {
            synchronized(this@ArucasList) {
                if (this.last < 0) {
                    runtimeError("Cannot remove unknown index")
                }
                this@ArucasList.removeAt(this.last)
                if (this.last < this.cursor) {
                    this.cursor--
                }
                this.last = -1
            }
        }

        override fun set(element: ClassInstance) {
            synchronized(this@ArucasList) {
                if (this.last < 0) {
                    runtimeError("Cannot set unknown index")
                }
                this@ArucasList.set(this.last, element)
            }
        }
    }
}