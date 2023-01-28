package me.senseiwells.arucas.utils.impl

import me.senseiwells.arucas.builtin.CollectionDef
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.LocatableTrace
import java.util.*

open class ArucasMap: ArucasCollection {
    companion object {
        private val DEADLOCKED_HANDLER = Any()

        private const val INITIAL_CAPACITY = 16
        private const val MAX_CAPACITY = 1 shl 30
        private const val TREEIFY_THRESHOLD = 8
        private const val UNTREEIFY_THRESHOLD = 6
        private const val MIN_TREEIFY_CAPACITY = 64
        private const val DEFAULT_LOAD_FACTOR = 0.75f
    }

    protected val lock = Any()

    private val loadFactor: Float
    private var table: Array<Node?>? = null
    private var size = 0
    private var threshold = 0

    init {
        this.loadFactor = DEFAULT_LOAD_FACTOR
    }

    fun isEmpty() = this.size == 0

    override fun length() = this.size

    override fun asCollection() = this.keys()

    override fun toSafeString() = "<map>"

    override fun formatCollection(elements: String) = "{$elements}"

    override fun toStringDeep(interpreter: Interpreter, trace: LocatableTrace, processed: Stack<ArucasCollection>): String {
        if (this in processed) {
            return this.toSafeString()
        }
        processed.push(this)
        val builder = StringBuilder()
        val iterator = this.pairSet().iterator()
        while (iterator.hasNext()) {
            val (key, value) = iterator.next()
            val kCollection = key.getPrimitive(CollectionDef::class)
            val kStr = kCollection?.toStringDeep(interpreter, trace, processed) ?: key.toString(interpreter, trace)
            val vCollection = value.getPrimitive(CollectionDef::class)
            val vStr = vCollection?.toStringDeep(interpreter, trace, processed) ?: value.toString(interpreter, trace)
            builder.append(kStr).append(": ").append(vStr)
            if (iterator.hasNext()) {
                builder.append(", ")
            }
        }
        processed.pop()
        return this.formatCollection(builder.toString())
    }

    open fun get(interpreter: Interpreter, key: ClassInstance): ClassInstance? {
        val node = this.getNode(interpreter, key)
        return node?.value
    }

    fun containsKey(interpreter: Interpreter, key: ClassInstance): Boolean {
        return this.getNode(interpreter, key) != null
    }

    fun getKey(interpreter: Interpreter, key: ClassInstance): ClassInstance? {
        val node = this.getNode(interpreter, key)
        return node?.key
    }

    fun put(interpreter: Interpreter, key: ClassInstance, value: ClassInstance): ClassInstance? {
        return this.putVal(interpreter, this.hash(interpreter, key), key, value, false, true)
    }

    fun putIfAbsent(interpreter: Interpreter, key: ClassInstance, value: ClassInstance): ClassInstance? {
        return this.putVal(interpreter, this.hash(interpreter, key), key, value, true, true)
    }

    fun putAll(interpreter: Interpreter, map: ArucasMap) {
        this.putMapEntries(interpreter, map, true)
    }

    fun remove(interpreter: Interpreter, key: ClassInstance): ClassInstance? {
        return this.removeNode(interpreter, this.hash(interpreter, key), key, null, false, true)?.value
    }

    fun remove(interpreter: Interpreter, key: ClassInstance, value: ClassInstance): Boolean {
        return this.removeNode(interpreter, this.hash(interpreter, key), key, value, true, true) != null
    }

    fun equals(interpreter: Interpreter, otherMap: ArucasMap): Boolean {
        if (this === otherMap) {
            return true
        }

        return this.deadlockSafe(otherMap) {
            val otherTable = it.table
            if (otherTable == null && this.table == null) {
                return@deadlockSafe true
            }
            if (this.size != it.size || otherTable == null) {
                return@deadlockSafe false
            }

            for (node in otherTable) {
                var mutNode = node
                while (mutNode != null) {
                    val thisNode = this.getNode(interpreter, mutNode.key)

                    if (thisNode == null || !thisNode.equals(interpreter, mutNode)) {
                        return@deadlockSafe false
                    }

                    mutNode = mutNode.next
                }
            }
            return@deadlockSafe true
        }
    }

    fun hashCode(interpreter: Interpreter): Int {
        synchronized(this.lock) {
            var h = 0

            val table = this.table ?: return h

            for (node in table) {
                var mutNode = node

                while (mutNode != null) {
                    h += mutNode.hashCode(interpreter)
                    mutNode = mutNode.next
                }
            }

            return h
        }
    }

    open fun clear() {
        synchronized(this.lock) {
            val table = this.table
            if (table != null && this.size > 0) {
                this.size = 0
                Arrays.fill(table, null)
            }
        }
    }

    open fun containsValue(interpreter: Interpreter, value: ClassInstance): Boolean {
        synchronized(this.lock) {
            val table = this.table
            if (table != null && this.size > 0) {
                for (e in table) {
                    var mutE = e
                    while (mutE != null) {
                        val v = mutE.value
                        if (v === value || value.equals(interpreter, v)) {
                            return true
                        }
                        mutE = mutE.next
                    }
                }
            }
            return false
        }
    }

    open fun keys(): ArucasList {
        val keyList = ArucasList()
        synchronized(this.lock) {
            val table = this.table
            if (this.size > 0 && table != null) {
                for (node in table) {
                    var mutNode = node
                    while (mutNode != null) {
                        keyList.add(mutNode.key)
                        mutNode = mutNode.next
                    }
                }
            }
            return keyList
        }
    }

    open fun values(): ArucasList {
        val valueList = ArucasList()
        synchronized(this.lock) {
            val table = this.table
            if (this.size > 0 && table != null) {
                for (node in table) {
                    var mutNode = node
                    while (mutNode != null) {
                        valueList.add(mutNode.value)
                        mutNode = mutNode.next
                    }
                }
            }
            return valueList
        }
    }

    open fun pairSet(): Set<Pair<ClassInstance, ClassInstance>> {
        val pairSet = HashSet<Pair<ClassInstance, ClassInstance>>()
        synchronized(this.lock) {
            val table = this.table
            if (this.size > 0 && table != null) {
                for (node in table) {
                    var mutNode = node
                    while (mutNode != null) {
                        pairSet.add(mutNode.key to mutNode.value)
                        mutNode = mutNode.next
                    }
                }
            }
            return pairSet
        }
    }

    internal fun getNode(interpreter: Interpreter, key: ClassInstance): Node? {
        synchronized(this.lock) {
            this.table?.let {
                val size = it.size
                if (size > 0) {
                    val hash = this.hash(interpreter, key)
                    val first = it[size - 1 and hash]
                    first ?: return null
                    var k = first.key
                    if (first.hash == hash && (k === key || k.equals(interpreter, key))) {
                        return first
                    }
                    var e = first.next
                    e ?: return null
                    if (first is TreeNode) {
                        return first.getTreeNode(interpreter, hash, key)
                    }
                    while (e != null) {
                        if (e.hash == hash) {
                            k = e.key
                            if (k === key || key.equals(interpreter, k)) {
                                return e
                            }
                        }
                        e = e.next
                    }
                }
            }
            return null
        }
    }

    internal fun putMapEntries(interpreter: Interpreter, otherMap: ArucasMap, evict: Boolean) {
        synchronized(this.lock) {
            val size = otherMap.size
            if (size > 0) {
                val table = this.table
                if (table == null) {
                    val factorThreshold = size / this.loadFactor + 1.0f
                    val threshold = factorThreshold.toInt().coerceAtMost(MAX_CAPACITY)
                    if (threshold > this.threshold) {
                        this.threshold = this.tableSizeFor(threshold)
                    }
                } else {
                    while (size > this.threshold && table.size < MAX_CAPACITY) {
                        this.resize()
                    }
                }
            }
        }
        this.deadlockSafe(otherMap) { map ->
            for ((key, value) in map.pairSet()) {
                this.putVal(interpreter, this.hash(interpreter, key), key, value, false, evict)
            }
        }
    }


    internal open fun newNode(hash: Int, key: ClassInstance, value: ClassInstance, next: TreeNode?): Node {
        return Node(hash, key, value, next)
    }

    internal open fun replacementNode(node: Node, next: Node?): Node {
        return Node(node.hash, node.key, node.value, next)
    }

    internal open fun newTreeNode(hash: Int, key: ClassInstance, value: ClassInstance, next: Node?): TreeNode {
        return TreeNode(hash, key, value, next)
    }

    internal open fun replacementTreeNode(node: Node, next: Node?): TreeNode {
        return TreeNode(node.hash, node.key, node.value, next)
    }

    // These are callbacks for OrderedMap...
    internal open fun afterNodeAccess(e: Node) { }

    internal open fun afterNodeInsertion(interpreter: Interpreter, evict: Boolean) { }

    internal open fun afterNodeRemoval(p: Node) { }

    private fun <T> deadlockSafe(otherMap: ArucasMap, consumer: (ArucasMap) -> T): T {
        /*
		 * If we always synchronize on DEADLOCK_HANDLE when locking on parameters
		 * we will never let two threads lock on each other without waiting for
		 * the other thread to release their lock on DEADLOCK_HANDLE.
		 * This prevents any deadlocks from happening.
		 * Anything that runs here will be slow since this method can only be
		 * executed once at a time, including if you are running multiple scripts.
		 */
        synchronized(DEADLOCKED_HANDLER) {
            synchronized(this.lock) {
                synchronized(otherMap.lock) {
                    return consumer(otherMap)
                }
            }
        }
    }

    private fun hash(interpreter: Interpreter, key: ClassInstance): Int {
        val hash = key.hashCode(interpreter)
        return hash xor (hash ushr 16)
    }

    private fun putVal(interpreter: Interpreter, hash: Int, key: ClassInstance, value: ClassInstance, ifAbsent: Boolean, evict: Boolean): ClassInstance? {
        synchronized(this.lock) {
            var table = this.table
            if (table == null || table.isEmpty()) {
                table = this.resize()
            }
            val size = table.size
            val i = (size - 1) and hash
            val p = table[i]
            if (p == null) {
                table[i] = this.newNode(hash, key, value, null)
            } else {
                var e: Node?
                var k = p.key
                if (p.hash == hash && (k === key || key.equals(interpreter, k))) {
                    e = p
                } else if (p is TreeNode) {
                    e = p.putTreeVal(interpreter, this, table, hash, key, value)
                } else {
                    var binCount = 0
                    var np: Node = p
                    while (true) {
                        e = np.next
                        if (e == null) {
                            np.next = this.newNode(hash, key, value, null)
                            if (binCount >= TREEIFY_THRESHOLD - 1) {
                                this.treeifyBin(table, hash)
                            }
                            break
                        }
                        if (e.hash == hash) {
                            k = e.key
                            if (k === key || key.equals(interpreter, k)) {
                                break
                            }
                        }
                        np = e
                        ++binCount
                    }
                }
                if (e != null) {
                    val oldValue = e.value
                    if (!ifAbsent) {
                        e.value = value
                    }
                    this.afterNodeAccess(e)
                    return oldValue
                }
            }
            if (++this.size > this.threshold) {
                this.resize()
            }
            this.afterNodeInsertion(interpreter, evict)
            return null
        }
    }

    @Suppress("SameParameterValue")
    private fun removeNode(interpreter: Interpreter, hash: Int, key: ClassInstance, value: ClassInstance?, match: Boolean, movable: Boolean): Node? {
        synchronized(this.lock) {
            val table = this.table
            if (table != null) {
                val n = table.size
                if (n > 0) {
                    val index = (n - 1) and hash
                    var p = table[index]
                    if (p != null) {
                        var node: Node? = null
                        var e: Node?
                        var k = p.key
                        val v: ClassInstance
                        if (p.hash == hash && (k === key || key.equals(interpreter, k))) {
                            node = p
                        } else {
                            e = p.next
                            if (e != null) {
                                if (p is TreeNode) {
                                    node = p.getTreeNode(interpreter, hash, key)
                                } else {
                                    while (e != null) {
                                        k = e.key
                                        if (e.hash == hash && (k === key || key.equals(interpreter, k))) {
                                            node = e
                                            break
                                        }
                                        p = e
                                        e = e.next
                                    }
                                    p!!
                                }
                            }
                        }


                        if (node != null) {
                            v = node.value
                            if (!match || v === value || (value !== null && value.equals(interpreter, v))) {
                                if (node is TreeNode) {
                                    node.removeTreeNode(this, table, movable)
                                } else if (node === p) {
                                    table[index] = node.next
                                } else {
                                    p.next = node.next
                                }
                                this.size--
                                this.afterNodeRemoval(node)
                                return node
                            }
                        }
                    }
                }
            }

            return null
        }
    }

    private fun resize(): Array<Node?> {
        synchronized(this.lock) {
            val oldTable = this.table
            val oldCap = oldTable?.size ?: 0
            val oldThr = this.threshold
            val newCap: Int
            var newThr = 0
            if (oldCap > 0) {
                if (oldCap >= MAX_CAPACITY) {
                    this.threshold = Int.MAX_VALUE
                    return oldTable!!
                }
                newCap = oldCap shl 1
                if (newCap < MAX_CAPACITY && oldCap >= INITIAL_CAPACITY) {
                    newThr = oldThr shl 1
                }
            } else if (oldThr > 0) {
                newCap = oldThr
            } else {
                newCap = INITIAL_CAPACITY
                newThr = (DEFAULT_LOAD_FACTOR * INITIAL_CAPACITY).toInt()
            }
            if (newThr == 0) {
                val ft = newCap.toFloat() * this.loadFactor
                newThr = if (newCap < MAX_CAPACITY && ft < MAX_CAPACITY.toFloat()) ft.toInt() else Int.MAX_VALUE
            }
            this.threshold = newThr
            val newTab = arrayOfNulls<Node>(newCap)
            this.table = newTab
            if (oldTable != null) {
                var j = 0
                while (j < oldCap) {
                    var e = oldTable[j]
                    if (e != null) {
                        oldTable[j] = null
                        if (e.next == null) {
                            newTab[e.hash and (newCap - 1)] = e
                        } else if (e is TreeNode) {
                            e.split(this, newTab, j, oldCap)
                        } else {
                            var loHead: Node? = null
                            var loTail: Node? = null
                            var hiHead: Node? = null
                            var hiTail: Node? = null
                            var next: Node?
                            while (e != null) {
                                next = e.next
                                if (e.hash and oldCap == 0) {
                                    if (loTail == null) {
                                        loHead = e
                                    } else {
                                        loTail.next = e
                                    }
                                    loTail = e
                                } else {
                                    if (hiTail == null) {
                                        hiHead = e
                                    } else {
                                        hiTail.next = e
                                    }
                                    hiTail = e
                                }
                                e = next
                            }
                            if (loTail != null) {
                                loTail.next = null
                                newTab[j] = loHead
                            }
                            if (hiTail != null) {
                                hiTail.next = null
                                newTab[j + oldCap] = hiHead
                            }
                        }
                    }


                    j++
                }
            }
            return newTab
        }
    }

    private fun treeifyBin(tab: Array<Node?>?, hash: Int) {
        synchronized(this.lock) {
            val n: Int
            val index: Int
            var e: Node?

            if (tab == null) {
                this.resize()
            } else {
                n = tab.size
                if (n < MIN_TREEIFY_CAPACITY) {
                    this.resize()
                } else {
                    index = (n - 1) and hash
                    e = tab[index]
                    if (e != null) {
                        var hd: TreeNode? = null
                        var tl: TreeNode? = null
                        while (e != null) {
                            val p = this.replacementTreeNode(e, null)
                            if (tl == null) {
                                hd = p
                            } else {
                                p.previous = tl
                                tl.next = p
                            }
                            tl = p
                            e = e.next
                        }
                        tab[index] = hd
                        hd?.treeify(tab)
                    }
                }
            }
            Unit
        }
    }

    private fun tableSizeFor(cap: Int): Int {
        val n = -1 ushr Integer.numberOfLeadingZeros(cap - 1)
        return if (n < 0) 1 else if (n >= MAX_CAPACITY) MAX_CAPACITY else n + 1
    }

    @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("map.hashCode(interpreter)"))
    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }

    @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("map.equals(interpreter, other)"))
    override fun equals(other: Any?): Boolean {
        return this === other
    }

    @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("map.toString(interpreter)"))
    override fun toString(): String {
        return this.toSafeString()
    }

    open class Node(
        val hash: Int,
        val key: ClassInstance,
        var value: ClassInstance,
        var next: Node?
    ) {
        fun equals(interpreter: Interpreter, other: Node): Boolean {
            if (this === other) {
                return true
            }
            return this.key.equals(interpreter, other.key) && this.value.equals(interpreter, other.value)
        }

        fun hashCode(interpreter: Interpreter): Int {
            return this.key.hashCode(interpreter) xor this.value.hashCode(interpreter)
        }
    }

    class TreeNode(
        hash: Int,
        key: ClassInstance,
        value: ClassInstance,
        next: Node?
    ) : ArucasOrderedMap.Entry(hash, key, value, next) {
        var parent: TreeNode? = null
        var left: TreeNode? = null
        var right: TreeNode? = null
        var previous: TreeNode? = null
        var red = false

        fun getTreeNode(interpreter: Interpreter, hash: Int, key: ClassInstance): TreeNode? {
            return (if (this.parent != null) this.root() else this).find(interpreter, hash, key)
        }

        fun treeify(table: Array<Node?>?) {
            var root: TreeNode? = null
            var x: TreeNode? = this
            var next: TreeNode?
            while (x != null) {
                next = x.next as? TreeNode
                x.right = null
                x.left = x.right
                if (root == null) {
                    x.parent = null
                    x.red = false
                    root = x
                    x = next
                    continue
                }
                val h = x.hash
                var p = root
                while (true) {
                    var dir: Int
                    val ph = p!!.hash
                    val pk: ClassInstance = p.key
                    dir = if (ph > h) -1 else if (ph < h) 1 else tieBreakOrder(x.key, pk)
                    val xp = p
                    p = if (dir <= 0) p.left else p.right
                    if (p == null) {
                        x.parent = xp
                        if (dir <= 0) {
                            xp.left = x
                        } else {
                            xp.right = x
                        }
                        root = balanceInsertion(root!!, x)
                        break
                    }
                }
                x = next
            }
            moveRootToFront(table, root)
        }

        fun putTreeVal(interpreter: Interpreter, map: ArucasMap, table: Array<Node?>?, h: Int, k: ClassInstance, v: ClassInstance): TreeNode? {
            var searched = false
            val root = if (this.parent != null) this.root() else this
            var p: TreeNode? = root
            while (true) {
                var dir: Int
                val ph = p!!.hash
                val pk = p.key
                if (ph > h) {
                    dir = -1
                } else if (ph < h) {
                    dir = 1
                } else if (pk === k || k.equals(interpreter, pk)) {
                    return p
                } else {
                    if (!searched) {
                        var q: TreeNode? = null
                        val ch = if (p.left == null) p.right else p.left
                        searched = true
                        if (ch != null && ch.find(interpreter, h, k).also { q = it } != null) {
                            return q
                        }
                    }
                    dir = tieBreakOrder(k, pk)
                }
                val xp = p
                p = if (dir <= 0) p.left else p.right
                if (p == null) {
                    val xpn = xp.next
                    val x: TreeNode = map.newTreeNode(h, k, v, xpn)
                    if (dir <= 0) {
                        xp.left = x
                    } else {
                        xp.right = x
                    }
                    xp.next = x
                    x.previous = xp
                    x.parent = xp
                    if (xpn != null) {
                        (xpn as TreeNode).previous = x
                    }
                    moveRootToFront(table, balanceInsertion(root, x))
                    return null
                }
            }
        }

        fun removeTreeNode(map: ArucasMap, table: Array<Node?>?, movable: Boolean) {
            if (table == null) {
                return
            }
            val size = table.size
            if (size == 0) {
                return
            }
            val index = size - 1 and this.hash
            var first = table[index] as TreeNode?
            var root = first
            val successor = this.next as TreeNode?
            val previous = this.previous
            if (previous == null) {
                first = successor
                table[index] = first
            } else {
                previous.next = successor
            }
            if (successor != null) {
                successor.previous = previous
            }
            if (first == null) {
                return
            }
            if (root!!.parent != null) {
                root = root.root()
            }
            if (movable) {
                val rootLeft = root.left
                if ((root.right == null || rootLeft == null || rootLeft.left == null)) {
                    table[index] = first.untreeify(map)
                    return
                }
            }
            val current = this
            val currentLeft = this.left
            val currentRight = this.right
            val replacement: TreeNode
            if (currentLeft != null && currentRight != null) {
                var next: TreeNode = currentRight
                var nextLeft = next.left
                while (nextLeft != null) {
                    next = nextLeft
                    nextLeft = next.left
                }
                val nextRed = next.red
                next.red = current.red
                current.red = nextRed
                val nextRight = next.right
                val currentParent = current.parent
                if (next === currentRight) {
                    current.parent = next
                    next.right = current
                } else {
                    val nextParent = next.parent
                    current.parent = nextParent
                    if (nextParent != null) {
                        if (next === nextParent.left) {
                            nextParent.left = current
                        } else {
                            nextParent.right = current
                        }
                    }
                    next.right = currentRight
                    currentRight.parent = next
                }
                current.left = null
                current.right = nextRight
                if (nextRight != null) {
                    nextRight.parent = current
                }
                next.left = currentLeft
                currentLeft.parent = next
                next.parent = currentParent
                if (currentParent == null) {
                    root = next
                } else if (current === currentParent.left) {
                    currentParent.left = next
                } else {
                    currentParent.right = next
                }
                replacement = nextRight ?: current
            } else {
                replacement = currentLeft ?: Objects.requireNonNullElse(currentRight, current)!!
            }
            if (replacement !== current) {
                replacement.parent = current.parent
                val pp = replacement.parent
                if (pp == null) {
                    root = replacement
                    root.red = false
                } else if (current === pp.left) {
                    pp.left = replacement
                } else {
                    pp.right = replacement
                }
                current.parent = null
                current.right = null
                current.left = null
            }
            val rootNode = if (current.red) root else balanceDeletion(root, replacement)
            if (replacement === current) {
                val pp = current.parent
                current.parent = null
                if (pp != null) {
                    if (current === pp.left) {
                        pp.left = null
                    } else if (current === pp.right) {
                        pp.right = null
                    }
                }
            }
            if (movable) {
                moveRootToFront(table, rootNode)
            }
        }

        fun split(map: ArucasMap, table: Array<Node?>, index: Int, bit: Int) {
            val treeNode = this
            var loHead: TreeNode? = null
            var loTail: TreeNode? = null
            var hiHead: TreeNode? = null
            var hiTail: TreeNode? = null
            var lc = 0
            var hc = 0

            var e: TreeNode? = treeNode
            var next: TreeNode?
            while (e != null) {
                next = e.next as TreeNode?
                e.next = null
                if (e.hash and bit == 0) {
                    e.previous = loTail
                    if (loTail == null) {
                        loHead = e
                    } else {
                        loTail.next = e
                    }
                    loTail = e
                    lc++
                } else {
                    e.previous = hiTail
                    if (hiTail == null) {
                        hiHead = e
                    } else {
                        hiTail.next = e
                    }
                    hiTail = e
                    hc++
                }
                e = next
            }

            if (loHead != null) {
                if (lc <= UNTREEIFY_THRESHOLD) {
                    table[index] = loHead.untreeify(map)
                } else {
                    table[index] = loHead
                    if (hiHead != null) {
                        loHead.treeify(table)
                    }
                }
            }
            if (hiHead != null) {
                if (hc <= UNTREEIFY_THRESHOLD) {
                    table[index + bit] = hiHead.untreeify(map)
                } else {
                    table[index + bit] = hiHead
                    if (loHead != null) {
                        hiHead.treeify(table)
                    }
                }
            }
        }

        private fun untreeify(map: ArucasMap): Node? {
            var returnNode: Node? = null
            var lastReplacement: Node? = null
            var current: Node? = this
            while (current != null) {
                val replacement: Node = map.replacementNode(current, null)
                if (lastReplacement == null) {
                    returnNode = replacement
                } else {
                    lastReplacement.next = replacement
                }
                lastReplacement = replacement
                current = current.next
            }
            return returnNode
        }

        private fun root(): TreeNode {
            var current = this
            var parent: TreeNode?
            while (true) {
                parent = current.parent
                if (parent == null) {
                    return current
                }
                current = parent
            }
        }

        private fun find(interpreter: Interpreter, hash: Int, key: ClassInstance): TreeNode? {
            var current: TreeNode? = this
            do {
                var currentHash: Int
                var currentKey: ClassInstance
                val pl = current!!.left
                val pr = current.right
                var q: TreeNode?
                if (current.hash.also { currentHash = it } > hash) {
                    current = pl
                } else if (currentHash < hash) {
                    current = pr
                } else if (current.key.also { currentKey = it } === key || key.equals(interpreter, currentKey)) {
                    return current
                } else if (pl == null) {
                    current = pr
                } else if (pr == null) {
                    current = pl
                } else if (pr.find(interpreter, hash, key).also { q = it } != null) {
                    return q
                } else {
                    current = pl
                }
            } while (current != null)
            return null
        }

        private companion object {
            fun moveRootToFront(table: Array<Node?>?, root: TreeNode?) {
                if (root != null && table != null) {
                    val size = table.size
                    if (size > 0) {
                        val index = size - 1 and root.hash
                        val first = table[index] as TreeNode?
                        if (root !== first) {
                            table[index] = root
                            val next = root.next as TreeNode?
                            val previous = root.previous
                            if (next != null) {
                                next.previous = previous
                            }
                            if (previous != null) {
                                previous.next = next
                            }
                            if (first != null) {
                                first.previous = root
                            }
                            root.next = first
                            root.previous = null
                        }
                    }
                }
            }

            fun tieBreakOrder(a: ClassInstance?, b: ClassInstance?): Int {
                var d = 0
                if (a === null || b === null || a::class.java.name.compareTo(b::class.java.simpleName).also { d = it } == 0) {
                    d = if (System.identityHashCode(a) <= System.identityHashCode(b)) -1 else 1
                }
                return d
            }

            fun rotateLeft(root: TreeNode, p: TreeNode?): TreeNode {
                var mutRoot = root
                val r: TreeNode?
                val pp: TreeNode?
                val rl: TreeNode?
                if (p != null) {
                    r = p.right
                    if (r != null) {
                        rl = r.left
                        p.right = rl
                        if (rl != null) {
                            rl.parent = p
                        }
                        pp = p.parent
                        r.parent = pp
                        if (pp == null) {
                            r.also { mutRoot = it }.red = false
                        } else if (pp.left === p) {
                            pp.left = r
                        } else {
                            pp.right = r
                        }
                        r.left = p
                        p.parent = r
                    }
                }
                return mutRoot
            }

            fun rotateRight(root: TreeNode, p: TreeNode?): TreeNode {
                var mutRoot = root
                val l: TreeNode?
                val pp: TreeNode?
                val lr: TreeNode?
                if (p != null ) {
                    l = p.left
                    if (l != null) {
                        lr = l.right
                        p.left = lr
                        if (lr != null) {
                            lr.parent = p
                        }
                        pp = p.parent
                        l.parent = pp
                        if (pp == null) {
                            l.also { mutRoot = it }.red = false
                        } else if (pp.right === p) {
                            pp.right = l
                        } else {
                            pp.left = l
                        }
                        l.right = p
                        p.parent = l
                    }
                }
                return mutRoot
            }

            fun balanceInsertion(root: TreeNode, x: TreeNode): TreeNode {
                var mutRoot = root
                var mutX = x
                mutX.red = true
                var xp: TreeNode?
                var xpp: TreeNode?
                var xppl: TreeNode?
                var xppr: TreeNode?
                while (true) {
                    xp = mutX.parent
                    if (xp == null) {
                        mutX.red = false
                        return mutX
                    }
                    xpp = xp.parent
                    if (!xp.red || xpp == null) {
                        return mutRoot
                    }
                    xppl = xpp.left
                    if (xp === xppl) {
                        xppr = xpp.right
                        if (xppr != null && xppr.red) {
                            xppr.red = false
                            xp.red = false
                            xpp.red = true
                            mutX = xpp
                        } else {
                            if (mutX === xp.right) {
                                mutX = xp
                                mutRoot = this.rotateLeft(mutRoot, mutX)
                                xp = mutX.parent
                                xpp = xp?.parent
                            }
                            if (xp != null) {
                                xp.red = false
                                if (xpp != null) {
                                    xpp.red = true
                                    mutRoot = this.rotateRight(mutRoot, xpp)
                                }
                            }
                        }
                    } else {
                        if (xppl != null && xppl.red) {
                            xppl.red = false
                            xp.red = false
                            xpp.red = true
                            mutX = xpp
                        } else {
                            if (mutX === xp.left) {
                                mutRoot = this.rotateRight(mutRoot, xp.also { mutX = it })
                                xp = mutX.parent
                                xpp = xp?.parent
                            }
                            if (xp != null) {
                                xp.red = false
                                if (xpp != null) {
                                    xpp.red = true
                                    mutRoot = this.rotateLeft(mutRoot, xpp)
                                }
                            }
                        }
                    }
                }
            }

            fun balanceDeletion(root: TreeNode, x: TreeNode?): TreeNode {
                var mutRoot = root
                var mutX = x
                var xp: TreeNode?
                var xpl: TreeNode?
                var xpr: TreeNode?
                while (true) {
                    if (mutX == null || mutX === mutRoot) {
                        return mutRoot
                    }
                    xp = mutX.parent
                    if (xp == null) {
                        mutX.red = false
                        return mutX
                    }
                    if (mutX.red) {
                        mutX.red = false
                        return mutRoot
                    }
                    xpl = xp.left
                    if (xpl === mutX) {
                        xpr = xp.right
                        if (xpr != null && xpr.red) {
                            xpr.red = false
                            xp.red = true
                            mutRoot = this.rotateLeft(mutRoot, xp)
                            xp = mutX.parent
                            xpr = xp?.right
                        }
                        if (xpr == null) {
                            mutX = xp
                        } else {
                            val sl = xpr.left
                            var sr = xpr.right
                            if ((sr == null || !sr.red) && (sl == null || !sl.red)) {
                                xpr.red = true
                                mutX = xp
                            } else {
                                if (sr == null || !sr.red) {
                                    sl!!.red = false
                                    xpr.red = true
                                    mutRoot = this.rotateRight(mutRoot, xpr)
                                    xp = mutX.parent
                                    xpr = xp?.right
                                }
                                if (xpr != null) {
                                    xpr.red = xp!!.red
                                    if (xpr.right.also { sr = it } != null) {
                                        sr!!.red = false
                                    }
                                }
                                if (xp != null) {
                                    xp.red = false
                                    mutRoot = this.rotateLeft(mutRoot, xp)
                                }
                                mutX = mutRoot
                            }
                        }
                    } else {
                        if (xpl != null && xpl.red) {
                            xpl.red = false
                            xp.red = true
                            mutRoot = this.rotateRight(mutRoot, xp)
                            xp = mutX.parent
                            xpl = xp?.left
                        }
                        if (xpl == null) {
                            mutX = xp
                        } else {
                            var sl = xpl.left
                            val sr = xpl.right
                            if ((sl == null || !sl.red) && (sr == null || !sr.red)) {
                                xpl.red = true
                                mutX = xp
                            } else {
                                if (sl == null || !sl.red) {
                                    sr!!.red = false
                                    xpl.red = true
                                    mutRoot = this.rotateLeft(mutRoot, xpl)
                                    xp = mutX.parent
                                    xpl = xp?.left
                                }
                                if (xpl != null) {
                                    xpl.red = xp!!.red
                                    if (xpl.left.also { sl = it } != null) {
                                        sl!!.red = false
                                    }
                                }
                                if (xp != null) {
                                    xp.red = false
                                    mutRoot = this.rotateRight(mutRoot, xp)
                                }
                                mutX = mutRoot
                            }
                        }
                    }
                }
            }
        }
    }
}