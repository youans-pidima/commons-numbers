















package org.apache.commons.numbers.field;






public interface Field<T> {







    T add(T a, T b);








    T subtract(T a, T b);







    T negate(T a);








    T multiply(int n, T a);








    T multiply(T a, T b);








    T divide(T a, T b);







    T reciprocal(T a);







    T one();







    T zero();
}
