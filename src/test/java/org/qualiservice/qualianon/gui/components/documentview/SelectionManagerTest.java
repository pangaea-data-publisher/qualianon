package org.qualiservice.qualianon.gui.components.documentview;

import javafx.scene.control.IndexRange;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


public class SelectionManagerTest {

    @Test
    public void extendRight() {
        assertEquals(new IndexRange(0, 4), SelectionManager.extendSelection("Test it", 0));
    }

    @Test
    public void extendToEnd() {
        assertEquals(new IndexRange(0, 4), SelectionManager.extendSelection("Test", 0));
    }

    @Test
    public void extendOverUmlaut() {
        assertEquals(new IndexRange(0, 4), SelectionManager.extendSelection("Täst", 0));
    }

    @Test
    public void extendLeft() {
        assertEquals(new IndexRange(5, 9), SelectionManager.extendSelection("Test some", 7));
    }

    @Test
    public void extendToBeginning() {
        assertEquals(new IndexRange(0, 4), SelectionManager.extendSelection("Test", 2));
    }

    @Test
    public void extendRange() {
        assertEquals(new IndexRange(0, 4), SelectionManager.extendSelection("Test", new IndexRange(1, 3)));
    }

    @Test
    public void extendRightContractOnWhitespace() {
        assertEquals(new IndexRange(0, 4), SelectionManager.extendSelection("Test it", new IndexRange(0, 5)));
    }

    @Test
    public void extendRightWithDigit() {
        assertEquals(new IndexRange(0, 4), SelectionManager.extendSelection("Tes8", 0));
    }

    @Test
    public void insideSquareBraces() {
        assertEquals(new IndexRange(3, 6), SelectionManager.extendSelection("Tes[8]t", 4));
    }

    @Test
    public void insideSquareBraces2() {
        assertEquals(new IndexRange(3, 6), SelectionManager.extendSelection("Tes[8]t", 5));
    }

    @Test
    public void insideSquareBraces3() {
        assertEquals(new IndexRange(3, 6), SelectionManager.extendSelection("Tes[8]t", 3));
    }

    @Test
    public void insideSquareBracesIncorrectRight() {
        assertEquals(new IndexRange(4, 5), SelectionManager.extendSelection("Tes[8[]t", 4));
    }

    @Test
    public void insideSquareBracesIncorrectLeft() {
        assertEquals(new IndexRange(4, 5), SelectionManager.extendSelection("Te[]8]st", 4));
    }

    @Test
    public void insideSquareBracesFullSelection() {
        assertEquals(new IndexRange(3, 6), SelectionManager.extendSelection("Te [8] st", new IndexRange(3, 6)));
    }

    @Test
    public void insideSquareBracesFullSelectionWithWhitespace() {
        assertEquals(new IndexRange(3, 6), SelectionManager.extendSelection("Te [8] st", new IndexRange(2, 7)));
    }

    @Test
    public void rangeCrossesMarking1() {
        assertEquals(true, SelectionManager.rangeCrossesMarking("["));
    }

    @Test
    public void rangeCrossesMarking2() {
        assertEquals(true, SelectionManager.rangeCrossesMarking("]"));
    }

    @Test
    public void rangeCrossesMarking3() {
        assertEquals(false, SelectionManager.rangeCrossesMarking("abc"));
    }

    @Test
    public void rangeCrossesMarking4() {
        assertEquals(false, SelectionManager.rangeCrossesMarking("[5]"));
    }

    @Test
    public void rangeCrossesMarking5() {
        assertEquals(true, SelectionManager.rangeCrossesMarking("[[5]"));
    }
}
