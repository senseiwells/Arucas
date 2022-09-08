package me.senseiwells.arucas.test

import me.senseiwells.arucas.util.TestHelper.assertEquals
import me.senseiwells.arucas.util.TestHelper.throwsRuntime
import org.junit.jupiter.api.Test

class InheritanceTests {
    @Test
    fun testCustomCollections() {
        assertEquals(0,
            """
            class E: Collection {
                E(): super();
                fun size() return 0;
            }
            return new E().size();
            """
        )
        assertEquals(true,
            """
            class E: Collection {
                E(): super();
                fun size() return 10;
            }
            e = new E();
            return e.size() == 10 ~ e.isEmpty();
            """
        )
        throwsRuntime(
            """
            class E: Collection {
                E(): super();
            }
            return e.size();
            """
        )
        throwsRuntime(
            """
            class E: Collection {
                E(): super();
            }
            new E().iterator().hasNext();
            """
        )
    }

    @Test
    fun testCustomIterators() {
        assertEquals(45,
            """
            class I: Iterator {
                var current;
                var end;
                
                I(c, e): super() {
                    this.current = c;
                    this.end = e;
                }
                
                fun hasNext() {
                    return this.current < this.end;
                }
                
                fun next() {
                    c = this.current;
                    this.current++;
                    return c;
                }
            }
            
            class It: Iterable {
                var start;
                var end;
                
                It(s, e): super() {
                    this.start = s;
                    this.end = e;
                }
                
                fun iterator() {
                    return new I(this.start, this.end);
                }
            }
            
            total = 0;
            foreach (e : new It(0, 10)) {
                total = total + e;
            }
            return total;
            """
        )
    }

    @Test
    fun testCustomFunctions() {
        assertEquals(15,
            """
            class E: Function {
                E(): super();
            
                fun invoke() {
                    return 10;
                }
                
                fun invoke(arg) {
                    return 1;
                }
                
                fun invoke(args...) {
                    return 2;
                }
            }
            e = new E();
            return e() + e(0) + e(0, 0) + e(0, 0, 0);
            """
        )
    }

    @Test
    fun testCustomError() {
        throwsRuntime(
            """
            class CustomError: Error {
                CustomError(): super();
            }
            throw new CustomError();
            """
        )
    }
}