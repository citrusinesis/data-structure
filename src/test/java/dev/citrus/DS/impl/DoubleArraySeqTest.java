package dev.citrus.DS.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoubleArraySeqTest {
    DoubleArraySeq empty;
    DoubleArraySeq prepared;
    DoubleArraySeq small;

    void assertDoubleArraySeq(double[] data, int currentIndex, int manyItems, DoubleArraySeq actual) {
        assertAll(
                () -> assertArrayEquals(data, actual.data),
                () -> assertEquals(currentIndex, actual.currentIndex),
                () -> assertEquals(manyItems, actual.manyItems)
        );
    }

    void assertDoubleArraySeq(DoubleArraySeq expected, DoubleArraySeq actual) {
        assertAll(
                () -> assertArrayEquals(expected.data, actual.data),
                () -> assertEquals(expected.currentIndex, actual.currentIndex),
                () -> assertEquals(expected.manyItems, actual.manyItems)
        );
    }

    @BeforeEach
    void initialize() {
        empty = new DoubleArraySeq();
        prepared = new DoubleArraySeq();
        small = new DoubleArraySeq(1);

        prepared.data = new double[]{0.1, 0.2, 0.3, 0.4, 0.5, 0.0, 0.0};
        prepared.manyItems = 5;
        prepared.currentIndex = 0;

        small.data = new double[]{0.1};
        small.manyItems = 1;
        small.currentIndex = 0;
    }

    @Test
    void defaultConstructor() {
        // given
        DoubleArraySeq test;
        double[] data = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

        // when
        test = new DoubleArraySeq();

        // then
        assertDoubleArraySeq(data, -1, 0, test);
    }

    @Test
    void constructor() {
        // given
        DoubleArraySeq test;
        double[] data = {0.0, 0.0, 0.0};

        // when
        test = new DoubleArraySeq(3);

        // then
        assertDoubleArraySeq(data, -1, 0, test);

        // throws
        assertThrows(IllegalArgumentException.class, () -> new DoubleArraySeq(-1));
    }

    @Test
    void addAfter() {
        // given -> initialize()
        double[] emptyData = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1};
        double[] preparedData = {0.1, 0.1, 0.2, 0.3, 0.4, 0.5, 0.0};
        double[] smallData = {0.1, 0.1, 0.0};

        // when
        empty.addAfter(0.1);
        prepared.addAfter(0.1);
        small.addAfter(0.1);

        // then
        assertDoubleArraySeq(emptyData, 9, 1, empty);
        assertDoubleArraySeq(preparedData, 1, 6, prepared);
        assertDoubleArraySeq(smallData, 1, 2, small);
    }

    @Test
    void addBefore() {
        // given -> initialize()
        double[] emptyData = {0.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        double[] preparedData = {0.1, 0.1, 0.2, 0.3, 0.4, 0.5, 0.0};
        double[] smallData = {0.1, 0.1, 0.0};

        // when
        empty.addBefore(0.1);
        prepared.addBefore(0.1);
        small.addBefore(0.1);

        // then
        assertDoubleArraySeq(emptyData, 0, 1, empty);
        assertDoubleArraySeq(preparedData, 0, 6, prepared);
        assertDoubleArraySeq(smallData, 0, 2, small);
    }

    @Test
    void addAll() {
        // given -> initialize()
        double[] data = {0.1, 0.1, 0.2, 0.3, 0.4, 0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

        // when
        small.addAll(prepared);

        // then
        assertDoubleArraySeq(data, 1, 6, small);

        // throws
        assertThrows(NullPointerException.class, () -> empty.addAll(null));
    }

    @Test
    void advance() {
        // given -> initialize()

        // when
        prepared.advance();

        // then
        assertEquals(1, prepared.currentIndex);

        // throws
        assertThrows(IllegalStateException.class, () -> empty.advance());
        assertThrows(IllegalStateException.class, () -> {
            prepared.currentIndex = prepared.manyItems - 1;
            prepared.advance();
        });
    }

    @Test
    void testClone() {
        // given -> initialize()

        // when
        DoubleArraySeq cloneResult = prepared.clone();

        // then
        assertDoubleArraySeq(prepared, cloneResult);
    }

    @Test
    void concatenation() {
        // given -> initialize()
        DoubleArraySeq shouldBe = new DoubleArraySeq();
        shouldBe.data = new double[]{0.1, 0.2, 0.3, 0.4, 0.5, 0.1, 0.0};
        shouldBe.manyItems = 6;
        shouldBe.currentIndex = 5;

        // when
        DoubleArraySeq resultSeq = DoubleArraySeq.concatenation(prepared, small);

        // then
        assertDoubleArraySeq(shouldBe, resultSeq);

        // throws
        assertAll(
                () -> assertThrows(
                        NullPointerException.class,
                        () -> DoubleArraySeq.concatenation(new DoubleArraySeq(), null)
                ),
                () -> assertThrows(
                        NullPointerException.class,
                        () -> DoubleArraySeq.concatenation(null, new DoubleArraySeq())
                ),
                () -> assertThrows(
                        NullPointerException.class,
                        () -> DoubleArraySeq.concatenation(null, null)
                )
        );
    }

    @Test
    void ensureCapacity() {
        // given -> initialize()
        double[] shouldBe = prepared.data.clone();

        // when
        int checkSize = prepared.data.length + 10;
        prepared.ensureCapacity(checkSize);

        // then
        assertEquals(checkSize, prepared.data.length);
        for (int i = 0; i < shouldBe.length; i++)
            assertEquals(shouldBe[i], prepared.data[i]);
    }

    @Test
    void getCapacity() {
        // given -> initialize()

        // when

        // then
        assertEquals(empty.data.length, empty.getCapacity());
        assertEquals(prepared.data.length, prepared.getCapacity());
        assertEquals(small.data.length, small.getCapacity());
    }

    @Test
    void getCurrent() {
        // given -> initialize()

        // when

        // then
        assertEquals(prepared.data[prepared.currentIndex], prepared.getCurrent());
        assertEquals(small.data[small.currentIndex], small.getCurrent());

        // throws
        assertThrows(IllegalStateException.class, () -> empty.getCurrent());
    }

    @Test
    void isCurrent() {
        // given -> initialize()

        // when

        // then
        assertFalse(empty.isCurrent());
        assertTrue(prepared.isCurrent());
        assertTrue(small.isCurrent());
    }

    @Test
    void removeCurrent() {
        // given -> initialize()
        double[] preparedShouldBe = new double[]{0.2, 0.3, 0.4, 0.5, 0.0, 0.0, 0.0};
        double[] smallShouldBe = new double[]{0.0};

        // when
        prepared.removeCurrent();
        small.removeCurrent();

        // then
        assertArrayEquals(preparedShouldBe, prepared.data);
        assertArrayEquals(smallShouldBe, small.data);

        // throws
        assertThrows(IllegalStateException.class, () -> new DoubleArraySeq(0).removeCurrent());
    }

    @Test
    void size() {
        // given -> initialize()


        // when

        // then
        assertEquals(empty.manyItems, empty.size());
        assertEquals(prepared.manyItems, prepared.size());
        assertEquals(small.manyItems, small.size());
    }

    @Test
    void start() {
        // given -> initialize()

        // when
        empty.start();

        // then
        assertEquals(0, empty.currentIndex);
    }

    @Test
    void trimToSize() {
        // given -> initialize()

        // when
        empty.trimToSize();
        prepared.trimToSize();
        small.trimToSize();

        // then
        assertEquals(empty.manyItems, empty.data.length);
        assertEquals(prepared.manyItems, prepared.data.length);
        assertEquals(small.manyItems, small.data.length);
    }
}