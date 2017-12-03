public class A {
    void m1() {
        m2();
        m1();
    }

    void m2() {
        m2();
        m1();

        B var = new B();
        var.m3();
    }
}

class B {
    void m3() { }
}