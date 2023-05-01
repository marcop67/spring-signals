package com.copytrading.bot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BotApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void textBlockTest() {

		var text = """
				"**{pair}**
				(**{type} {levarage}x**)
				
				**ENTRY**: {entry}
				
				
				**TAKE PROFIT**:
				Target 1: {tp_one}
				Target 2: {tp_two}
				Target 3: {tp_three}
				Target 4: {tp_four}
				Target 5: {tp_five}
				Target 6: {tp_six}
				Target 7: {tp_seven}
				Target 8: {tp_eight}
				Target 9: {tp_nine}
				Target 10: {tp_ten}
				
				
				**STOP LOSS**: {sl}
				
				
				🚀**PREMIUM**
				
				**BUDGET**: {budget}%
				**TP**: {tp}"
				""";




		System.out.println(text);
	}



}
