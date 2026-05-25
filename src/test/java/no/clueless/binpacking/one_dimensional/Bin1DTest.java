package no.clueless.binpacking.one_dimensional;

import org.junit.jupiter.api.BeforeEach;

class Bin1DTest {
    Bin1D sut;

    @BeforeEach
    void setUp() {
        sut = new Bin1D(35.0);
    }
}