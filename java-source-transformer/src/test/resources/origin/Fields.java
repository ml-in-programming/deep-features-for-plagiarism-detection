// package com.example

public class A {
    private int a, b = 0;

    private void m() {
        b = 1;
        int l = this.a + b;

        B o = new B();
        o.m();

        o.b = b;
        this.b = o.b + l;
    }
}

class B {
    public int b = 0;

    public void m() {
        b = 1;
        int l = b + this.b;
        b = l * l;
    }
}
