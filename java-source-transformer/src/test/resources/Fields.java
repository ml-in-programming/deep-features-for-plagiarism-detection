// package com.example

public class A {
    private int a, b = 0;

    private void m() {
        b = 1;
        int l = this.a + b;

        B o = new B();
        o.m();

        o.f3 = b;
        this.b = o.f3 + l;
    }
}

class B {
    public int b = 0;

    public void m() {
        f3 = 1;
        int l = f3 + this.f3;
        f3 = l * l;
    }
}
