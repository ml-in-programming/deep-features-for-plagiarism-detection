// package com.example

public class A {
    private int f1, f2 = 0;

    private void m() {
        f2 = 1;
        int l = this.f1 + f2;

        B o = new B();
        o.m();

        o.f3 = f2;
        this.f2 = o.f3 + l;
    }
}

class B {
    public int f3 = 0;

    public void m() {
        f3 = 1;
        int l = f3 + this.f3;
        f3 = l * l;
    }
}
