// package com.example

public class A {
    private int i1, i2 = 0;

    private void i5() {
        i2 = 1;
        int i3 = this.i1 + i2;

        B i4 = new B();
        i4.m();

        i4.i6 = i2;
        this.i2 = i4.i6 + i3;
    }
}

class B {
    public int i6 = 0;

    public void i8() {
        i6 = 1;
        int i7 = i6 + this.i6;
        i6 = i7 * i7;
    }
}
