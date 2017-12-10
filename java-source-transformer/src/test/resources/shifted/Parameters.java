class A {
    void m1(int c, int a, int b) {
        a = b;

        int a = 0;
        c = a;
    }

    void m2(int c, int a) {
        int c = 0;
        a = a + c;
    }
}
