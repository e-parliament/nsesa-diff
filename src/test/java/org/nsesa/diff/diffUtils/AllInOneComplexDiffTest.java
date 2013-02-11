/**
 * Copyright 2013 European Parliament
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package org.nsesa.diff.diffUtils;

import org.junit.Assert;
import org.junit.Test;
import org.nsesa.diff.diffUtils.service.model.DefaultThreeWayDiffContext;
import org.nsesa.diff.diffUtils.service.model.DiffMethod;


public class AllInOneComplexDiffTest {

    @Test
    public void testCharacterComplexDiffxxxxx() {
        String original = "<p>The Union and the Member States should strive to provide the most up-to-date information on their greenhouse gas emissions, in particular under the framework of the Europe 2020 strategy and its specified timelines. This Regulation should enable such estimates to be prepared in the shortest timeframes possible by using statistical and other information.</p>";
        String modified = "<p>The Union and the Member States should strive to provide the most up-to-date information on their greenhouse gas emissions, in particular under the framework of the Europe 2020 strategy and its specified timelines, and in the framework of the European space policy and strategy that addresses important challenges such as natural disasters, resources and climate monitoring, for the benefits of EU citizens. In that respect, space-based data should be considered as key monitoring tools for the Union and the Member States due to their capacity to improve the overall picture of CO2 and CH4 emissions, as well as LULUCF. To that end, the GMES programme and other satellite systems should be used to the maximum extent to provide timely emission reporting (global daily measurement of CO2 and CH4 man-made and rural emissions as well as CO2 sinks) and independent verifications of the calculated emission reports. This Regulation should enable such estimates to be prepared in the shortest timeframes possible by using statistical and other information.</p>";
        String verified = "<p>The Union and the Member States should strive to provide the most up-to-date information on their greenhouse gas emissions, in particular under the framework of the Europe 2020 strategy and its specified timelines, and in the framework of the European space policy and strategy that addresses important challenges such as natural disasters, resources and climate monitoring, for the benefits of EU citizens. In that respect, space-based data should be considered as key monitoring tools for the Union and the Member States due to their capacity to improve the overall picture of CO2 and CH4 emissions, as well as LULUCF. To that end, the GMES programme and other satellite systems should be used to the maximum extent to provide timely emission reporting (global daily measurement of CO2 and CH4 man-made and rural emissions as well as CO2 sinks) and independent verifications of the calculated emission reports. This Regulation should enable such estimates to be prepared in the shortest timeframes possible by using statistical and other information.</p>";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        result = DiffUtils.threeWayDiff(original, modified, verified, new DefaultThreeWayDiffContext(DiffMethod.WORD));
        //assertWords(result, "<red>This</red> is <bi>a</bi> <red>test</red>.", "<blue>This<ins>s</ins></blue> is <bi>an <blue>idiot<ins>s</ins>ic</blue></bi> test.");
    }

    @Test
    public void testCharacterComplexDiffxxxxxxx() {
        String original = "The Union and the Member States should strive to provide the most up-to-date information on their greenhouse gas emissions, in particular under the framework of the Europe 2020 strategy and its specified timelines. This Regulation should enable such estimates to be prepared in the shortest timeframes possible by using statistical and other information.";
        String modified = "The Union and the Member States should strive to provide the most up-to-date information on their greenhouse gas emissions, in particular under the framework of the Europe 2020 strategy and its specified timelines, and in the framework of the European space policy and strategy that addresses important challenges such as natural disasters, resources and climate monitoring, for the benefits of EU citizens. In that respect, space-based data should be considered as key monitoring tools for the Union and the Member States due to their capacity to improve the overall picture of CO2 and CH4 emissions, as well as LULUCF. To that end, the GMES programme and other satellite systems should be used to the maximum extent to provide timely emission reporting (global daily measurement of CO2 and CH4 man-made and rural emissions as well as CO2 sinks) and independent verifications of the calculated emission reports. This Regulation should enable such estimates to be prepared in the shortest timeframes possible by using statistical and other information.The Union and the Member States should strive to provide the most up-to-date information on their greenhouse gas emissions, in particular under the framework of the Europe 2020 strategy and its specified timelines. This Regulation should enable such estimates to be prepared in the shortest timeframes possible by using statistical and other information.";
        String verified = "The Union and the Member States should strive to provide the most up-to-date information on their greenhouse gas emissions, in particular under the framework of the Europe 2020 strategy and its specified timelines, and in the framework of the European space policy and strategy that addresses important challenges such as natural disasters, resources and climate monitoring, for the benefits of EU citizens. In that respect, space-based data should be considered as key monitoring tools for the Union and the Member States due to their capacity to improve the overall picture of CO2 and CH4 emissions, as well as LULUCF. To that end, the GMES programme and other satellite systems should be used to the maximum extent to provide timely emission reporting (global daily measurement of CO2 and CH4 man-made and rural emissions as well as CO2 sinks) and independent verif)ications of the calculated emission reports. This Regulation should enable such estimates to be prepared in the shortest timeframes possible by using statistical and other information.The Union and the Member States should strive to provide the most up-to-date information on their greenhouse gas emissions, in particular under the framework of the Europe 2020 strategy and its specified timelines. This Regulation should enable such estimates to be prepared in the shortest timeframes possible by using statistical and other information.";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        result = DiffUtils.threeWayDiff(original, modified, verified, new DefaultThreeWayDiffContext(DiffMethod.WORD));

        //assertWords(result, "<red>This</red> is <bi>a</bi> <red>test</red>.", "<blue>This<ins>s</ins></blue> is <bi>an <blue>idiot<ins>s</ins>ic</blue></bi> test.");
    }

    @Test
    public void testCharacterComplexDiffxxx() {
        String original = "This is a test.";
        String modified = "This is an idiotic test.";
        String verified = "Thiss is an idiotsic.";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        //assertWords(result, "<red>This</red> is <bi>a</bi> <red>test</red>.", "<blue>This<ins>s</ins></blue> is <bi>an <blue>idiot<ins>s</ins>ic</blue></bi> test.");
    }

    @Test
    public void testCapitalization2() {
        String original = "This is a test.";
        String modified = "This iS A Test.";
        String verified = "this Is a test.";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        System.out.println(result[0]);
        System.out.println(result[1]);

        //assertWords(result, "<red>This</red> is <bi>a</bi> <red>test</red>.", "<blue>This<ins>s</ins></blue> is <bi>an <blue>idiot<ins>s</ins>ic</blue></bi> test.");
    }

    @Test
    public void testBrackets() {
        String original = "( 1 2 3 )";
        String modified = "(1 2 3)";
        String verified = "(1 2  3 5)";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        System.out.println(result[0]);
        System.out.println(result[1]);

        //assertWords(result, "<red>This</red> is <bi>a</bi> <red>test</red>.", "<blue>This<ins>s</ins></blue> is <bi>an <blue>idiot<ins>s</ins>ic</blue></bi> test.");
    }

    @Test
    public void testCharacterComplexDiff() {
        String original = "Selected candidates.";
        String modified = "Selected candidates.";
        String verified = "All of the selected candidates.";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "Selected candidates.", "<ins>All of the</ins> selected candidates.");
    }

    @Test
    public void testDiff() {
        /*String original = "Selected candidates from national diplomatic services who are second by their Member States should be employed as temporary agents and thus be put on equal footing with officials. The implementing provisions to be adopted by the EEAS should guarantee career perspectives for temporary agents that are equivalent to those officials.";
        String modified = "Selected candidates from national diplomatic services who are second by their Member States should be recruited on the basis of an objective and transparant procedure and employed as temporary agents and thus be put on equal footing with officials. The implementing provisions to be adopted by the EEAS should guarantee career prospects within the EEAS for temporary agents that are equivalent to those officials.";
		String verified = "All of the selected candidates from national diplomatic services who are second by their Member States should be recruited based on an objective and transparant procedures and employed as temporary agents and thus be put on equal footing with officials. The provisions to be adopted by the EEAS should guarantee career prospects within the EEAS for temporary agents that are equivalent to those officials.";
		
		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
		
		assertWords(result, "Selected candidates from national diplomatic services who are second by their Member States should be employed as temporary agents and thus be put on equal footing with officials. The <red>implementing</red> provisions to be adopted by the EEAS should guarantee career <bi>perspectives</bi> for temporary agents that are equivalent to those officials.", "<ins>All of the</ins> selected candidates from national diplomatic services who are second by their Member States should be <bi>recruited <ins>based</ins> on <del>the basis of</del> an objective and transparant <blue>procedure<ins>s</ins></blue> and</bi> employed as temporary agents and thus be put on equal footing with officials. The <del>implementing</del> provisions to be adopted by the EEAS should guarantee career <bi>prospects within the EEAS</bi> for temporary agents that are equivalent to those officials.");*/
    }

    @Test
    public void testOriginalDeletion() {
        String original = "This is a test";
        String modified = "This is test";
        String verified = "This is test";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "This is <bi>a</bi> test", "This is test");

        // with markup
        original = "<p>This is a test</p>";
        modified = "<p>This is test</p>";
        verified = "<p>This is test</p>";

        result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "<p>This is <bi>a</bi> test</p>", "<p>This is test</p>");
    }

    @Test
    public void testOriginalInsertion() {
        String original = "This is test";
        String modified = "This is a test";
        String verified = "This is a test";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "This is test", "This is <bi>a</bi> test");

        // with markup
        original = "<p>This is test</p>";
        modified = "<p>This is a test</p>";
        verified = "<p>This is a test</p>";

        result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "<p>This is test</p>", "<p>This is <bi>a</bi> test</p>");
    }

    @Test
    public void testOriginalChange() {
        String original = "This is a test";
        String modified = "This is a tst";
        String verified = "This is a tst";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "This is a <bi>test</bi>", "This is a <bi>tst</bi>");
    }

    @Test
    public void testSimpleDstChangeWord() {
        String original = "This is a test";
        String modified = "This is a test";
        String verified = "Thise is a test";

        DefaultThreeWayDiffContext context = new DefaultThreeWayDiffContext(DiffMethod.WORD);

        String[] result = DiffUtils.threeWayDiff(original, modified, verified, context);

        assertWords(result, "<red>This</red> is a test", "<del>This</del><ins>Thise</ins> is a test");
    }

    @Test
    public void testSimpleDstInsertWord() {
        String original = "This is a test";
        String modified = "This is a test";
        String verified = "This ax is a test";

        DefaultThreeWayDiffContext context = new DefaultThreeWayDiffContext(DiffMethod.WORD);

        String[] result = DiffUtils.threeWayDiff(original, modified, verified, context);

        assertWords(result, "This is a test", "This <ins>ax</ins> is a test");
    }

    @Test
    public void testSimpleDstChangeAndInsertWord() {
        String original = "This is a test";
        String modified = "This is a test";
        String verified = "Thise ax is a test";

        DefaultThreeWayDiffContext context = new DefaultThreeWayDiffContext(DiffMethod.WORD);

        String[] result = DiffUtils.threeWayDiff(original, modified, verified, context);

        assertWords(result, "<red>This</red> is a test", "<del>This</del><ins>Thise ax</ins> is a test");
    }

    @Test
    public void testSimpleDstChangeCharacter() {
        String original = "This is a test";
        String modified = "This is a test";
        String verified = "Thise is a test";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "<red>This</red> is a test", "<blue>This<ins>e</ins></blue> is a test");
    }

    @Test
    public void testSimpleDstChangeInChangeCharacter() {
        String original = "This is a test";
        String modified = "Thiss is a test";
        String verified = "Thissse ss is a test";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "<bi>This</bi> is a test", "<blue>Thiss<ins>se ss</ins></blue> is a test");
    }

    @Test
    public void testSimpleDstChange2() {
        String original = "This is a test to prove the working of the complex diffing.";
        String modified = "This is a test to prove the working of the new superduper xxx complex diffing.";
        String verified = "This is a test to prove the working of the ne xxx complex diffing.";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "This is a test to prove the working of the complex diffing.", "This is a test to prove the working of the <blue>ne<del>w superduper</del></blue> <bi>xxx</bi> complex diffing.");
    }

    @Test
    public void testSimpleDstChangeWithOriginalDeletion() {
        String original = "This is a test for discovering if changes work";
        String modified = "This for discovering if changes work";
        String verified = "This fors discovering if changes work";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "This <bi>is a test</bi> <red>for</red> discovering if changes work", "This <blue>for<ins>s</ins></blue> discovering if changes work");
    }

    @Test
    public void testSimpleDstChangeWithOriginalDelete() {
        String original = "This is a test for discovering if changes work";
        String modified = "This is a discovering if changes";
        String verified = "This is a discovering changes";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "This is a <bi>test for</bi> discovering <red>if</red> changes <bi>work</bi>", "This is a discovering <del>if</del> changes");
    }

    @Test
    public void testSimpleDstChangeWithOriginalChange() {
        String original = "This is a test for discovering if changes work";
        String modified = "This was something for discovering if changes work";
        String verified = "This was something fors discovering if changes work";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "This <bi>is a test</bi> <red>for</red> discovering if changes work", "This <bi>was something</bi> <blue>for<ins>s</ins></blue> discovering if changes work");
    }

    @Test
    public void testSimpleDstMultiwordChangeWithOriginalChange() {
        /*String original = "This is a test for discovering if changes work";
		String modified = "This was something for discovering if changes work";
		String verified = "This was something fors another discovering if changes work";
		
		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
		
		assertWords(result, "This <bi>is a test</bi> <red>for</red> discovering if changes work", "This <bi>was something</bi> <blue>for<ins>s</ins></blue> <ins>another</ins> discovering if changes work");*/
    }

    @Test
    public void testSimpleDstInsert() {
        String original = "This is a test";
        String modified = "This is a test";
        String verified = "This is a worthwhile test";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "This is a test", "This is a <ins>worthwhile</ins> test");
    }

    @Test
    public void testSimpleDstDelete() {
        String original = "This is a test";
        String modified = "This is a test";
        String verified = "This is test";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "This is <red>a</red> test", "This is <del>a</del> test");
    }

    @Test
    public void testComplexDst() {
		/*String original = "This is a test that I just made up. Selected candidates from countries will be represented.";
		String modified = "This is a test I just made up. The selected candidates from countries that are part of the EU will be represented.";
		String verified = "This is a test I just made up. The selected candidate countries from countries that part of the Union will be represented.";
		
		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
		
		assertWords(result, "This is a test <bi>that</bi> I just made up. Selected <red>candidates</red> from countries will be represented.", "This is a test I just made up. <bi>The selected</bi> <blue>candidate<ins> countrie</ins>s</blue> from countries <bi>that <del>are</del> part of the <blue><del>E</del>U<ins>nion</ins></blue></bi> will be represented.");*/
    }

    @Test
    public void testAnotherComplexDst() {
		/*String original = "Credit institutions that are member of a scheme referred to in Article 1(3) and 1(4) shall inform depositors adequately on the functioning of the scheme. Such information may not contain a reference to unlimited coverage of deposits.";
		String modified = "Credit institutions shall inform depositors adequately, and in an easy-to-understand fashion, about the functioning of the Deposit Guarantee Scheme. In the process, the maximum coverage level and other sources of information on the Deposit Guarantee Scheme shall also be addressed. Such information may not contain a reference to unlimited coverage of deposits, however.";
		String verified = "Credit institutions shall inform depositors adequately, and in a way which is easy-to-understand, concerning the functioning of the Deposit Guarantee Scheme. In the process, however, the maximum coverage level and other sources of information on the Deposit Guarantee Scheme shall also be addressed. Such information may not contain a reference to unlimited coverage of deposits.";
		
		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
		
		assertWords(result, "Credit institutions <bi>that are member of a scheme referred to in Article 1(3) and 1(4)</bi> shall inform depositors adequately <bi>on</bi> the functioning of the <bi>scheme</bi>. Such information may not contain a reference to unlimited coverage of deposits.", "Credit institutions shall inform depositors adequately<bi>, and in <blue>a<del>n</del><ins> way which is</ins></blue> easy-to-understand <del>fashion</del>, <blue><del>about</del><ins>concerning</ins></blue></bi> the functioning of the <bi>Deposit Guarantee Scheme</bi>. <bi>In the process, <ins>however,</ins> the maximum coverage level and other sources of information on the Deposit Guarantee Scheme shall also be addressed.</bi> Such information may not contain a reference to unlimited coverage of deposits<bi><del>, however</del></bi>.");*/
    }

    @Test
    //@Ignore // spacing issue
    public void testAnotherComplexDst2() {
		/*String original =    "Member States shall ensure that credit institutions make available to actual and intending depositors the information necessary for the identification of the Deposit Guarantee Scheme of which the institution and its branches are members within the Union . . When a deposit is not guaranteed by a Deposit Guarantee Scheme in accordance with Article 4 , the credit institution shall inform the depositor accordingly.";
		String modified =    "Member States shall ensure that credit institutions make available to actual and intending depositors the information necessary for the identification of the Deposit Guarantee Scheme of which the institution and its branches are members within the Union . . When a deposit is not guaranteed by a Deposit Guarantee Scheme in accordance with Article 4, 1 a-g, i-k and Article 4, 2 , the credit institution shall inform the depositor accordingly and in that case depositors shall be offered the possibility to withdraw their deposits without incurring any penalty fees, and with the right to all accrued interest and benefits.";
		String verified = "Member States shall ensure that credit institutions make available to actual and intending depositors the necessary information for the identification of the Deposit Guarantee Scheme of which the institution and its branches are members within the Union. When a deposit is not guaranteed by a Deposit Guarantee Scheme in accordance with Article 4(1)(a) to (g) and (i) to (k) and Article 4(2), the credit institution shall inform the depositor accordingly, whereupon the depositor shall be offered the possibility to withdraw his or her deposits without incurring any penalty fees, and with the all attained interests and benefits.";
		
		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
		
		assertWords(result, "Member States shall ensure that credit institutions make available to actual and intending depositors the <red>information</red> necessary for the identification of the Deposit Guarantee Scheme of which the institution and its branches are members within the Union . <red>.</red> When a deposit is not guaranteed by a Deposit Guarantee Scheme in accordance with Article <red>4</red> <red>,</red> the credit institution shall inform the depositor accordingly.", "Member States shall ensure that credit institutions make available to actual and intending depositors the <del>information</del> necessary <ins>information</ins> for the identification of the Deposit Guarantee Scheme of which the institution and its branches are members within the Union . <del>.</del> When a deposit is not guaranteed by a Deposit Guarantee Scheme in accordance with Article <blue>4<del>, 1 a-g, i-k</del><ins>(1)(a) to (g)</ins></blue> and <ins>(i) to (k) and</ins> Article <blue>4<ins>(2)</ins></blue>, <del>2 ,</del></bi> the credit institution shall inform the depositor accordingly <bi><blue><del>and in that cas</del><ins>, whereupon th</ins>e depositor<del>s</del></blue> shall be offered the possibility to withdraw <blue><del>t</del><ins>his or </ins>he<del>i</del>r</blue> deposits without incurring any penalty fees, and with the <del>right to</del> all <blue>a<del>ccru</del><ins>ttain</ins>ed interest<ins>s</ins></blue> and benefits</bi>.");*/
    }

    @Test
    public void testAnotherComplexDst3() {
		/*String original = "Information to actual depositors shall be provided on their statements of account. This information shall consist of a confirmation that the deposits are eligible pursuant to Article 2(1) and Article 4. Moreover, reference shall be made to the information sheet in Annex III and where it can be obtained. The web site of the responsible Deposit Guarantee Scheme may also be indicated.";
		String modified = "Information to actual depositors shall be provided on their statements of account. This information shall consist of a confirmation that the deposits are eligible pursuant to Article 2(1) and Article 4. Moreover, reference shall be made to the information sheet in Annex III and where it can be obtained. The website of the responsible DGS must be indicated. The website must contain the necessary information for depositors, in particular information concerning the provisions regarding the process and conditions of deposit guarantees as envisaged by this directive.";
		String verified = "Information to actual depositors shall be provided on their statements of account. This information shall consist of a confirmation that the deposits are eligible pursuant to Article 2(1) and Article 4. Moreover, reference shall be made to the information sheet in Annex III and where it can be obtained. The information sheet in Annex III shall also be attached to one of their statements of account at least once a year. The website of the responsible Deposit Guarantee Scheme shall also be indicated. The website must contain the necessary information for depositors, in particular information concerning the provisions regarding the process and conditions of deposit guarantees as envisaged by this Directive.";
		
		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
		
		assertWords(result, "Information to actual depositors shall be provided on their statements of account. This information shall consist of a confirmation that the deposits are eligible pursuant to Article 2(1) and Article 4. Moreover, reference shall be made to the information sheet in Annex III and where it can be obtained. The <bi>web site</bi> of the responsible <bi>Deposit Guarantee Scheme may also</bi> be indicated.", "Information to actual depositors shall be provided on their statements of account. This information shall consist of a confirmation that the deposits are eligible pursuant to Article 2(1) and Article 4. Moreover, reference shall be made to the information sheet in Annex III and where it can be obtained. The <ins>information sheet in Annex III shall also be attached to one of their statements of account at least once a year. The</ins> <bi>website</bi> of the responsible <bi><blue>D<del>GS must</del><ins>eposit Guarantee Scheme shall also</ins></blue> be indicated. <bi>The website must contain the necessary information for depositors, in particular information concerning the provisions regarding the process and conditions of deposit guarantees as envisaged by this <blue><del>d</del><ins>D</ins>irective</blue>.</bi>");*/
    }

    @Test
    public void testAnotherComplexDst4() {
        String original = "than 0.25% of eligible deposits.";
        String modified = "than 0.25% of deposits covered.";
        String verified = "than 0,25 % of covered deposits .";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "than <red>0.25%</red> of <bi>eligible</bi> deposits.", "than <blue>0<del>.</del><ins>,</ins>25 %</blue> of <ins>covered</ins> deposits <del>covered</del>.");
    }

    @Test
    public void testAnotherComplexDst5() {
		/*String original =    "(1) Directive 2000/25/EC of the European Parliament and of the Council of 22 May 2000 on action to be taken against the emission of gaseous and particulate pollutants by engines intended to power agricultural or forestry tractors and amending Council Directive 74/150/EEC9 regulates exhaust emissions from engines installed in agricultural and forestry tractors. The current stage of emission limits applicable for type approval of the majority of compression ignition engines is referred to as Stage III A. The directive provides that those limits will be replaced by the more stringent Stage III B limits, entering into force progressively as of 1st January 2011 with regard to the placing on the market and from 1st January 2010 as regards the type approval for those engines. Stage IV, providing for limit values more stringent than stage III B, will enter into force progressively as of 1st January 2013 as regards the type approval for those engines and as of 1st January 2014 with regard to the placing on the market.";
		String modified =    "(1) Directive 2000/25/EC of the European Parliament and of the Council of 22 May 2000 on action to be taken against the emission of gaseous and particulate pollutants by engines intended to power agricultural or forestry tractors and amending Council Directive 74/150/EEC9 regulates exhaust emissions from engines installed in agricultural and forestry tractors with a view to further safeguarding human health and ee the we environment. The current stage of emission limits applicable for type approval of the majority of compression ignition egines is referred to as Stage III A. The directive provides that those limits will be replaced by the more stringent Stage III B limits,wentering into force progressively as of 1st January 2011 with regard to the placing on the market and from 1st January ee 2010 ww as regarrds the type approval for those engines. Stage IV, providing for limit values more stringent than stage III B, will enter into force progressively as of 1st January 2013 as regards the type approval for those engines and as of 1st January 2014 with regard to the placing on the market.";
		String verified = "(1) Directive 2000/25/EC of the European Parliament and of the Council of 22 May 2000 on action to be taken against the emission of gaseous and particulate pollutants by engines intended to power agricultural or forestry tractors and amending Council Directive 74/150/EEC9 regulates exhaust emissions from engines installed in agricultural and forestry tractors with a view to further safeguarding human health and ee the we environment. The current stage of emission limits applicable for type approval of the majority of compression ignition egines is referred to as Stage III A. The directive provides that those limits will be replaced by the more stringent Stage III B limits,wentering into force progressively as of 1st January 2011 with regard to the placing on the market and from 1st January ee 2810 ww as regarrds the type approval for those engines. Stage IV, providing for limit values more stringent than stage III B, will enter into force progressively as of 1st January 2013 as regards the type approval for those engines and as of 1st January 2014 with regard to the placing on the market.";
		
		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
		
		assertWords(result, "(1) Directive 2000/25/EC of the European Parliament and of the Council of 22 May 2000 on action to be taken against the emission of gaseous and particulate pollutants by engines intended to power agricultural or forestry tractors and amending Council Directive 74/150/EEC9 regulates exhaust emissions from engines installed in agricultural and forestry tractors. The current stage of emission limits applicable for type approval of the majority of compression ignition <bi>engines</bi> is referred to as Stage III A. The directive provides that those limits will be replaced by the more stringent Stage III B <bi>limits, entering</bi> into force progressively as of 1st January 2011 with regard to the placing on the market and from 1st January <bi><red>2010</red></bi> <bi>as</bi> <bi>regards</bi> the type approval for those engines. Stage IV, providing for limit values more stringent than stage III B, will enter into force progressively as of 1st January 2013 as regards the type approval for those engines and as of 1st January 2014 with regard to the placing on the market.", "(1) Directive 2000/25/EC of the European Parliament and of the Council of 22 May 2000 on action to be taken against the emission of gaseous and particulate pollutants by engines intended to power agricultural or forestry tractors and amending Council Directive 74/150/EEC9 regulates exhaust emissions from engines installed in agricultural and forestry tractors <bi>with a view to further safeguarding human health and ee the we environment</bi>. The current stage of emission limits applicable for type approval of the majority of compression ignition <bi>egines</bi> is referred to as Stage III A. The directive provides that those limits will be replaced by the more stringent Stage III B <bi>limits,wentering</bi> into force progressively as of 1st January 2011 with regard to the placing on the market and from 1st January <bi>ee</bi> <bi><blue>2<del>0</del><ins>8</ins>10</blue></bi> <bi>ww</bi> <bi>as</bi> <bi>regarrds</bi> the type approval for those engines. Stage IV, providing for limit values more stringent than stage III B, will enter into force progressively as of 1st January 2013 as regards the type approval for those engines and as of 1st January 2014 with regard to the placing on the market.");*/
    }

    @Test
    public void testAnotherComplexDst6_verifyAllRangesAddedToReferences() {
		/*String original =    "<p>Therefore, provision should be made for energy taxation to consist of two components, CO2-related taxation and general energy consumption taxation. In order for energy taxation to adapt to the operation of the Union scheme under Directive 2003/87/EC Member States should be required to explicitly distinguish between those two components. This would also allow distinct treatment of fuels that are biomass or made from biomass.</p>";
		String modified =    "<p>Therefore, provision should be made for energy taxation to consist of two components, CO3-related taxation<sup>1</sup> and general energy consumption taxation. In order for energy taxation to adapt to the operation of the Union scheme under Directive 2003/87/EC Member States should be required to explicitly distinguish between those two components. This would also allow distinct treatment of fuels that are biomass or made from biomass.</p>";
		String verified = "<p>Therefore, should be made for energy taxation to consist of two components, CO3-related taxation<sup>1</sup> and general energy consumption taxation. In order for energy taxation to adapt to the operation of the Unionn scheme under Directive 2003/87/EC Member States should be required to to explicitly distinguish between those two components. This would also allow distinct treatment of fuels that are biomass or made from biomass.</p>";
		
		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
		
		assertWords(result, "<p>Therefore, <red>provision</red> should be made for energy taxation to consist of two components, <bi>CO2-related</bi> taxation and general energy consumption taxation. In order for energy taxation to adapt to the operation of the <red>Union</red> scheme under Directive 2003/87/EC Member States should be required to explicitly distinguish between those two components. This would also allow distinct treatment of fuels that are biomass or made from biomass.</p>", "<p>Therefore, <del>provision</del> should be made for energy taxation to consist of two components, <bi>CO3-related</bi> taxation<bi><sup>1</sup></bi> and general energy consumption taxation. In order for energy taxation to adapt to the operation of the <blue>Union<ins>n</ins></blue> scheme under Directive 2003/87/EC Member States should be required to <ins>to</ins> explicitly distinguish between those two components. This would also allow distinct treatment of fuels that are biomass or made from biomass.</p>");*/
    }

    @Test
    public void testAnotherComplexDst7() {
		/*String original =    "<p>Das Prüfverfahren gelangt nur zur Anwendung beim Erlass von:</p>";
		String modified =    "<p>Prüfverfahren gelangt grundsätzlich beim Erlass von allgemeinen Durchführungsmaßnahmen zur Anwendung, sofern einheitliche Bedingungen erforderlich sind.</p>";
		String verified = "<p>Prüfverfahren gelangt grundsätzlich beim Erlass von allgemeinen Durchführungsmaßnahmen zur Anwendung, sofern einheitliche Bedingungen erforderlich sind.</p>";
		
		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
		
		assertWords(result, "<p><bi>Das</bi> Prüfverfahren gelangt <bi>nur zur Anwendung</bi> beim Erlass von<bi>:</bi></p>", "<p>Prüfverfahren gelangt <bi>grundsätzlich</bi> beim Erlass von <bi>allgemeinen Durchführungsmaßnahmen zur Anwendung, sofern einheitliche Bedingungen erforderlich sind.</bi></p>");*/
    }

    @Test
    public void testWeirdShift() {
		/*String original =    "This is a test from 2010 the days of lalaa";
		String modified =    "This is a test from te 2010 xx the days of lalaa";
		String verified = "This is a test from 2011 the days of lalaa";
		
		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
		
		assertWords(result, "This is a test from <bi><red>2010</red></bi> the days of lalaa", "This is a test from <bi><blue><del>te 2010 xx</del><ins>2011</ins></blue></bi> the days of lalaa");*/
    }

    @Test
    public void testSubSuperScript() {
		/*String original =    "<p>This is a test from 2010</p>";
		String modified =    "<p>This is a test from 2010<sup>1</sup> and next</p>";
		String verified = "<p>This is a test from 2010<sup>12</sup> and next</p>";
		
 		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
 		
 		assertWords(result, "<p>This is a test from 2010</p>", "<p>This is a test from 2010<bi><sup><blue>1<ins>2</ins></blue></sup> and next</bi></p>");*/
    }

    @Test
    public void testSubSuperScriptTemp() {
		/*String original =    "<p>The objective of this Directive is to approximate rules on criminal law in the Member States in the area of attacks against information systems, and improve cooperation between judicial and other competent authorities, including the police and other specialised law enforcement services of the Member States.</p>";
		String modified =    "<p>The objective of this Directive is to approximate rules on criminal law in the Member States in the area of attacks against information systems, and improve cooperation between judicial and other competent authorities, including the police and other specialised law enforcement services of the Member States, in fully accordance with the principle of divxision of powers.</p>";
		String verified = "<p>The objective of this Directive is to approximate rules on criminal law in the Member States in the area of attacks agaxinst infoxrmation sysxtems, and improve cooperation between judicial and other competent authorities, including the police and other specialised law enforcement services of the Member States, in fully accordance with the principle of divxision of powers.</p>";
		
		verified = "<p>The objective of this Directive is to approximate rules on criminal law in the Member States in the area of attacks informxation against sxystems, and improve cooperation between judicial and other competent authorities, including the police and other specialised law enforcement services of the Member States, in fully accordance with the principle of divxision of powers.</p>";
		
		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
 		
		assertWords(result, "<p>The objective of this Directive is to approximate rules on criminal law in the Member States in the area of attacks against <red>information</red> <red>systems</red>, and improve cooperation between judicial and other competent authorities, including the police and other specialised law enforcement services of the Member States.</p>", "<p>The objective of this Directive is to approximate rules on criminal law in the Member States in the area of attacks <ins>informxation</ins> against <del>information</del> <del>systems</del><ins>sxystems</ins>, and improve cooperation between judicial and other competent authorities, including the police and other specialised law enforcement services of the Member States<bi>, in fully accordance with the principle of divxision of powers</bi>.</p>");*/
    }

//	@Test
//	public void testSubSuperScriptNew() {
//		String original =    "<p>This is a test from 2010</p>";
//		String modified =    "<p>This is a test from 2010<sup>1</sup> and next</p>";
//		String verified = "<p>This is a test from 2010<sup>12</sup> and next</p>";
//		
// 		String[] result = ComplexDiff.diff3(original, modified, verified);
// 		
//		Word last = complexDiff.original.getLast();
//		StringBuilder builder = new StringBuilder();
// 		for (Word word : complexDiff.original) {
// 			builder.append(word.print(WORD_PRINTER_ORIGINAL, last.equals(word)));
// 		}
// 		String or = builder.toString().replaceAll("</bi><bi>", "");
// 		
// 		last = complexDiff.revised.getLast();
// 		StringBuilder builder2 = new StringBuilder();
// 		for (Word word : complexDiff.revised) {
// 			builder2.append(word.print(WORD_PRINTER_REVISED, last.equals(word)));
// 		}
// 		String rv = builder2.toString().replaceAll("</bi><bi>", "");
// 		
// 		System.out.println(builder.toString());
// 		System.out.println(or);
// 		System.out.println(builder2.toString());
// 		System.out.println(rv);
//		
// 		Assert.assertEquals("<p>This is a test from 2010</p>", or);
//		Assert.assertEquals("<p>This is a test from 2010<bi><sup><blue>1<ins>2</ins></blue></sup> and next</bi></p>", rv);
//	}

//	@Test
//	public void testSubSuperScriptNew2() {
//		String original =    "A B C D";
//		String modified =    "A B C E F G";
//		String verified = "A B C E H G";
//		
//		new ArrayList<String>();
//		
// 		String[] result = ComplexDiff.diff3(original, modified, verified);
// 		
//		Word last = complexDiff.original.getLast();
//		StringBuilder builder = new StringBuilder();
// 		for (Word word : complexDiff.original) {
// 			builder.append(word.print(WORD_PRINTER_ORIGINAL, last.equals(word)));
// 		}
// 		
// 		last = complexDiff.revised.getLast();
// 		StringBuilder builder2 = new StringBuilder();
// 		for (Word word : complexDiff.revised) {
// 			builder2.append(word.print(WORD_PRINTER_REVISED, last.equals(word)));
// 		}
// 		
// 		System.out.println(builder.toString());
// 		System.out.println(builder2.toString());
//		Assert.assertEquals("", builder.toString());
//		Assert.assertEquals("", builder2);
//	}

    @Test
    public void testSubSuperScript2() {
        String original = "<p>Member States may amend.</p>";
        String modified = "<p>Member States may amend.</p>";
        String verified = "<p>Member States may<sup>5</sup> amend.</p>";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "<p>Member States may amend.</p>", "<p>Member States may<sup><ins>5</ins></sup> amend.</p>");
    }

    @Test
    public void testHTML() {
		/*String original =    "<p>Member States.</p><p>May amend.</p>";
		String modified =    "<p>Member States.</p><p>May amend.</p>";
		String verified = "<p>Member States.</p><p>May amend.</p>";
		
 		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
		
 		assertWords(result, "<p>Member States may amend.</p>", "<p>Member States may<sup><ins>5</ins></sup> amend.</p>");*/
    }

    @Test
    public void testNestedHTML() {
		/*String original =    "<p>Member<span>sss </span>States.</p><p>May <br/>amend.</p>";
		String modified =    "<p>Member<p>sss </p>States.</p><p>May amend.</p>";
		String verified = "<p>Member<p>sss </p>States.</p><p>May amend.</p>";
		
		//TODO: Diff on html nodes, to figure out possible changes 
		
 		//String[] result = ComplexDiff.diff3(original, modified, verified);
 		
 		original =    "<p>Member<span>sss </span>States.</p><p>May <br/>amend.</p>";
		modified =    "<p>Member<span>sss </span>States.</p><p>May.</p>";
		verified = "<p>Member<span>sss </span>States.</p><p>May test.</p>";
		
 		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
		
 		assertWords(result, "<p>Member States may amend.</p>", "<p>Member States may<sup><ins>5</ins></sup> amend.</p>");*/
    }

    @Test
    // Weird split of words
    public void testSubSuperScript3() {
		/*String original =    "<p>maniÃ¨re Ã  mieux;</p>";
		String modified =    "<p>maniÃ¨re Ã  mieux;</p>";
		String verified = "<p>manssiÃ¨re<sup>12</sup> Ã  mieux;</p>";
		
 		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
		
 		assertWords(result, "<p><red>maniÃ¨re</red> Ã  mieux;</p>", "<p><blue>man<ins>ss</ins>iÃ¨re<ins><sup>12</sup></ins></blue> Ã  mieux;</p>");*/
    }

    @Test
    public void testSubSuperScript4() {
		/*String original =    "<p>First programme should.</p>";
		String modified =    "<p>First programme should.</p>";
		String verified = "<p>First<sup>1</sup> prosgramme<sup>2</sup> should.</p>";
		
 		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
		
 		assertWords(result, "<p>First <red>programme</red> should.</p>", "<p>First<blue><ins><sup>1</sup> </ins>pro<ins>s</ins>gramme<ins><sup>2</sup></ins></blue> should.</p>");*/
    }

    @Test
    //@Ignore
    public void testSubSuperScript5() {
		/*String original =    "<p>Programme should.</p>";
		String modified =    "<p>Programme in the world is should.</p>";
		String verified = "<p>Programme in<sup>1</sup> thse<sup>2</sup> world is should.</p>";
		
 		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
 		
 		assertWords(result, "<p>Programme should.</p>", "<p>Programme <bi>in<ins><sup>1</sup></ins><blue><del>the</del>thse<ins><sup>2</sup></ins></blue> world is</bi> should.</p>");*/
    }

    @Test
    public void testHtml3() {
		/*String original =    "Texts adopted, P7_TA(2010)0173.";
		String modified =    "";
		String verified = "<p>Texts adopted, P7_TA(2010)0173. g new mod</p>";
		
 		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
		
 		assertWords(result, "<bi>Texts adopted, P7_TA(2010)0173.</bi>", "<p><ins>Texts adopted, P7_TA(2010)0173. g new mod</ins></p>");*/
    }

    @Test
    public void testInsertDelimiters() {
        String original = "<p>This is. Yes.</p>";
        String modified = "<p>This is. Yes123.</p>";
        String verified = "<p>This is. Yes123. This .</p>";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);
        //DiffResult complexDiff2 = ComplexDiff.diff2(original, modified, verified);

        assertWords(result, "<p>This is. <bi>Yes</bi>.</p>", "<p>This is. <bi>Yes123</bi>. <ins>This .</ins></p>");
    }

    @Test
    public void testChangeDelimiters() {
		/*String original =    "<p>This is. Yes.</p>";
		String modified =    "<p>This is. Yes123. Ja.</p>";
		String verified = "<p>This iss. Yes123456 ssqdqsd qsdqsd. Ja.</p>";
		
 		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
 		
 		assertWords(result, "<p>This <red>is</red>. <bi>Yes</bi>.</p>", "<p>This <blue>is<ins>s</ins></blue>. <bi><blue>Yes123<ins>456 ssqdqsd qsdqsd</ins></blue></bi>. <bi>Ja.</bi></p>");*/
    }

    @Test
    public void testRevertToOriginal() {
		/*String original =    "Three yellow cars";
		String modified =    "Three green bicycle";
		String verified = "Three yellow bicycles";
		
 		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
 		
 		assertWords(result, "Three <bi>yellow cars</bi>", "Three <bi><blue><del>green</del><ins>yellow</ins> bicycle<ins>s</ins></blue>");*/
    }

    @Test
    public void testCapitalization() {
        String original = "Three";
        String modified = "Three";
        String verified = "three";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "Three", "three");
    }

    @Test
    public void testParagraphs() {
		/*String original = "<p>The measures to be taken to achieve the objectives set out in Article 2 may include the following initiatives organised at Union, national, regional or local level linked to the objectives of the European Year:</p>";
		String modified = "<p>The measures to be taken<sup>3</sup> to achieve the objectives set out in Article 2 may include the following initiatives organised at The measures to be taken to achieve the objectives set out in Article 2 may include the following initiatives organised at Union, national, regional or local level linked to the objectives of the European<sup>1</sup> Year: Union, national, regional or local level linked to the objectives of the European Year: The measures to be taken to achieve the objectives set out in Article 2 may include the following<sup>2</sup> initiatives organised at Union, national, regional or local level linked to the objectives of the European Year:</p>";
		String verified = "<p>The measures to be taken to achieve achieve the objectives set out in Article 2 may include the following initiatives organised at The meaopsures to be taken to achieve the objectives set out in Article 2 may include the following inititives organised at Union, national, or local level linked to the objectives of the European<sup>1</sup> Year: Union, national, regional or local level linked to the objectives of the European Year: The measures to be taken to achieve the objectives set out in Article 2 may include the following<sup>2</sup> initiatives organised at Union, national, regional or local level linked to the objectives of the European<sup>3</sup> Year:</p>";
		
		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
 		
		assertWords(result, "<p>The measures to be taken to achieve the objectives set out in Article 2 may include the following initiatives organised at Union, national, <red>regional</red> or local level linked to the objectives of the European Year:</p>", "<p>The measures to be taken<bi><sup><del>3</del></sup></bi> to achieve <ins>achieve</ins> the objectives set out in Article 2 may include the following initiatives organised at <bi>The <blue>mea<ins>op</ins>sures</blue> to be taken to achieve the objectives set out in Article 2 may include the following <blue>initi<del>a</del>tives</blue> organised at</bi> Union, national, <del>regional</del> or local level linked to the objectives of the European<bi><sup>1</sup></bi> Year: <bi>Union, national, regional or local level linked to the objectives of the European Year: The measures to be taken to achieve the objectives set out in Article 2 may include the following<sup>2</sup> initiatives organised at Union, national, regional or local level linked to the objectives of the European<sup><ins>3</ins></sup> Year:</bi></p>");*/
    }

    @Test
    public void testCleanupDeletion() {
		/*String original = "gemeinsame Straftatbestände für den rechtswidrigen";
		String modified = "gemeinsame Straftaestände für dyn rechtswidrigen";
		String verified = "gemeinsame rechtswidrigen dyn";
		
		String[] result = DiffUtils.threeWayDiff(original, modified, verified);
 		
		assertWords(result, "gemeinsame <bi>Straftatbestände</bi> <bi><red>für</red></bi> <bi>den</bi> rechtswidrigen", "gemeinsame <bi><del>Straftaestände</bi> <bi>für</bi> <bi>dyn</del></bi> rechtswidrigen <ins>dyn</ins>");*/
    }

    @Test
    public void testPrint() {
        String original = "<p>one</p>";
        String modified = "<p>one</p>";
        String verified = "<p>one</p><p>two</p><p>three</p>";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        assertWords(result, "<p>one</p>", "<p>one</p><p><ins>two</ins></p><p><ins>three</ins></p>");
    }


    // - Remember the rule - if last unchanged word before the changed string is equal to the last changed word, swap their places. Example below.
    // - Only <bi> markings below

    @Test
    public void newExcitingTest() {
        String original = "exporter shall notify the designated authority.";
        String modified = "exporter shall notify, unless the exported does not need, the designated authority.";
        String verified = "exporter shall notify, unless the exported does not need, the designated authority.";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);

        System.out.println(result[0]);
        System.out.println(result[1]);

//		assertWords(result, "exporter shall notify <bi>the</bi> designated authority.", "exporter shall notify<bi>, unless the exported does not need, the</bi> designated authority.");
//		assertWords(result, "exporter shall notify the designated authority.", "exporter shall notify<bi>, unless the exported does not need,</bi> the designated authority.");	
        // is now assertWords(result, "exporter shall notify the designated authority.", "exporter shall notify<bi>, unless</bi> the <bi>exported does not need, the</bi> designated authority.");

    }

    @Test
    public void newExcitingTestX() {
        String original = "This is a test";
        String modified = "This is test";
        String verified = "This isss sa tessst";

        String[] result = DiffUtils.threeWayDiff(original, modified, verified);
    }

    @Test
    public void testInput() {
		/*String original =    "";
		String modified =    "";
		String verified = "";
		
 		String[] complexDiff = DiffUtils.threeWayDiff(original, modified, verified);
 		
 		Assert.assertEquals("", complexDiff[0]);
		Assert.assertEquals("", complexDiff[1]);
		
		original =    "Test";
		modified =    "";
		verified = "";
		
 		complexDiff = DiffUtils.threeWayDiff(original, modified, verified);
 		
 		Assert.assertEquals("<bi>Test</bi>", complexDiff[0]);
		Assert.assertEquals("", complexDiff[1]);
		
		original =    "";
		modified =    "Test";
		verified = "";
		
 		complexDiff = DiffUtils.threeWayDiff(original, modified, verified);
 		
 		Assert.assertEquals("", complexDiff[0]);
		Assert.assertEquals("<bi><del>Test</del></bi>", complexDiff[1]);
		
		original =    "";
		modified =    "";
		verified = "Test";
		
 		complexDiff = DiffUtils.threeWayDiff(original, modified, verified);
 		
 		Assert.assertEquals("", complexDiff[0]);
		Assert.assertEquals("<ins>Test</ins>", complexDiff[1]);*/
    }

//	@Test
//	public void testMultiplePunctuationsAtEndOfWord() {
//		String original = "<p>The Office should offer a forum that brings together public authorities and the private sector, ensuring the collection, analysis and dissemination of relevant objective, comparable and reliable data regarding the value of intellectual property rights and the infringements of those rights, the development of best practices and strategies to protect intellectual property rights, and raising public awareness of the impacts of intellectual property rights infringements. Furthermore, the Office should fulfil additional tasks, such as to improve the understanding of the value of intellectual property rights, enhance the expertise of persons involved in the enforcement of intellectual property rights by appropriate training measures, increase knowledge on techniques to prevent counterfeiting, and improve cooperation with third countries and international organisations.</p>%%%";
//		String revised = "<p>The Office should offer a forum that brings together public authorities and the private sector, ensuring the collection, analysis and dissemination of relevant objective, comparable and reliable data regarding the value of intellectual property rights and the infringements of those rights, identifying and promoting best practices to enforce intellectual property rights, and raising public awareness of the impacts of intellectual property rights infringements. Furthermore, the Office should fulfil additional tasks, such as to improve the understanding of the value of intellectual property rights, exchange of information on new competitive business models expanding the legal offer of cultural and creative content, enhance the expertise of persons involved in the enforcement of intellectual property rights by appropriate training measures, increase knowledge on techniques to prevent infringement of intellectual property rights, and improve cooperation with third countries and international organisations.</p>%%%";
//		String overrideRevised = "<p>The Office should offer a forum that brings together public authorities and the private sector, ensuring the collection, analysis and dissemination of relevant objective, comparable and reliable data regarding the value of intellectual property rights and the infringements of those rights, identifying and promoting best practices to enforce intellectual property rights, and raising public awareness of the impacts of intellectual property rights infringements. Furthermore, the Office should fulfil additional tasks, such as to improve the understanding of the value of intellectual property rights, exchange of information on new competitive business models expanding the legal offer of cultural and creative content, enhance the expertise of persons involved in the enforcement of intellectual property rights by appropriate training measures, increase knowledge on techniques to prevent infringement of intellectual property rights, and improve cooperation with third countries and international organisations...</p>%%%";
//		
//		String[] result = ComplexDiff.diff3(original, modified, verified);
//		
//		String originalChangeTemplate = "<span class=\"highlight-diff\">{0}</span>";
//		
//		// style for complex diffing
//		String originalComplexChangeTemplate = "<span class=\"highlight-red\">{0}</span>";
//		String complexInsertTemplate         = "<span class=\"highlight-ins\">{0}</span>";
//		String complexDeleteTemplate         = "<span class=\"highlight-del\">{0}</span>";
//		String complexChangeTemplate         = "{0}";
//		
//		String coloredOriginal = DiffResultFormatter.printOriginal(complexDiff, originalChangeTemplate, originalComplexChangeTemplate);
//		String finalOriginal = DiffResultFormatter.stringContainersToStringOriginal(complexDiff, "<span style=\"font-weight: bold;font-style: italic;\">{0}</span>", "<span style=\"font-weight: bold;font-style: italic;\">{0}</span>");
//		String coloredAmendment = DiffResultFormatter.printRevised(complexDiff, originalChangeTemplate, complexInsertTemplate, complexDeleteTemplate, complexChangeTemplate);
//		String finalAmendment = DiffResultFormatter.stringContainersToStringRevisedFinal(complexDiff);
//		
//		System.out.println(coloredOriginal);
//		System.out.println(finalOriginal);
//		System.out.println(coloredAmendment);
//		System.out.println(finalAmendment);
//	}

//	@Test
//	public void testMultiplePunctuationsAtMiddleOfWord() {
//		String original = "Test";
//		String revised = "Test";
//		String overrideRevised = "T...est";
//		
//		String[] result = ComplexDiff.diff3(original, modified, overrideRevised);
//		
//		String originalChangeTemplate = "<span class=\"highlight-diff\">{0}</span>";
//		
//		// style for complex diffing
//		String originalComplexChangeTemplate = "<span class=\"highlight-red\">{0}</span>";
//		String complexInsertTemplate         = "<span class=\"highlight-ins\">{0}</span>";
//		String complexDeleteTemplate         = "<span class=\"highlight-del\">{0}</span>";
//		String complexChangeTemplate         = "{0}";
//		
//		String coloredOriginal = DiffResultFormatter.printOriginal(complexDiff, originalChangeTemplate, originalComplexChangeTemplate);
//		String finalOriginal = DiffResultFormatter.stringContainersToStringOriginal(complexDiff, "<span style=\"font-weight: bold;font-style: italic;\">{0}</span>", "<span style=\"font-weight: bold;font-style: italic;\">{0}</span>");
//		String coloredAmendment = DiffResultFormatter.printRevised(complexDiff, originalChangeTemplate, complexInsertTemplate, complexDeleteTemplate, complexChangeTemplate);
//		String finalAmendment = DiffResultFormatter.stringContainersToStringRevisedFinal(complexDiff);
//		
//		System.out.println(coloredOriginal);
//		System.out.println(finalOriginal);
//		System.out.println(coloredAmendment);
//		System.out.println(finalAmendment);
//	}

    @Test
    public void testComplex() {
		/*String original = "relatives à l'étiquetage facultatif de la viande bovine";
		String revised = "relatives au système d'étiquetage facultatif de la viande bovine";
		String overrideRevised = "relatives au système d'étiquetage facultatif de la viande bovine";
		
		String[] result = DiffUtils.threeWayDiff(original, revised, overrideRevised);
		
		assertWords(result, "relatives <diff>à l'étiquetage</diff> facultatif de la viande bovine", "relatives <diff>au système d'étiquetage</diff> facultatif de la viande bovine");*/
    }

    @Test
    public void testComplex2() {
		/*String original = "des bovins et l'étiquetage facultatif";
		String revised = "des bovins et le système d'étiquetage facultatif";
		String overrideRevised = "des bovins et le système d'étiquetage facultatif";
		
		String[] result = DiffUtils.threeWayDiff(original, revised, overrideRevised);
		
		assertWords(result, "des bovins et <diff>l'étiquetage</diff> facultatif", "des bovins et <diff>le système d'étiquetage</diff> facultatif");*/
    }

    @Test
    public void testComplex3() {
		/*String original = "to make EID compulsory on their territory only when they deem it appropriate, after considering all those factors.";
		String revised = "to introduce and  implement mandatory EID on their territory on a step-by-step basis.";
		String overrideRevised = "to introduce and  implement mandatory EID on their territory on a step-by-step basis.";
		
		String[] result = DiffUtils.threeWayDiff(original, revised, overrideRevised);
		
		assertWords(result, "to <bi>make EID compulsory</bi> on their territory <bi>only when they deem it appropriate, after considering all those factors</bi>.", "to <diff>introduce and implement mandatory</diff> <diff>EID</diff> on their territory <diff>on a step-by-step basis</diff>.");*/
    }

    @Test
    public void testComplex4() {
        String original = "<p>faits ne sont pas sans gravité.</p>";
        String revised = "<p>faits x.</p><p>Y ne E pas été informé complètement ni en temps utile de la vulnérabilité du système d'information.</p>";
        String overrideRevised = "<p>faits x.</p><p>Y ne E pas été informé complètement ni en temps utile de la vulnérabilité du système d'information.</p>";

        String[] result = DiffUtils.threeWayDiff(original, revised, overrideRevised);

        //printResult(complexDiff);
    }

    @Test
    public void testComplex7() {
        String original = "<P>les faits ne sont pas sans gravité.</P>";
        String revised = "<p>les faits relèvent d'une intention criminelle ou délictueuse et ayant des <br/>conséquences graves et dommages à l'existence ou au fonctionnement du ou des systèmes d'information.</p><p>Le comportement visé au paragraphe 1 ne doit être érigé en infraction pénale qu'en cas d'infraction à une mesure de sécurité et  si l'opérateur ou le fournisseur du système n'a pas été informé complètement ni en temps utile de la vulnérabilité du système d'information.</p>";
        String overrideRevised = "<p>les faits relèvent d'une intention criminelle ou délictueuse et ayant des conséquences graves et dommages à l'existence <del>ou</del> au fonctionnement du ou des systèmes d'information.</p>";

        String[] result = DiffUtils.threeWayDiff(original, revised, overrideRevised);

        //printResult(complexDiff);
    }

    @Test
    public void testBreaking() {
        String original = "C'est n'est pas une pipe!";
        String revised = "C'esst n'est pas une pipe!";
        String overrideRevised = "C'est n'esst pas une pipe!";

        String[] result = DiffUtils.threeWayDiff(original, revised, overrideRevised);
    }

    @Test
    public void testComplex7a() {
        String original = "<p>les faits ne sont pas sans gravité.</p>";
        String revised = "<p>les faits relèvent d'une intention criminelle ou délictueuse et ayant des conséquences graves et dommages à l'existence ou au fonctionnement du ou des systèmes d'information. Le comportement visé au paragraphe 1 ne doit être érigé en infraction pénale qu'en cas d'infraction à une mesure de sécurité et  si l'opérateur ou le fournisseur du système n'a pas été informé complètement ni en temps utile de la vulnérabilité du système d'information.</p>";
        String overrideRevised = "<p>les faits relèvent d'une intention criminelle ou délictueuse et ayant des conséquences graves et dommages à l'existence ou au fonctionnement du ou des systèmes d'information.</p>";

        String[] result = DiffUtils.threeWayDiff(original, revised, overrideRevised);
    }

    @Test
    public void testComplex7b() {
        String original = "<p>les faits ne sont pas sans gravité.</p>";
        String revised = "<p>les faits relèvent. Le paragraphe 1 ne doit du système n'a pas été informé complètement ni en temps utile de la vulnérabilité du système d'information.</p>";
        String overrideRevised = "<p>les faits relèvent. Le paragraphe 1 ne doit du système n'a pas été informé complètement ni en temps utile de la vulnérabilité du système d'information.</p>";

        String[] result = DiffUtils.threeWayDiff(original, revised, overrideRevised);
    }

    @Test
    public void testComplex5() {
        String original = "Die Mitgliedstaaten treffen die erforderlichen Maßnahmen, um sicherzustellen, dass das vorsätzliche, mit technischen Hilfsmitteln bewirkte unbefugte Abfangen nichtöffentlicher Computerdatenübermittlungen an ein Informationssystem, aus einem Informationssystem oder innerhalb eines Informationssystems einschließlich elektromagnetischer Abstrahlungen aus einem Informationssystem, das Träger solcher Computerdaten ist, unter Strafe gestellt wird.";
        String revised = "Die Mitgliedstaaten treffen die erforderlichen Maßnahmen, um sicherzustellen, dass das vorsätzliche, mit technischen Hilfsmitteln bewirkte unbefugte Abfangen nichtöffentlicher Computerdatenübermittlungen an ein Informationssystem, aus einem Informationssystem oder innerhalb eines Informationssystems einschließlich elektromagnetischer Abstrahlungen aus einem Informationssystem, das Träger solcher Computerdaten ist, zumindest dann unter Strafe gestellt wird, wenn kein leichter Fall vorliegt. Das Abfangen kann auch eine Aufzeichnung umfassen. Zu technischen Hilfsmitteln gehören technische Vorrichtungen, die an Übertragungsleitungen angebracht werden, sowie Vorrichtungen zur Sammlung und Aufzeichnung kabelloser Kommunikation, einschließlich der Benutzung von Software, Passwörtern und Codes.";
        String overrideRevised = "Die Mitgliedstaaten treffen die erforderlichen Maßnahmen, um sicherzustellen, dass das vorsätzliche, mit technischen Hilfsmitteln bewirkte unbefugte Abfangen nichtöffentlicher Computerdatenübermittlungen an ein Informationssystem, aus einem Informationssystem oder innerhalb eines Informationssystems einschließlich elektromagnetischer Abstrahlungen aus einem Informationssystem, das Träger solcher Computerdaten ist, zumindest dann unter Strafe gestellt wird, wenn kein leichter Fall vorliegt. Das Abfangen kann auch eine Aufzeichnung betreffen. Die Datenübermittlung umfasst die Zeitspanne des Transports von Daten, leitungsgebunden oder per Funk, zwischen dem Absenden beim Sender und dem Ankommen beim Empfänger. Zu den technischen Hilfsmitteln gehören technische Vorrichtungen, die an Übertragungsleitungen angebracht werden, sowie Vorrichtungen zur Sammlung und Aufzeichnung kabelloser Kommunikation, einschließlich von Software, Passwörtern und Codes.";

        String[] result = DiffUtils.threeWayDiff(original, revised, overrideRevised);
    }

    @Test
    public void testComplex6() {
        String original = "Statele membre adoptă măsurile necesare pentru a garanta că faptele menționate la articolele 3-6 sunt pasibile de sancțiuni penale privative de libertate având o durată maximă de cel puțin cinci ani în cazul în care sunt comise prin utilizarea unui instrument conceput pentru a lansa atacuri care afectează o număr semnificativ de sisteme informatice sau atacuri care provoacă pagube considerabile, precum întreruperea serviciilor aferente sistemului, costuri financiare sau pierderi de date cu caracter personal.";
        String revised = "Statele membre adoptă măsurile necesare pentru a garanta că faptele menționate la articolele 3-6 sunt pasibile de sancțiuni penale privative de libertate având o durată maximă de cel puțin cinci ani în cazul în care sunt comise prin utilizarea unui instrument conceput pentru a lansa atacuri care afectează o număr semnificativ de sisteme informatice sau atacuri care provoacă pagube considerabile, precum întreruperea serviciilor aferente sistemului, costuri financiare sau pierderi de date cu caracter personal.";
        String overrideRevised = "Statele membre adoptă măsurile necesare pentru a garanta că faptele sunt pasibile de sancțiuni penale privative de libertate având o durată maximă de cel puțin cinci ani în cazul în care sunt comise prin utilizarea unui instrument conceput pentru a lansa atacuri care afectează o număr semnificativ de sisteme informatice sau atacuri care provoacă pagube considerabile, precum întreruperea serviciilor aferente sistemului, costuri financiare, pierderi de date sensibile sau cu caracter personal, afectarea unor sisteme informatice ale unor infrastructuri critice.";

        String[] result = DiffUtils.threeWayDiff(original, revised, overrideRevised);
    }

    @Test
    public void testAttributes() {
        String original = "<table id=\"1\" border=\"1\"><tr id=\"2\"><td id=\"3\">test</td></tr></table>";
        String revised = "<table id=\"1\" border=\"1\"><tr id=\"2\"><td id=\"3\">test2</td></tr></table>";
        String overrideRevised = "<table id=\"1\" border=\"1\"><tr id=\"2\"><td id=\"3\">test2</td></tr></table>";

        String[] result = DiffUtils.threeWayDiff(original, revised, overrideRevised);

        String expectedOriginal = "<table id=\"1\" border=\"1\"><tr id=\"2\"><td id=\"3\"><bi>test</bi></td></tr></table>";
        String expectedRevised = "<table id=\"1\" border=\"1\"><tr id=\"2\"><td id=\"3\"><bi>test2</bi></td></tr></table>";

        assertWords(result, expectedOriginal, expectedRevised);

    }


    @Test
    public void testCleanup() {
        String original = "This is a test.";
        String revised = "This iss a tests.";
        String overrideRevised = "This iss a testss.";

        String[] result = DiffUtils.threeWayDiff(original, revised, overrideRevised);

        assertWords(result, "This <bi>is a test</bi>.", "This <bi>iss a</bi> <blue>tests<ins>s</ins></blue>.");
    }

    @Test
    public void testSubSupScripts() {
		/*String original =        "one two three four";
		String revised =         "one<sub>one</sub> two<sub>two</sub> three four<sub>three</sub>";
		String overrideRevised = "one<sub>one</sub> two<sub>two</sub> three four<sub>three</sub>";
		
		String[] result = DiffUtils.threeWayDiff(original, revised, overrideRevised);
		
		assertWords(result, "one two three four", "<bi>one</bi><sub>one</sub> <bi>two</bi><sub>two</sub> three four<sub><bi>three</bi></sub>");*/
    }

    @Test
    public void testCleanup2() {
		/*String original = "This value is a test.";
		String revised = "Thisss a tests.";
		String overrideRevised = "Thisss a tests.";
		
		String[] result = DiffUtils.threeWayDiff(original, revised, overrideRevised);
		
		assertWords(result, "<bi>This value is a test</bi>.", "<diff>Thisss</diff> <diff>a</diff> <diff>tests</diff>.");*/
    }

    private void assertWords(String[] result, String expectedOriginal, String expectedRevised) {
        Assert.assertEquals(expectedOriginal, result[0]);
        Assert.assertEquals(expectedRevised, result[1]);
    }

}
