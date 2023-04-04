package dev.citrus.DS.impl;

/**
 * A DoubleArraySeq keeps track of a sequence of double numbers.
 * The sequence can have a special “current element,” which is specified and accessed through four methods
 * that are not available in the bag class (start, getCurrent, advance, and isCurrent).
 * <p>
 * Limitations:
 * (1) The capacity of a sequence can change after it’s created, but the maximum capacity is limited by the amount of free memory on the machine.
 * The constructor, addAfter, addBefore, clone, and concatenation will result in an OutOfMemoryError when free memory is exhausted.
 * (2) A sequence’s capacity cannot exceed the largest integer, 2,147,483,647 (Integer.MAX_VALUE).
 * Any attempt to create a larger capacity results in failure due to an arithmetic overflow.
 */
public class DoubleArraySeq implements Cloneable {
    double[] data;
    int currentIndex;
    int manyItems;


    /**
     * Initialize an empty sequence with an initial capacity of 10.
     * Note that the addAfter and addBefore methods work efficiently (without needing more memory) until this capacity is reached.
     *
     * Postcondition:
     *     This sequence is empty and has an initial capacity of 10.
     *
     * @throws OutOfMemoryError Indicates insufficient memory for new double[10].
     */
    public DoubleArraySeq() {
        this(10);
    }

    /**
     * Initialize an empty sequence with a specified initial capacity.
     * Note that the addAfter and addBefore methods work efficiently (without needing more memory) until this capacity is reached.
     *
     * Precondition:
     *     initialCapacity is non-negative.
     * Postcondition:
     *     This sequence is empty and has the given initial capacity.
     *
     * @param initialCapacity The initial capacity of this sequence.
     * @throws IllegalArgumentException Indicates that initialCapacity is negative.
     * @throws OutOfMemoryError         Indicates insufficient memory for new double[initialCapacity].
     */
    public DoubleArraySeq(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity should not be negative.");
        }

        this.data = new double[initialCapacity];
        this.currentIndex = -1;
        this.manyItems = 0;
    }

    /**
     * Create a new sequence that contains all the elements from one sequence followed by another.
     *
     * Precondition:
     *     Neither s1 nor s2 is null.
     * Note:
     *     An attempt to increase the capacity beyond Integer.MAX_VALUE will cause this sequence to fail with an arithmetic overflow.
     *
     * @param s1 The first of two sequences.
     * @param s2 The second of two sequences.
     * @return A new sequence that has the elements of s1 followed by the elements of s2 (with no current element)
     * @throws NullPointerException Indicates that one of the arguments is null.
     * @throws OutOfMemoryError     Indicates insufficient memory for the new sequence.
     */
    public static DoubleArraySeq concatenation(DoubleArraySeq s1, DoubleArraySeq s2) {
        if (s1 == null || s2 == null)
            throw new NullPointerException("Arguments should not be null.");

        DoubleArraySeq returnValue = s1.clone();
        returnValue.addAll(s2);

        return returnValue;
    }

    /**
     * Adds a new element to this sequence after the current element.
     * If this new element would take this sequence beyond its current capacity, then the capacity is increased before adding the new element.
     *
     * Postcondition:
     *     A new copy of the element has been added to this sequence.
     *     If there was a current element, then addAfter places the new element after the current element.
     *     If there was no current element, then addAfter places the new element at the end of this sequence.
     *     In all cases, the new element becomes the new current element of this sequence.
     * Note:
     *     An attempt to increase the capacity beyond Integer.MAX_VALUE will cause this sequence to fail with an arithmetic overflow.
     *
     * @param element The new element that is being added.
     * @throws OutOfMemoryError Indicates insufficient memory to increase the size of this sequence.
     */
    public void addAfter(double element) {
        if (size() == getCapacity())
            ensureCapacity(2 * size() + 1);

        if (size() == 0) {
            currentIndex = getCapacity() - 1;
            data[currentIndex] = element;
            manyItems++;
            return;
        }

        for (int i = size(); i > currentIndex + 1; i--) {
            data[i] = data[i - 1];
        }

        data[++currentIndex] = element;
        manyItems++;
    }

    /**
     * Adds a new element to this sequence before the current element.
     *     If this new element would take this sequence beyond its current capacity, then the capacity is increased before adding the new element.
     *
     * Postcondition:
     *     A new copy of the element has been added to this sequence.
     *     If there was a current element, then addBefore places the new element before the current element.
     *     If there was no current element, then addBefore places the new element at the front of this sequence.
     *     In all cases, the new element becomes the new current element of this sequence.
     * Note:
     *     An attempt to increase the capacity beyond Integer.MAX_VALUE will cause this sequence to fail with an arithmetic overflow.
     *
     * @param element The new element that is being added.
     * @throws OutOfMemoryError Indicates insufficient memory to increase the size of this sequence.
     */
    public void addBefore(double element) {
        if (size() == getCapacity())
            ensureCapacity(2 * size() + 1);

        if (size() == 0) {
            currentIndex = 0;
            data[currentIndex] = element;
            manyItems++;
            return;
        }

        for (int i = size(); i > currentIndex; i--) {
            data[i] = data[i - 1];
        }

        data[currentIndex] = element;
        manyItems++;
    }

    /**
     * Place the contents of another sequence at the end of this sequence.
     *
     * Precondition:
     *      The parameter, addend, is not null.
     * Postcondition:
     *     The elements from addend have been placed at the end of this sequence.
     *     The current element of this sequence remains where it was, and the addend is also unchanged.
     * Note:
     *     An attempt to increase the capacity beyond Integer.MAX_VALUE will cause this sequence to fail with an arithmetic overflow.
     *
     * @param addend A sequence whose contents will be placed at the end of this sequence
     * @throws NullPointerException Indicates that addend is null.
     * @throws OutOfMemoryError     Indicates insufficient memory to increase the capacity of this sequence.
     */
    public void addAll(DoubleArraySeq addend) {
        if (addend == null)
            throw new NullPointerException("Addend should not be null.");

        int totalItems = this.size() + addend.size();
        if (totalItems > getCapacity())
            ensureCapacity(2 * (this.size() + addend.size()) + 1);

        System.arraycopy(addend.data, 0, this.data, this.size(), addend.size());

        this.currentIndex = this.size();
        this.manyItems += addend.manyItems;
    }

    /**
     * Move forward so that the current element is now the next element in this sequence.
     *
     * Precondition:
     *     isCurrent() returns true.
     * Postcondition:
     *     If the current element was already the end element of this sequence (with nothing after it), then there is no longer any current element.
     *     Otherwise, the new element is the element immediately after the original current element.
     *
     * @throws IllegalStateException Indicates that there is no current element, so advance may not be called.
     */
    public void advance() {
        if (!isCurrent())
            throw new IllegalStateException("There is no current element.");

        if (size() - 1 == this.currentIndex)
            throw new IllegalStateException("currentIndex is end of sequence.");

        currentIndex++;
    }

    /**
     * Generate a copy of this sequence.
     *
     * @return DoubleArraySeq
     *     The return value is a copy of this sequence.
     *     Subsequent changes to the copy will not affect the original, nor vice versa.
     *     The return value must be typecast to DoubleArraySeq before it is used.
     * @throws OutOfMemoryError Indicates insufficient memory for creating the clone.
     */
    public DoubleArraySeq clone() {
        DoubleArraySeq toReturn;

        try {
            toReturn = (DoubleArraySeq) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("This class does not implement Cloneable.");
        }

        toReturn.data = this.data.clone();
        toReturn.currentIndex = this.currentIndex;
        toReturn.manyItems = this.manyItems;

        return toReturn;
    }

    /**
     * Change the current capacity of this sequence.
     *
     * Postcondition:
     *     This sequence’s capacity has been changed to at least minimumCapacity.
     *
     * @param minimumCapacity the new capacity for this sequence
     * @throws OutOfMemoryError Indicates insufficient memory for new double[minimumCapacity].
     */
    public void ensureCapacity(int minimumCapacity) {
        if (minimumCapacity < this.data.length)
            return;

        double[] replacement = new double[minimumCapacity];
        System.arraycopy(this.data, 0, replacement, 0, manyItems);
        this.data = replacement;
    }

    /**
     * Accessor method to determine the current capacity of this sequence.
     * The addBefore and addAfter methods work efficiently (without needing more memory) until this capacity is reached.
     *
     * @return The current capacity of this sequence
     */
    public int getCapacity() {
        return this.data.length;
    }

    /**
     * Accessor method to determine the current element of this sequence.
     *
     * Precondition:
     *     isCurrent() returns true.
     *
     * @return The current element of this sequence
     * @throws IllegalStateException Indicates that there is no current element.
     */

    public double getCurrent() {
        if (!isCurrent())
            throw new IllegalStateException("There is no current element.");

        return data[currentIndex];
    }

    /**
     * Accessor method to determine whether this sequence has a specified current element that can be retrieved with the getCurrent method.
     *
     * @return true (there is a current element) false (there is no current element at the moment)
     */
    public boolean isCurrent() {
        return size() != 0;
    }

    /**
     * Remove the current element from this sequence.
     *
     * Precondition:
     *     isCurrent() returns true.
     * Postcondition:
     *     The current element has been removed from this sequence, and the following element (if there is one) is now the new current element.
     *     If there was no following element, then there is now no current element.
     *
     * @throws IllegalStateException Indicates that there is no current element, so removeCurrent may not be called.
     */
    public void removeCurrent() {
        if (!isCurrent())
            throw new IllegalStateException("There is no current element.");

        if (size() == 1) {
            data[currentIndex] = 0.0;
        } else {
            for (int i = currentIndex; i < size(); i++) {
                data[i] = data[i + 1];
            }
        }
        manyItems--;
    }

    /**
     * Accessor method to determine the number of elements in this sequence.
     *
     * @return the number of elements in this sequence
     */
    public int size() {
        return manyItems;
    }

    /**
     * Set the current element at the front of this sequence.
     *
     * Postcondition:
     *     The front element of this sequence is now the current element (but if this sequence has no elements at all, then there is no current element).
     */
    public void start() {
        currentIndex = 0;
    }

    /**
     * Reduce the current capacity of this sequence to its actual size (i.e., the number of elements it contains).
     *
     * Postcondition:
     *     This sequence’s capacity has been changed to its current size.
     *
     * @throws OutOfMemoryError Indicates insufficient memory for altering the capacity.
     */
    public void trimToSize() {
        ensureCapacity(this.size());
    }
}