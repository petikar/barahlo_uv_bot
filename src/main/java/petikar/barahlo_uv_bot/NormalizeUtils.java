package petikar.barahlo_uv_bot;

import com.github.demidko.aot.MorphologyTag;
import com.github.demidko.aot.WordformMeaning;

import java.util.Arrays;
import java.util.List;

import static com.github.demidko.aot.WordformMeaning.lookupForMeanings;

public class NormalizeUtils {
    public static String normalize(String text) {

        text = text.replaceAll("\\p{P}", " ");

        List<String> words = Arrays.asList(text.toLowerCase().split("\\s+"));

        StringBuilder result = new StringBuilder();

        for (String word : words) {

            if (lookupForMeanings(word).size() != 0) {
                WordformMeaning startForm = lookupForMeanings(word).get(0).getLemma();

                if (!startForm.getMorphology().contains(MorphologyTag.Infinitive)) {
                    result.append(startForm).append(' ');
                }
            } else {
                result.append(word).append(' ');
            }

        }

        String resultString = String.valueOf(result);
        return resultString.trim();
    }

/*    private static String delStopWords(String text) {
        text = text.replaceAll("\\p{P}", " ");
        text = text.toLowerCase();
        text = text.replaceAll("продам","")
                .replaceAll("отдам","")
                .replaceAll("новый","")
                .replaceAll("новая","")
                .replaceAll("новое","")
        ;

        return text.trim();
    }*/


}
