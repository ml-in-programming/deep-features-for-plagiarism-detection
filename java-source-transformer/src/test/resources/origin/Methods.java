public class A {
    void a() {
        b();
        a();
    }

    void b() {
        b();
        a();

        B var = new B();
        var.b();
    }
}

class B {
    void b() { }
}