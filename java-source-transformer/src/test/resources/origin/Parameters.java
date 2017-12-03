class A {
    void m1(int a, int b, int c) {
        a = b;

        int a = 0;
        c = a;
    }

    void m2(int a, int c) {
        int c = 0;
        a = a + c;
    }
}
