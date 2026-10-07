















package org.apache.commons.numbers.examples.jmh.arrays;




















interface SearchableInterval {





    int left();






    int right();










    int previousIndex(int k);










    int nextIndex(int k);


































    default int split(int ka, int kb, int[] upper) {
        upper[0] = nextIndex(kb + 1);
        return previousIndex(ka - 1);
    }
}
