package me.senseiwells.arucas.test

import me.senseiwells.arucas.util.TestHelper.assertEquals
import org.junit.jupiter.api.Test

class ThreadTest {
    @Test
    fun testThreadScope() {
        assertEquals("[10, 11]",
            """
			l = [];
			thread = Thread.runThreaded(fun() l.append(10));
			sleep(50);
			l.append(11);
			return l.toString();
			"""
        )
    }

    @Test
    fun testThreadStop() {
        assertEquals(10,
            """
			thread = Thread.runThreaded(fun() {
				thread.stop();
			});
			return 10;
			"""
        )
    }

    @Test
    fun testThreadFreezeThaw() {
        assertEquals(11,
            """
            x = 10;
			thread = Thread.runThreaded(fun() {
				Thread.freeze();
                x++;
			});
            sleep(50);
			thread.thaw();
            sleep(50);
			return x;
			"""
        )
    }
}