package com.quackinduckstries.gamesdonequack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.quackinduckstries.gamesdonequack.entities.LombokTest;

class LombokTestTest {

	@Test
	void testLombokGetterSetter() {
		LombokTest test = new LombokTest();
		test.setMessage("Hello Lombok");
		assertEquals("Hello Lombok", test.getMessage());
	}

}
