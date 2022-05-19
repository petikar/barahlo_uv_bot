package petikar.barahlo_uv_bot;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DateConverterTest {

    @Test
    public void dateToIntTest() {

        //given
        LocalDateTime dateGiven = LocalDateTime.of(2022, 05, 13, 15, 48, 5);
        int dateIntGiven = 1652431685;

        //when
        Integer dateInt = DateConverter.dateToInteger(dateGiven);

        //then
        assertThat(dateInt).isNotNull();
        assertThat(dateInt).isEqualTo(dateIntGiven);
    }

    @Test
    public void intToDateTest() {

        //given
        LocalDateTime dateGiven = LocalDateTime.of(2022, 05, 13, 15, 48, 5);
        int dateInt = 1652431685;

        //when
        LocalDateTime date = DateConverter.intToDate(dateInt);

        //then
        assertThat(date).isNotNull();
        assertThat(date).isEqualTo(dateGiven);
    }
}