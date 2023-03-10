package me.senseiwells.arucas.utils.impl

import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.interpreter.Interpreter

class ArucasOrderedMap(): ArucasMap() {
    private val accessOrder: Boolean

    private var head: Entry? = null
    private var tail: Entry? = null

    init {
        this.accessOrder = false
    }

    constructor(interpreter: Interpreter, map: ArucasMap): this() {
        this.putMapEntries(interpreter, map, false)
    }

    constructor(interpreter: Interpreter, map: Map<*, *>): this() {
        for ((key, value) in map) {
            this.put(interpreter, interpreter.convertValue(key), interpreter.convertValue(value))
        }
    }

    override fun newNode(hash: Int, key: ClassInstance, value: ClassInstance, next: TreeNode?): Node {
        val entry = Entry(hash, key, value, next)
        linkNodeLast(entry)
        return entry
    }

    override fun replacementNode(node: Node, next: Node?): Node {
        val q = node as Entry
        val t = Entry(q.hash, q.key, q.value, next)
        transferLinks(q, t)
        return t
    }

    override fun newTreeNode(hash: Int, key: ClassInstance, value: ClassInstance, next: Node?): TreeNode {
        val p = TreeNode(hash, key, value, next)
        linkNodeLast(p)
        return p
    }

    override fun replacementTreeNode(node: Node, next: Node?): TreeNode {
        val q = node as Entry
        val t = TreeNode(q.hash, q.key, q.value, next)
        transferLinks(q, t)
        return t
    }

    override fun afterNodeAccess(e: Node) {
        synchronized(lock) {
            var last = tail
            if (accessOrder && last !== e) {
                val p = e as Entry
                val b = p.before
                val a = p.after
                p.after = null
                if (b == null) {
                    head = a
                } else {
                    b.after = a
                }
                if (a != null) {
                    a.before = b
                } else {
                    last = b
                }
                if (last == null) {
                    head = p
                } else {
                    p.before = last
                    last.after = p
                }
                tail = p
            }
        }
    }

    override fun containsValue(interpreter: Interpreter, value: ClassInstance): Boolean {
        synchronized(lock) {
            var e = head
            while (e != null) {
                val v = e.value
                if (v === value || value.equals(interpreter, v)) {
                    return true
                }
                e = e.after
            }
            return false
        }
    }

    override fun get(interpreter: Interpreter, key: ClassInstance): ClassInstance? {
        synchronized(lock) {
            val e = getNode(interpreter, key) ?: return null
            if (accessOrder) {
                afterNodeAccess(e)
            }
            return e.value
        }
    }

    override fun clear() {
        synchronized(lock) {
            super.clear()
            tail = null
            head = null
        }
    }

    override fun keys(): ArucasList {
        val keyList = ArucasList()
        synchronized(lock) {
            var e = head
            while (e != null) {
                keyList.add(e.key)
                e = e.after
            }
            return keyList
        }
    }

    override fun values(): ArucasList {
        val valueList = ArucasList()
        synchronized(lock) {
            var e = head
            while (e != null) {
                valueList.add(e.value)
                e = e.after
            }
            return valueList
        }
    }

    override fun pairSet(): Set<Pair<ClassInstance, ClassInstance>> {
        val pairSet = LinkedHashSet<Pair<ClassInstance, ClassInstance>>()
        synchronized(lock) {
            var e = head
            while (e != null) {
                pairSet.add(e.key to e.value)
                e = e.after
            }
            return pairSet
        }
    }

    override fun afterNodeInsertion(interpreter: Interpreter, evict: Boolean) {

    }

    override fun afterNodeRemoval(p: Node) {
        synchronized(this.lock) {
            val e = p as Entry
            val b = e.before
            val a = e.after
            e.after = null
            e.before = null
            if (b == null) {
                head = a
            } else {
                b.after = a
            }
            if (a == null) {
                tail = b
            } else {
                a.before = b
            }
        }
    }

    private fun linkNodeLast(entry: Entry) {
        synchronized(lock) {
            val last = tail
            tail = entry
            if (last == null) {
                head = entry
                return
            }
            entry.before = last
            last.after = entry
        }
    }

    private fun transferLinks(source: Entry, destination: Entry) {
        synchronized(lock) {
            destination.before = source.before
            val b = destination.before
            destination.after = source.after
            val a = destination.after
            if (b == null) {
                head = destination
            } else {
                b.after = destination
            }
            if (a == null) {
                tail = destination
            } else {
                a.before = destination
            }
        }
    }

    open class Entry(
        hash: Int,
        key: ClassInstance,
        value: ClassInstance,
        next: Node?
    ): Node(hash, key, value, next) {
        var before: Entry? = null
        var after: Entry? = null
    }
}