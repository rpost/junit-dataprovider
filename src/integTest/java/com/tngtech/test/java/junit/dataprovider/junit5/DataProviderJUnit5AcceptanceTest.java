package com.tngtech.test.java.junit.dataprovider.junit5;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Nested;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith(JUnitPlatform.class)
public class DataProviderJUnit5AcceptanceTest {

    @BeforeClass
    public static void beforeClass() {
        System.out.println("beforeClass");
    }

    @Before
    public void before() {
        System.out.println("before");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("after");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        System.out.println("afterClass");
    }

    @DataProvider
    public static Object[][] dataProviderAdd() {
        //@formatter:off
        return new Object[][] {
            {  0,  0,  0 },
            {  0,  1,  1 },
            {  1,  0,  1 },
            {  1,  1,  2 },

            {  0, -1, -1 },
            { -1, -1, -2 },
        };
        //@formatter:on
    }

    @Test
    @UseDataProvider
    public void testAdd(int a, int b, int expected) throws Exception {
        System.out.println("test");

        // Expect:
        assertThat(a + b).isEqualTo(expected);
    }

    @Test
    public void testMinus() throws Exception {
        System.out.println("test");

        // Expect:
        assertThat(2 - 1).isEqualTo(1);
    }

    @org.junit.jupiter.api.Test
    void myFirstTest() {
        assertThat(1 + 2).isEqualTo(3);
    }

    @Nested
    public class InnerTest {

        @org.junit.jupiter.api.Test
        public void testName() throws Exception {
            System.out.println("TestName");
        }
    }
}
