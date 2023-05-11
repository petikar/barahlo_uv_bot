package petikar.barahlo_uv_bot;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class NormalizeUtilsTest {
    @Test
    public void normalizeOneWordTest() {

        //given
        String given = "люди";
        String result = "человек";

        //when
        String returnString = NormalizeUtils.normalize(given);

        //then
        assertThat(returnString).isNotNull();
        assertThat(returnString).isEqualTo(result);
    }

    @Test
    public void normalizeManyWordsTest() {

        //given
        String given = "люди яблоки";
        String result = "человек яблоко";

        //when
        String returnString = NormalizeUtils.normalize(given);

        //then
        assertThat(returnString).isNotNull();
        assertThat(returnString).isEqualTo(result);
    }

    @Test
    public void normalizeManyWordsWithInfinitiveTest() {

        //given
        String given = "Продаю килограмм яблок";
        String result = "килограмм яблоко";

        //when
        String returnString = NormalizeUtils.normalize(given);

        //then
        assertThat(returnString).isNotNull();
        assertThat(returnString).isEqualTo(result);
    }

/*    @Test
    public void delStopWords1Test() {

        //given
        String given = "Продам молоко";
        String result = "молоко";

        //when
        String returnString = NormalizeUtils.delStopWords(given);

        //then
        assertThat(returnString).isNotNull();
        assertThat(returnString).isEqualTo(result);
    }

    @Test
    public void delStopWords2Test() {

        //given
        String given = "продам, отдам качелю";
        String result = "качелю";

        //when
        String returnString = NormalizeUtils.delStopWords(given);

        //then
        assertThat(returnString).isNotNull();
        assertThat(returnString).isEqualTo(result);
    }*/

}