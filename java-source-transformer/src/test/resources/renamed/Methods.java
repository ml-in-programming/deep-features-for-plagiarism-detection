public class A {
    void i1() {
        i3();
        i1();
    }

    void i3() {
        i3();
        i1();

        B i2 = new B();
        i2.i4();
    }
}

class B {
    void i4() { }
}